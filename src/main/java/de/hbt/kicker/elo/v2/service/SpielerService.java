package de.hbt.kicker.elo.v2.service;

import static de.hbt.kicker.elo.KickerGlobalSettings.ELO_START;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.time.DateUtils;

import de.hbt.kicker.elo.v2.model.EloPunktzahl;
import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.model.Spieler;
import de.hbt.kicker.elo.v2.persistence.EloPersistence;

public class SpielerService {

	private EloPersistence eloPersistence;

	public Spieler findSpielerByName(String spielerName) {
		return eloPersistence.findSpielerByName(spielerName);
	}

	public List<Spieler> findAllVisibleSpieler() {
		return eloPersistence.findAll(Spieler.class, 10000, " where m.visible = true order by name");
	}

	public List<RanglistenEintrag> getSpielerRangliste() {
		List<Spieler> alleSpieler = eloPersistence.createQuery("select s from Spieler s join fetch s.punktzahl", Spieler.class)
				.getResultList();
		Collections.sort(alleSpieler, new Comparator<Spieler>() {
			public int compare(Spieler o1, Spieler o2) {
				return (o1.getPunktzahl().getPunktzahl() - o2.getPunktzahl().getPunktzahl()) * -1;
			}
		});

		/* Nur aktive Spieler */
		for (Iterator<Spieler> i = alleSpieler.iterator(); i.hasNext();) {
			Spieler spieler = i.next();
			if (!spieler.isVisible()) {
				i.remove();
			}
		}

		List<RanglistenEintrag> result = new ArrayList<RanglistenEintrag>();
		int rang = 1;
		for (Spieler spieler : alleSpieler) {
			RanglistenEintrag eintrag = new RanglistenEintrag(spieler.getId(), rang, spieler.getName(), spieler.getPunktzahl()
					.getPunktzahl(), spieler.getAnzahlSpiele(), spieler.getAnzahlSiege());
			result.add(eintrag);
			rang++;
		}
		return result;
	}

	public List<Spieler> findAllInaktiveSpieler() {
		List<Spieler> alleSpieler = eloPersistence.findAll(Spieler.class);
		Collections.sort(alleSpieler, new Comparator<Spieler>() {
			public int compare(Spieler o1, Spieler o2) {
				return (o1.getPunktzahl().getPunktzahl() - o2.getPunktzahl().getPunktzahl()) * -1;
			}
		});

		/* Nur inaktive Spieler */
		for (Iterator<Spieler> i = alleSpieler.iterator(); i.hasNext();) {
			Spieler spieler = i.next();
			if (spieler.isVisible()) {
				i.remove();
			}
		}

		return alleSpieler;
	}

	public List<GespieltesSpiel> getSpiele(String spielerName, int maxResults) {
		List<Spiel> alleSpiele = eloPersistence.createQuery("select distinct s from Spiel s " + //
				"join fetch s.gewinner g " + //
				"join fetch g.punktzahl gp " + //
				"join fetch s.verlierer v " + //
				"join fetch v.punktzahl vp " + //
				"join fetch g.spieler1 s1 " + //
				"join fetch g.spieler2 s2 " + //
				"join fetch v.spieler1 s3 " + //
				"join fetch v.spieler2 s4 " + //
				"join fetch s.punktzahl p " + //
				"where s1.name = :spielerName " + //
				"or s2.name = :spielerName " + //
				"or s3.name = :spielerName " + //
				"or s4.name = :spielerName order by s.zeitpunkt desc", Spiel.class).setParameter("spielerName", spielerName)
				.getResultList();

		List<GespieltesSpiel> result = new ArrayList<GespieltesSpiel>();
		for (Spiel spiel : alleSpiele) {
			boolean gewonnen = true;
			if (!spiel.getGewinner().getSpieler1().getName().equals(spielerName)
					&& !spiel.getGewinner().getSpieler2().getName().equals(spielerName)) {
				gewonnen = false;
			}
			result.add(new GespieltesSpiel(spiel, gewonnen));
		}
		return result.subList(0, Math.min(result.size(), maxResults));
	}

