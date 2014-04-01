package de.hbt.kicker.elo.v2.service;

import static de.hbt.kicker.elo.KickerGlobalSettings.ELO_START;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import de.hbt.kicker.elo.v2.model.Team;
import de.hbt.kicker.elo.v2.persistence.EloPersistence;

public class TeamService {

	private EloPersistence eloPersistence;

	public Team findTeamById(String teamId) {
		return eloPersistence.findById(Team.class, teamId);
	}

	public Team findOrCreateTeam(Spieler spieler1, Spieler spieler2, Date timestamp) {
		Team team = eloPersistence.findTeamBySpieler(spieler1, spieler2);
		if (team == null) {
			team = new Team();
			team.setSpieler1(spieler1);
			team.setSpieler2(spieler2);
			EloPunktzahl elopunkte = new EloPunktzahl();
			elopunkte.setBerechnetAm(timestamp);
			elopunkte.setPunktzahl(ELO_START);
			team.setPunktzahl(elopunkte);
			eloPersistence.persist(team);
		}
		return team;
	}

	public List<RanglistenEintrag> getTeamRangliste() {
		List<Team> alleTeams = eloPersistence.createQuery("select distinct t from Team t join fetch t.punktzahlHistorie", Team.class)
				.getResultList();
		Collections.sort(alleTeams, new Comparator<Team>() {
			public int compare(Team o1, Team o2) {
				return (o1.getPunktzahl().getPunktzahl() - o2.getPunktzahl().getPunktzahl()) * -1;
			}
		});

		/* Nur aktive Spieler */
		for (Iterator<Team> i = alleTeams.iterator(); i.hasNext();) {
			Team team = i.next();
			if (!team.getSpieler1().isVisible() || !team.getSpieler2().isVisible()) {
				i.remove();
			}
		}

		List<RanglistenEintrag> rangliste = new ArrayList<RanglistenEintrag>();
		int rang = 1;
		for (Team team : alleTeams) {
			int anzahlSpiele = team.getPunktzahlHistorie().size() - 1;
			int anzahlSiege = 0;

			for (EloPunktzahl punktzahl : team.getPunktzahlHistorie()) {
				if (punktzahl.getZuwachs() > 0) {
					anzahlSiege++;
				}
			}

			RanglistenEintrag eintrag = new RanglistenEintrag(team.getId(), rang, team.getSpieler1().getName() + "/"
					+ team.getSpieler2().getName(), team.getPunktzahl().getPunktzahl(), anzahlSpiele, anzahlSiege);
			rangliste.add(eintrag);
			rang++;
		}
		return rangliste;
	}

	public List<Team> findAllInaktiveTeams() {
		List<Team> alleTeams = eloPersistence.findAll(Team.class);
		Collections.sort(alleTeams, new Comparator<Team>() {
			public int compare(Team o1, Team o2) {
				return (o1.getPunktzahl().getPunktzahl() - o2.getPunktzahl().getPunktzahl()) * -1;
			}
		});

		/* Nur inaktive Spieler */
		for (Iterator<Team> i = alleTeams.iterator(); i.hasNext();) {
			Team team = i.next();
			if (team.getSpieler1().isVisible() && team.getSpieler2().isVisible()) {
				i.remove();
			}
		}

		return alleTeams;
	}

	public List<GespieltesSpiel> getSpiele(String teamId, int maxResults) {
		List<Spiel> alleSpiele = eloPersistence.findAll(Spiel.class, Integer.MAX_VALUE, "order by m.zeitpunkt desc");
		for (Iterator<Spiel> i = alleSpiele.iterator(); i.hasNext();) {
			Spiel spiel = i.next();
			if (!spiel.getGewinner().getId().equals(teamId) && !spiel.getVerlierer().getId().equals(teamId)) {
				i.remove();
			}
		}
		List<GespieltesSpiel> result = new ArrayList<GespieltesSpiel>();
		for (Spiel spiel : alleSpiele) {
			boolean gewonnen = true;
			if (!spiel.getGewinner().getId().equals(teamId)) {
				gewonnen = false;
			}
			result.add(new GespieltesSpiel(spiel, gewonnen));
		}
		return result.subList(0, Math.min(result.size(), maxResults));
	}

	public List<EloStatistik> getEloStatistik(String teamId, String datePattern, String dateStart) {
		Team team = eloPersistence
				.createQuery("select distinct t from Team t join fetch t.punktzahlHistorie h where t.id = :teamId", Team.class)
				.setParameter("teamId", teamId).getSingleResult();
		TreeMap<String, EloPunktzahl> werte = new TreeMap<String, EloPunktzahl>();
		Set<EloPunktzahl> punktzahlHistorie = team.getPunktzahlHistorie();
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

		List<EloStatistik> result = new ArrayList<EloStatistik>();
		for (Map.Entry<String, EloPunktzahl> entry : werte.entrySet()) {
			result.add(new EloStatistik(entry.getKey(), entry.getValue().getBerechnetAm().getTime() / 1000, entry.getValue().getPunktzahl()));
		}
		return result;
	}

