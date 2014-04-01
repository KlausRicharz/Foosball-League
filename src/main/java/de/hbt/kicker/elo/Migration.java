package de.hbt.kicker.elo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.lang3.time.DateUtils;

import de.hbt.kicker.elo.v1.model.SpielAlt;
import de.hbt.kicker.elo.v1.model.SpielerAlt;
import de.hbt.kicker.elo.v1.persistence.EloPersistenceAlt;
import de.hbt.kicker.elo.v2.model.EloPunktzahl;
import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.model.Spieler;
import de.hbt.kicker.elo.v2.model.Team;
import de.hbt.kicker.elo.v2.persistence.EloPersistence;
import de.hbt.kicker.elo.v2.service.EloCalculationResult;
import de.hbt.kicker.elo.v2.service.EloService;
import de.hbt.kicker.elo.v2.service.SpielService;
import de.hbt.kicker.elo.v2.service.SpielerService;
import de.hbt.kicker.elo.v2.service.TeamService;

public class Migration {

	private static boolean RECALC_ELO = true;

	private static Map<String, List<Integer>> alteEloWerte = new HashMap<String, List<Integer>>();
	private static Map<String, Integer> lastEloIndex = new HashMap<String, Integer>();
	private static Set<String> players = new HashSet<String>();
	private static Date minDate = null;

	private static EntityManager entityManager;

	static {
		/*
		 * players.add("Timm"); players.add("Tim"); players.add("Klaus");
		 * players.add("Claas"); players.add("Ilja"); players.add("Stefan");
		 * players.add("ThomasD"); players.add("Jannick"); try { minDate = new
		 * SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN).parse("2012-01-01"); }
		 * catch (ParseException e) { minDate = null; }
		 */
	}

	public static void main(String[] args) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("elo");
		entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();

		EloPersistence ep = new EloPersistence();
		ep.setEntityManager(entityManager);
		
		if(ep.findAll(Spieler.class).size() > 0) {
			System.out.println("Daten wurden bereits migriert!");
			entityManager.close();
			factory.close();
			return;
		}
		
		initAlteElo(entityManager);
		
		EloPersistenceAlt epa = new EloPersistenceAlt();
		epa.setEntityManager(entityManager);
		EloService es = new EloService();
//		es.setEloCalculationStrategy(new LegacyEloCalculationStrategy());
		SpielerService ssv = new SpielerService();
		ssv.setEloPersistence(ep);
		SpielService sps = new SpielService();
		sps.setEloPersistence(ep);
		sps.setEloService(es);
		TeamService ts = new TeamService();
		ts.setEloPersistence(ep);

		List<SpielAlt> alteSpiele = epa.findAllSpiele();
		filter(alteSpiele);
		int total = alteSpiele.size();
		int count = 0;
		System.out.println(alteSpiele.size() + " matches werden migriert ...");
		long start = System.currentTimeMillis();
		for (SpielAlt spielAlt : alteSpiele) {
			count++;
			Spieler s1 = migrateSpieler(ssv, spielAlt.getSpieler1(), spielAlt.getTimestamp());
			Spieler s2 = migrateSpieler(ssv, spielAlt.getSpieler2(), spielAlt.getTimestamp());
			Spieler s3 = migrateSpieler(ssv, spielAlt.getSpieler3(), spielAlt.getTimestamp());
			Spieler s4 = migrateSpieler(ssv, spielAlt.getSpieler4(), spielAlt.getTimestamp());
			Team t1 = migrateTeam(ts, s1, s2, spielAlt.getTimestamp());
			Team t2 = migrateTeam(ts, s3, s4, spielAlt.getTimestamp());
			migrateSpiel(sps, spielAlt, t1, t2);
			if (count % 80 == 0) {
				System.out.println();
			}
			System.out.print('.');
			if (count % 500 == 0) {
				System.out.print(count);
				System.out.print(" (");
				System.out.print(count * 100 / total);
				System.out.print("%) - ");
				double permin = (double)500 / (double)(System.currentTimeMillis() - start) * 1000 * 60;
				System.out.print(permin);
				System.out.println(" rows/minute");
				start = System.currentTimeMillis();
			}
		}