	public List<EloStatistik> getEloStatistik(String spielerName, String datePattern, String dateStart) {
		return getEloStatistikInternal(spielerName, datePattern, dateStart, false);
	}

	private List<EloStatistik> getEloStatistikInternal(String spielerName, String datePattern, String dateStart, boolean fillGaps) {
		Spieler spieler = eloPersistence
				.createQuery("select distinct s from Spieler s join fetch s.punktzahlHistorie h where s.name = :spielerName", Spieler.class)
				.setParameter("spielerName", spielerName).getSingleResult();
		TreeMap<String, EloPunktzahl> werte = new TreeMap<String, EloPunktzahl>();
		Set<EloPunktzahl> punktzahlHistorie = spieler.getPunktzahlHistorie();
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern, Locale.GERMAN);

		Date start;
		Date ende = DateUtils.ceiling(Calendar.getInstance(Locale.GERMAN), Calendar.DATE).getTime();
		try {
			start = sdf.parse(dateStart);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		for (EloPunktzahl punktzahl : punktzahlHistorie) {
			if (punktzahl.getBerechnetAm().before(start) || punktzahl.getBerechnetAm().after(ende)) {
				continue;
			}
			String key = sdf.format(punktzahl.getBerechnetAm());
			EloPunktzahl bisherigePunktzahl = werte.get(key);
			if (bisherigePunktzahl == null) {
				werte.put(key, punktzahl);
			} else {
				if (bisherigePunktzahl.getBerechnetAm().before(punktzahl.getBerechnetAm())) {
					werte.put(key, punktzahl);
				}
			}
		}

		if (fillGaps) {

			int lastValue = 0;

			Date temp = start;
			do {
				String key = sdf.format(temp);
				if (!werte.containsKey(key)) {
					EloPunktzahl elo = new EloPunktzahl();
					elo.setBerechnetAm(temp);
					elo.setPunktzahl(lastValue);
					werte.put(key, elo);
				} else {
					lastValue = werte.get(key).getPunktzahl();
				}
				temp = DateUtils.addDays(temp, 1);
			} while (!temp.after(ende));
		}

		List<EloStatistik> result = new ArrayList<EloStatistik>();
		for (Map.Entry<String, EloPunktzahl> entry : werte.entrySet()) {
			result.add(new EloStatistik(entry.getKey(), entry.getValue().getBerechnetAm().getTime() / 1000, entry.getValue().getPunktzahl()));
		}
		return result;
	}

	public List<RangStatistik> getRangStatistik(String spielerName, String datePattern, String dateStart) {
		List<EloStatistik> eigeneEloStatistik = getEloStatistikInternal(spielerName, datePattern, dateStart, true);
		Map<String, List<EloStatistik>> andereEloStatistiken = new HashMap<String, List<EloStatistik>>();
		List<Spieler> alleSpieler = eloPersistence.findAll(Spieler.class);
		for (Spieler spieler : alleSpieler) {
			if (spieler.isVisible() && !spieler.getName().equals(spielerName)) {
				andereEloStatistiken.put(spieler.getName(), getEloStatistikInternal(spieler.getName(), datePattern, dateStart, true));
			}
		}

		List<RangStatistik> result = new ArrayList<RangStatistik>();
		for (int i = 0; i < eigeneEloStatistik.size(); i++) {
			RangStatistik rang = calculateRang(eigeneEloStatistik, andereEloStatistiken, i);
			if (rang != null) {
				result.add(rang);
			}
		}
		/* doppelte Einträge entfernen */
		RangStatistik prev = null;
		for (Iterator<RangStatistik> i = result.iterator(); i.hasNext();) {
			RangStatistik stat = i.next();
			if (prev == null) {
				prev = stat;
			} else if (prev.getRang() == stat.getRang()) {
				i.remove();
			} else {
				prev = stat;
			}
		}
		return result;
	}