	public StatistikZusammenfassung<Team> getStatistikZusammenfassung(String teamId) {
		Team team = eloPersistence.findById(Team.class, teamId);

		int anzahlSiege = 0;
		int toreGeschossen = 0;
		int toreGefangen = 0;
		int eloMinimum = Integer.MAX_VALUE;
		int eloMaximum = Integer.MIN_VALUE;

		for (EloPunktzahl punktzahl : team.getPunktzahlHistorie()) {
			if (punktzahl.getPunktzahl() < eloMinimum) {
				eloMinimum = punktzahl.getPunktzahl();
			}
			if (punktzahl.getPunktzahl() > eloMaximum) {
				eloMaximum = punktzahl.getPunktzahl();
			}
		}

		List<Spiel> spiele = eloPersistence.findAll(Spiel.class, Integer.MAX_VALUE, "order by m.zeitpunkt desc");
		for (Iterator<Spiel> i = spiele.iterator(); i.hasNext();) {
			Spiel spiel = i.next();
			if (!spiel.getGewinner().getId().equals(teamId) && !spiel.getVerlierer().getId().equals(teamId)) {
				i.remove();
			}
		}
		for (Spiel spiel : spiele) {
			boolean gewonnen = true;
			if (!spiel.getGewinner().getId().equals(teamId)) {
				gewonnen = false;
			}
			if (gewonnen) {
				anzahlSiege++;
				toreGeschossen += spiel.getToreGewinner();
				toreGefangen += spiel.getToreVerlierer();
			} else {
				toreGefangen += spiel.getToreGewinner();
				toreGeschossen += spiel.getToreVerlierer();
			}
		}

		StatistikZusammenfassung<Team> result = new StatistikZusammenfassung<Team>(team, spiele.size(), anzahlSiege, toreGeschossen, toreGefangen,
				eloMinimum, eloMaximum);
		return result;
	}
	
	public List<RanglistenEintrag> getTopNTeams(String spielerName, int n) {
		
		Spieler spieler = eloPersistence.findSpielerByName(spielerName);
		
		List<Team> alleTeams = eloPersistence.createQuery("select distinct t from Team t join fetch t.punktzahlHistorie " +
				"where t.spieler1.name = :spielerName or t.spieler2.name = :spielerName", Team.class)
				.setParameter("spielerName", spielerName)
				.getResultList();
		Collections.sort(alleTeams, new Comparator<Team>() {
			public int compare(Team o1, Team o2) {
				return (o1.getPunktzahl().getPunktzahl() - o2.getPunktzahl().getPunktzahl()) * -1;
			}
		});
		
		/* Nur Teams mit Spielern Top/Flop aktiviert */
		for (Iterator<Team> i = alleTeams.iterator(); i.hasNext();) {
			Team team = i.next();
			if (!team.getSpieler1().isTopFlopAktiviert() || !team.getSpieler2().isTopFlopAktiviert()) {
				i.remove();
			}
		}

		/* Nur aktive Spieler, falls Spieler noch aktiv */
		if(spieler.isVisible()) {
			for (Iterator<Team> i = alleTeams.iterator(); i.hasNext();) {
				Team team = i.next();
				if (!team.getSpieler1().isVisible() || !team.getSpieler2().isVisible()) {
					i.remove();
				}
			}
		}

		List<RanglistenEintrag> rangliste = new ArrayList<RanglistenEintrag>();
		int rang = 1;
		for (Team team : alleTeams) {
			int anzahlSpiele = team.getPunktzahlHistorie().size() - 1;
			int anzahlSiege = 0;

			for (EloPunktzahl punktzahl : team.getPunktzahlHistorie()) {
				if (punktzahl.getZuwachs() > 0) {
					anzahlSiege++;
				}
			}

			RanglistenEintrag eintrag = new RanglistenEintrag(team.getId(), rang, team.getSpieler1().getName() + "/"
					+ team.getSpieler2().getName(), team.getPunktzahl().getPunktzahl(), anzahlSpiele, anzahlSiege);
			rangliste.add(eintrag);
			rang++;
		}
		return rangliste.subList(0, Math.min(rangliste.size(), n));
	}

	public void setEloPersistence(EloPersistence eloPersistence) {
		this.eloPersistence = eloPersistence;
	}

}