		entityManager.flush();
		entityManager.getTransaction().commit();
		
	}

	private static void initAlteElo(EntityManager entityManager) {
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = entityManager.createNativeQuery("select s.NAME, e.ELOWERT, e.id " + //
				"from ELOWERTE e join ENTRY s ON (e.SPIELERID = s.ID) " + //
				"order by s.name, e.id").getResultList();
		String spielerName = "";
		int vorherigerEloWert = 0;
		int eloZuwachs = 0;
		List<Integer> eloWerte = null;
		for (Object[] row : resultList) {
			if (!spielerName.equals(row[0])) {
				spielerName = (String) row[0];
				eloWerte = new ArrayList<Integer>();
				eloWerte.add(KickerGlobalSettings.ELO_START);
				alteEloWerte.put(spielerName, eloWerte);
				vorherigerEloWert = KickerGlobalSettings.ELO_START;
			}
			eloZuwachs = ((Integer) row[1]) - vorherigerEloWert;
			eloWerte.add(eloZuwachs);
			vorherigerEloWert = (Integer) row[1];
		}
		
		for (Map.Entry<String, List<Integer>> entry : alteEloWerte.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}

	private static void filter(List<SpielAlt> alteSpiele) {
		for (Iterator<SpielAlt> iterator = alteSpiele.iterator(); iterator.hasNext();) {
			SpielAlt spielAlt = iterator.next();
			if (!matches(spielAlt)) {
				iterator.remove();
			}
		}
	}

	private static boolean matches(SpielAlt spielAlt) {
		return playerMatches(spielAlt.getSpieler1()) && playerMatches(spielAlt.getSpieler2()) && playerMatches(spielAlt.getSpieler3())
				&& playerMatches(spielAlt.getSpieler4()) && dateMatches(spielAlt);
	}

	private static boolean dateMatches(SpielAlt spielAlt) {
		return minDate == null || !spielAlt.getTimestamp().before(minDate);
	}

	private static boolean playerMatches(SpielerAlt spieler) {
		return players.isEmpty() || players.contains(spieler.getName());
	}

	private static void migrateSpiel(SpielService sps, SpielAlt spielAlt, Team t1, Team t2) {
		Spiel spiel = new Spiel();
		spiel.setGewinner(t1);
		spiel.setVerlierer(t2);
		spiel.setToreGewinner(spielAlt.getPunktewin());
		spiel.setToreVerlierer(spielAlt.getPunktelost());
		spiel.setZeitpunkt(spielAlt.getTimestamp());

		if (RECALC_ELO) {
			sps.calculateEloAndPersistSpiel(spiel);
		} else {
			readEloAndPersist(spiel);
		}
	}

	private static void readEloAndPersist(Spiel spiel) {
		
//		System.out.println("Spiel am " + spiel.getZeitpunkt());
		
		int zuwachsGewinnerAlt = 0;
		int zuwachsGewinnerAlt1 = getNextAlterEloWert(spiel.getGewinner().getSpieler1().getName());
		int zuwachsGewinnerAlt2 = getNextAlterEloWert(spiel.getGewinner().getSpieler2().getName());
		int zuwachsVerliererAlt = 0;
		int zuwachsVerliererAlt1 = getNextAlterEloWert(spiel.getVerlierer().getSpieler1().getName());
		int zuwachsVerliererAlt2 = getNextAlterEloWert(spiel.getVerlierer().getSpieler2().getName());

		if (zuwachsGewinnerAlt1 != zuwachsGewinnerAlt2) {
			System.out.print('x');
		}
		zuwachsGewinnerAlt = zuwachsGewinnerAlt1;

		if (zuwachsVerliererAlt1 != zuwachsVerliererAlt2) {
			System.out.print('y');
		}
		zuwachsVerliererAlt = zuwachsVerliererAlt1;

		zuwachsVerliererAlt = Math.abs(zuwachsVerliererAlt);

		int eloGewinner1 = spiel.getGewinner().getSpieler1().getPunktzahl().getPunktzahl();
		int eloGewinner2 = spiel.getGewinner().getSpieler2().getPunktzahl().getPunktzahl();
		int eloVerlierer1 = spiel.getVerlierer().getSpieler1().getPunktzahl().getPunktzahl();
		int eloVerlierer2 = spiel.getVerlierer().getSpieler2().getPunktzahl().getPunktzahl();

		EloCalculationResult gewinnerResult = new EloCalculationResult(zuwachsGewinnerAlt, eloGewinner1, eloGewinner2, eloVerlierer1,
				eloVerlierer2, 0, 0.5);
		EloCalculationResult verliererResult = new EloCalculationResult(zuwachsVerliererAlt, eloGewinner1, eloGewinner2, eloVerlierer1,
				eloVerlierer2, 0, 0.5);

		EloPunktzahl p1 = spiel.getGewinner().getSpieler1().getPunktzahl();
//		System.out.println(spiel.getGewinner().getSpieler1().getName() + "/" + gewinnerResult.getZuwachs());
		spiel.getGewinner().getSpieler1().setPunktzahl(addElo(p1, gewinnerResult, spiel.getZeitpunkt()));
		spiel.getGewinner().getSpieler1().setAnzahlSpiele(spiel.getGewinner().getSpieler1().getAnzahlSpiele() + 1);
		spiel.getGewinner().getSpieler1().setAnzahlSiege(spiel.getGewinner().getSpieler1().getAnzahlSiege() + 1);

		EloPunktzahl p2 = spiel.getGewinner().getSpieler2().getPunktzahl();
//		System.out.println(spiel.getGewinner().getSpieler2().getName() + "/" + gewinnerResult.getZuwachs());
		spiel.getGewinner().getSpieler2().setPunktzahl(addElo(p2, gewinnerResult, spiel.getZeitpunkt()));
		spiel.getGewinner().getSpieler2().setAnzahlSpiele(spiel.getGewinner().getSpieler2().getAnzahlSpiele() + 1);
		spiel.getGewinner().getSpieler2().setAnzahlSiege(spiel.getGewinner().getSpieler2().getAnzahlSiege() + 1);

		EloPunktzahl p3 = spiel.getVerlierer().getSpieler1().getPunktzahl();
//		System.out.println(spiel.getVerlierer().getSpieler1().getName() + "/" + -verliererResult.getZuwachs());
		spiel.getVerlierer().getSpieler1().setPunktzahl(subtractElo(p3, verliererResult, spiel.getZeitpunkt()));
		spiel.getVerlierer().getSpieler1().setAnzahlSpiele(spiel.getVerlierer().getSpieler1().getAnzahlSpiele() + 1);

		EloPunktzahl p4 = spiel.getVerlierer().getSpieler2().getPunktzahl();
//		System.out.println(spiel.getVerlierer().getSpieler2().getName() + "/" + -verliererResult.getZuwachs());
		spiel.getVerlierer().getSpieler2().setPunktzahl(subtractElo(p4, verliererResult, spiel.getZeitpunkt()));
		spiel.getVerlierer().getSpieler2().setAnzahlSpiele(spiel.getVerlierer().getSpieler2().getAnzahlSpiele() + 1);

		spiel.getGewinner().setPunktzahl(addElo(spiel.getGewinner().getPunktzahl(), gewinnerResult, spiel.getZeitpunkt()));
		spiel.getVerlierer().setPunktzahl(subtractElo(spiel.getVerlierer().getPunktzahl(), verliererResult, spiel.getZeitpunkt()));

		spiel.setPunktzahl(createElo(gewinnerResult, spiel.getZeitpunkt()));

		entityManager.persist(spiel);
	}

	private static int getNextAlterEloWert(String name) {
		Integer index = lastEloIndex.get(name);
		if (index == null) {
			index = 1;
		} else {
			index++;
		}
		lastEloIndex.put(name, index);
		return alteEloWerte.get(name).get(index);
	}
	
	private static EloPunktzahl createElo(EloCalculationResult eloResult, Date refDate) {
		EloPunktzahl eloPunktzahl = createEloPunktzahl(eloResult, refDate);
		eloPunktzahl.setPunktzahl(eloResult.getZuwachs());
		return eloPunktzahl;
	}

	private static EloPunktzahl createEloPunktzahl(EloCalculationResult eloResult, Date refDate) {
		EloPunktzahl eloPunktzahl = new EloPunktzahl();
		eloPunktzahl.setBerechnetAm(refDate);
		eloPunktzahl.setZuwachs(eloResult.getZuwachs());
		eloPunktzahl.setEloGewinner1(eloResult.getEloGewinner1());
		eloPunktzahl.setEloGewinner2(eloResult.getEloGewinner2());
		eloPunktzahl.setEloVerlierer1(eloResult.getEloVerlierer1());
		eloPunktzahl.setEloVerlierer2(eloResult.getEloVerlierer2());
		eloPunktzahl.setErwartung(eloResult.getErwartung());
		eloPunktzahl.setFaktor(eloResult.getFaktor());
		return eloPunktzahl;
	}

	private static EloPunktzahl addElo(EloPunktzahl current, EloCalculationResult eloResult, Date refDate) {
		EloPunktzahl eloPunktzahl = createEloPunktzahl(eloResult, refDate);
		eloPunktzahl.setPunktzahl(eloResult.getZuwachs());
		int eloPunkte = current.getPunktzahl();
		eloPunkte += eloResult.getZuwachs();
		if (eloPunkte < 0) {
			eloPunkte = 0;
		}
		eloPunktzahl.setPunktzahl(eloPunkte);
		
//		System.out.println("current: " + current.getPunktzahl() + " / neu: " + eloPunktzahl.getPunktzahl());
		return eloPunktzahl;
	}

	private static EloPunktzahl subtractElo(EloPunktzahl current, EloCalculationResult eloResult, Date refDate) {
		EloPunktzahl eloPunktzahl = createEloPunktzahl(eloResult, refDate);
		eloPunktzahl.setPunktzahl(eloResult.getZuwachs());
		int eloPunkte = current.getPunktzahl();
		eloPunkte -= eloResult.getZuwachs();
		if (eloPunkte < 0) {
			eloPunkte = 0;
		}
		eloPunktzahl.setPunktzahl(eloPunkte);
		eloPunktzahl.setZuwachs(-1 * eloPunktzahl.getZuwachs());
		
//		System.out.println("current: " + current.getPunktzahl() + " / neu: " + eloPunktzahl.getPunktzahl());
		
		return eloPunktzahl;
	}

	private static Map<String, Team> teamMapping = new HashMap<String, Team>();

	private static Team migrateTeam(TeamService ts, Spieler s1, Spieler s2, Date timestamp) {
		String teamKey = s1.getName() + "#" + s2.getName();
		Team team = teamMapping.get(teamKey);
		if (team == null) {
			team = ts.findOrCreateTeam(s1, s2, timestamp);
			teamMapping.put(teamKey, team);
		}
		return team;
	}

	private static Map<SpielerAlt, Spieler> spielerMapping = new HashMap<SpielerAlt, Spieler>();

	private static Spieler migrateSpieler(SpielerService ssv, SpielerAlt spielerAlt, Date timestamp) {
		Spieler spieler = spielerMapping.get(spielerAlt);
		if (spieler == null) {
			spieler = ssv.findOrCreateSpieler(spielerAlt.getName(), DateUtils.addHours(timestamp, -1));
			spieler.setVisible(spielerAlt.isVisible());
			spielerMapping.put(spielerAlt, spieler);
			System.out.println("Neuer Spieler: " + spieler.getName());
		}
		return spieler;
	}

}