	private RangStatistik calculateRang(List<EloStatistik> eigeneEloStatistik, Map<String, List<EloStatistik>> andereEloStatistiken,
			int index) {
		EloStatistik eigeneElo = eigeneEloStatistik.get(index);
		if (eigeneElo.getPunktzahl() == 0) {
			return null;
		}
		int rang = 1;
		for (List<EloStatistik> andereEloStatistik : andereEloStatistiken.values()) {
			if (andereEloStatistik.get(index).getPunktzahl() > eigeneElo.getPunktzahl()) {
				rang++;
			}
		}
		return new RangStatistik(eigeneElo.getLabel(), eigeneElo.getTime(), rang);
	}

	public StatistikZusammenfassung<Spieler> getStatistikZusammenfassung(String spielerName) {
		Spieler spieler = eloPersistence
				.createQuery("select distinct s from Spieler s join fetch s.punktzahlHistorie where s.name = :spielerName", Spieler.class)
				.setParameter("spielerName", spielerName).getSingleResult();

		int toreGeschossen = 0;
		int toreGefangen = 0;
		int eloMinimum = Integer.MAX_VALUE;
		int eloMaximum = Integer.MIN_VALUE;

		for (EloPunktzahl punktzahl : spieler.getPunktzahlHistorie()) {
			if (punktzahl.getPunktzahl() < eloMinimum) {
				eloMinimum = punktzahl.getPunktzahl();
			}
			if (punktzahl.getPunktzahl() > eloMaximum) {
				eloMaximum = punktzahl.getPunktzahl();
			}
		}

		List<GespieltesSpiel> spiele = getSpiele(spielerName, Integer.MAX_VALUE);

		for (GespieltesSpiel spiel : spiele) {
			boolean gewonnen = spiel.isGewonnen();
			if (gewonnen) {
				toreGeschossen += spiel.getToreGewinner();
				toreGefangen += spiel.getToreVerlierer();
			} else {
				toreGefangen += spiel.getToreGewinner();
				toreGeschossen += spiel.getToreVerlierer();
			}
		}

		StatistikZusammenfassung<Spieler> result = new StatistikZusammenfassung<Spieler>(spieler, spieler.getAnzahlSpiele(),
				spieler.getAnzahlSiege(), toreGeschossen, toreGefangen, eloMinimum, eloMaximum);
		return result;
	}

	public Spieler findOrCreateSpieler(String spielerName, Date timestamp) {
		Spieler spieler = eloPersistence.findSpielerByName(spielerName);
		if (spieler == null) {
			EloPunktzahl elopunkte = new EloPunktzahl();
			elopunkte.setBerechnetAm(timestamp);
			elopunkte.setPunktzahl(ELO_START);
			spieler = new Spieler();
			spieler.setName(spielerName);
			spieler.setPunktzahl(elopunkte);
			eloPersistence.persist(spieler);
		}
		return spieler;
	}

	public void activateSpieler(String spielerName) {
		eloPersistence.findSpielerByName(spielerName).setVisible(true);
	}

	public void deactivateSpieler(String spielerName) {
		eloPersistence.findSpielerByName(spielerName).setVisible(false);
	}

	public void toggleTopFlop(String spielerName) {
		Spieler spieler = eloPersistence.findSpielerByName(spielerName);
		spieler.setTopFlopAktiviert(!spieler.isTopFlopAktiviert());
	}

	public boolean isSpielerRemoveable(String spielerName) {
		Spieler spieler = eloPersistence
				.createQuery("select distinct s from Spieler s join fetch s.punktzahlHistorie h where s.name = :spielerName", Spieler.class)
				.setParameter("spielerName", spielerName).getSingleResult();
		return spieler.getPunktzahlHistorie().size() == 1;
	}

	public void removeSpieler(String spielerName) {
		if (isSpielerRemoveable(spielerName)) {
			eloPersistence.remove(eloPersistence.findSpielerByName(spielerName));
		}
	}

	public void setEloPersistence(EloPersistence eloPersistence) {
		this.eloPersistence = eloPersistence;
	}
	
	public List<RanglistenEintrag> getStatistikLieblingsmitspieler(String spielerName, int n) {
		List<RanglistenEintrag> result = createMitspielerRangliste(spielerName);
		Collections.sort(result, new Comparator<RanglistenEintrag>() {
			public int compare(RanglistenEintrag o1, RanglistenEintrag o2) {
				return o2.getPunktzahl() - o1.getPunktzahl();
			}
		});
		result = result.subList(0, Math.min(result.size(), n));
		int rang = 1;
		for (Iterator<RanglistenEintrag> i = result.iterator(); i.hasNext();) {
			RanglistenEintrag eintrag = i.next();
			if (eintrag.getPunktzahl() <= 0) {
				i.remove();
				continue;
			}
			eintrag.setRang(rang);
			rang++;
		}
		return result;
	}
	
	public List<RanglistenEintrag> getStatistikAngstmitspieler(String spielerName, int n) {
		List<RanglistenEintrag> result = createMitspielerRangliste(spielerName);
		Collections.sort(result, new Comparator<RanglistenEintrag>() {
			public int compare(RanglistenEintrag o1, RanglistenEintrag o2) {
				return o1.getPunktzahl() - o2.getPunktzahl();
			}
		});
		result = result.subList(0, Math.min(result.size(), n));
		int rang = 1;
		for (Iterator<RanglistenEintrag> i = result.iterator(); i.hasNext();) {
			RanglistenEintrag eintrag = i.next();
			if (eintrag.getPunktzahl() >= 0) {
				i.remove();
				continue;
			}
			eintrag.setRang(rang);
			rang++;
		}
		return result;
	}

	public List<RanglistenEintrag> getStatistikLieblingsgegner(String spielerName, int n) {
		List<RanglistenEintrag> result = createGegnerRangliste(spielerName);
		Collections.sort(result, new Comparator<RanglistenEintrag>() {
			public int compare(RanglistenEintrag o1, RanglistenEintrag o2) {
				return o2.getPunktzahl() - o1.getPunktzahl();
			}
		});
		result = result.subList(0, Math.min(result.size(), n));
		int rang = 1;
		for (Iterator<RanglistenEintrag> i = result.iterator(); i.hasNext();) {
			RanglistenEintrag eintrag = i.next();
			if (eintrag.getPunktzahl() <= 0) {
				i.remove();
				continue;
			}
			eintrag.setRang(rang);
			rang++;
		}
		return result;
	}

	public List<RanglistenEintrag> getStatistikAngstgegner(String spielerName, int n) {
		List<RanglistenEintrag> result = createGegnerRangliste(spielerName);
		Collections.sort(result, new Comparator<RanglistenEintrag>() {
			public int compare(RanglistenEintrag o1, RanglistenEintrag o2) {
				return o1.getPunktzahl() - o2.getPunktzahl();
			}
		});
		result = result.subList(0, Math.min(result.size(), n));
		int rang = 1;
		for (Iterator<RanglistenEintrag> i = result.iterator(); i.hasNext();) {
			RanglistenEintrag eintrag = i.next();
			if (eintrag.getPunktzahl() >= 0) {
				i.remove();
				continue;
			}
			eintrag.setRang(rang);
			rang++;
		}
		return result;
	}

	private List<RanglistenEintrag> createGegnerRangliste(String spielerName) {
		Map<Spieler, Integer> punkteMap = new HashMap<Spieler, Integer>();
		Map<Spieler, Integer> spieleMap = new HashMap<Spieler, Integer>();
		Map<Spieler, Integer> siegeMap = new HashMap<Spieler, Integer>();
		List<GespieltesSpiel> spiele = getSpiele(spielerName, Integer.MAX_VALUE);
		for (GespieltesSpiel spiel : spiele) {
			if (spiel.isGewonnen()) {
				if (!punkteMap.containsKey(spiel.getVerlierer().getSpieler1())) {
					punkteMap.put(spiel.getVerlierer().getSpieler1(), 0);
					spieleMap.put(spiel.getVerlierer().getSpieler1(), 0);
					siegeMap.put(spiel.getVerlierer().getSpieler1(), 0);
				}
				if (!punkteMap.containsKey(spiel.getVerlierer().getSpieler2())) {
					punkteMap.put(spiel.getVerlierer().getSpieler2(), 0);
					spieleMap.put(spiel.getVerlierer().getSpieler2(), 0);
					siegeMap.put(spiel.getVerlierer().getSpieler2(), 0);
				}
			} else {
				if (!punkteMap.containsKey(spiel.getGewinner().getSpieler1())) {
					punkteMap.put(spiel.getGewinner().getSpieler1(), 0);
					spieleMap.put(spiel.getGewinner().getSpieler1(), 0);
					siegeMap.put(spiel.getGewinner().getSpieler1(), 0);
				}
				if (!punkteMap.containsKey(spiel.getGewinner().getSpieler2())) {
					punkteMap.put(spiel.getGewinner().getSpieler2(), 0);
					spieleMap.put(spiel.getGewinner().getSpieler2(), 0);
					siegeMap.put(spiel.getGewinner().getSpieler2(), 0);
				}
			}
			if (spiel.isGewonnen()) {
				punkteMap.put(spiel.getVerlierer().getSpieler1(), punkteMap.get(spiel.getVerlierer().getSpieler1())
						+ spiel.getPunktzahl().getPunktzahl());
				spieleMap.put(spiel.getVerlierer().getSpieler1(), spieleMap.get(spiel.getVerlierer().getSpieler1()) + 1);
				siegeMap.put(spiel.getVerlierer().getSpieler1(), siegeMap.get(spiel.getVerlierer().getSpieler1()) + 1);
				punkteMap.put(spiel.getVerlierer().getSpieler2(), punkteMap.get(spiel.getVerlierer().getSpieler2())
						+ spiel.getPunktzahl().getPunktzahl());
				spieleMap.put(spiel.getVerlierer().getSpieler2(), spieleMap.get(spiel.getVerlierer().getSpieler2()) + 1);
				siegeMap.put(spiel.getVerlierer().getSpieler2(), siegeMap.get(spiel.getVerlierer().getSpieler2()) + 1);
			} else {
				punkteMap.put(spiel.getGewinner().getSpieler1(), punkteMap.get(spiel.getGewinner().getSpieler1())
						- spiel.getPunktzahl().getPunktzahl());
				spieleMap.put(spiel.getGewinner().getSpieler1(), spieleMap.get(spiel.getGewinner().getSpieler1()) + 1);
				punkteMap.put(spiel.getGewinner().getSpieler2(), punkteMap.get(spiel.getGewinner().getSpieler2())
						- spiel.getPunktzahl().getPunktzahl());
				spieleMap.put(spiel.getGewinner().getSpieler2(), spieleMap.get(spiel.getGewinner().getSpieler2()) + 1);
			}
		}
		Spieler angefragterSpieler = eloPersistence.findSpielerByName(spielerName);
		Set<Spieler> spielerListe = punkteMap.keySet();
		List<RanglistenEintrag> result = new ArrayList<RanglistenEintrag>(spielerListe.size());
		for (Spieler spieler : spielerListe) {
			if (!angefragterSpieler.isVisible() || spieler.isVisible()) {
				Integer punkte = punkteMap.get(spieler);
				Integer spielAnzahl = spieleMap.get(spieler);
				Integer siege = siegeMap.get(spieler);
				RanglistenEintrag re = new RanglistenEintrag(spieler.getId(), -1, spieler.getName(), punkte, spielAnzahl, siege);
				result.add(re);
			}
		}
		return result;
	}
	
	private List<RanglistenEintrag> createMitspielerRangliste(String spielerName) {
		Map<Spieler, Integer> punkteMap = new HashMap<Spieler, Integer>();
		Map<Spieler, Integer> spieleMap = new HashMap<Spieler, Integer>();
		Map<Spieler, Integer> siegeMap = new HashMap<Spieler, Integer>();
		List<GespieltesSpiel> spiele = getSpiele(spielerName, Integer.MAX_VALUE);
		for (GespieltesSpiel spiel : spiele) {
			if (!spiel.isGewonnen()) {
				if (!punkteMap.containsKey(spiel.getVerlierer().getSpieler1())) {
					punkteMap.put(spiel.getVerlierer().getSpieler1(), 0);
					spieleMap.put(spiel.getVerlierer().getSpieler1(), 0);
					siegeMap.put(spiel.getVerlierer().getSpieler1(), 0);
				}
				if (!punkteMap.containsKey(spiel.getVerlierer().getSpieler2())) {
					punkteMap.put(spiel.getVerlierer().getSpieler2(), 0);
					spieleMap.put(spiel.getVerlierer().getSpieler2(), 0);
					siegeMap.put(spiel.getVerlierer().getSpieler2(), 0);
				}
			} else {
				if (!punkteMap.containsKey(spiel.getGewinner().getSpieler1())) {
					punkteMap.put(spiel.getGewinner().getSpieler1(), 0);
					spieleMap.put(spiel.getGewinner().getSpieler1(), 0);
					siegeMap.put(spiel.getGewinner().getSpieler1(), 0);
				}
				if (!punkteMap.containsKey(spiel.getGewinner().getSpieler2())) {
					punkteMap.put(spiel.getGewinner().getSpieler2(), 0);
					spieleMap.put(spiel.getGewinner().getSpieler2(), 0);
					siegeMap.put(spiel.getGewinner().getSpieler2(), 0);
				}
			}
			if (!spiel.isGewonnen()) {
				punkteMap.put(spiel.getVerlierer().getSpieler1(), punkteMap.get(spiel.getVerlierer().getSpieler1())
						- spiel.getPunktzahl().getPunktzahl());
				spieleMap.put(spiel.getVerlierer().getSpieler1(), spieleMap.get(spiel.getVerlierer().getSpieler1()) + 1);
				punkteMap.put(spiel.getVerlierer().getSpieler2(), punkteMap.get(spiel.getVerlierer().getSpieler2())
						- spiel.getPunktzahl().getPunktzahl());
				spieleMap.put(spiel.getVerlierer().getSpieler2(), spieleMap.get(spiel.getVerlierer().getSpieler2()) + 1);
			} else {
				punkteMap.put(spiel.getGewinner().getSpieler1(), punkteMap.get(spiel.getGewinner().getSpieler1())
						+ spiel.getPunktzahl().getPunktzahl());
				spieleMap.put(spiel.getGewinner().getSpieler1(), spieleMap.get(spiel.getGewinner().getSpieler1()) + 1);
				siegeMap.put(spiel.getGewinner().getSpieler1(), siegeMap.get(spiel.getGewinner().getSpieler1()) + 1);
				punkteMap.put(spiel.getGewinner().getSpieler2(), punkteMap.get(spiel.getGewinner().getSpieler2())
						+ spiel.getPunktzahl().getPunktzahl());
				spieleMap.put(spiel.getGewinner().getSpieler2(), spieleMap.get(spiel.getGewinner().getSpieler2()) + 1);
				siegeMap.put(spiel.getGewinner().getSpieler2(), siegeMap.get(spiel.getGewinner().getSpieler2()) + 1);
			}
		}
		Spieler angefragterSpieler = eloPersistence.findSpielerByName(spielerName);
		Set<Spieler> spielerListe = punkteMap.keySet();
		List<RanglistenEintrag> result = new ArrayList<RanglistenEintrag>(spielerListe.size());
		for (Spieler spieler : spielerListe) {
			if(spieler.getName().equals(spielerName)) {
				continue;
			}
			if (!angefragterSpieler.isVisible() || spieler.isVisible()) {
				Integer punkte = punkteMap.get(spieler);
				Integer spielAnzahl = spieleMap.get(spieler);
				Integer siege = siegeMap.get(spieler);
				RanglistenEintrag re = new RanglistenEintrag(spieler.getId(), -1, spieler.getName(), punkte, spielAnzahl, siege);
				result.add(re);
			}
		}
		return result;
	}

}