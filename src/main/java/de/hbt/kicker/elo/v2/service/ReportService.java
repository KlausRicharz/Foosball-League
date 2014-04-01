package de.hbt.kicker.elo.v2.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hbt.kicker.elo.KickerGlobalSettings;
import de.hbt.kicker.elo.v2.model.EloPunktzahl;
import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.model.Spieler;
import de.hbt.kicker.elo.v2.model.Team;
import de.hbt.kicker.elo.v2.persistence.EloPersistence;

public class ReportService {

	private EloPersistence eloPersistence;
	private EloService eloService;

	public ReportResult getFilteredReport(String[] spielerNamen, String startString, String endeString) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN);
		Date start = sdf.parse(startString);
		Date ende = null;

		if (endeString == null) {
			ende = new Date();
		} else {
			ende = sdf.parse(endeString);
		}

		Map<String, Spieler> spielerMap = new HashMap<String, Spieler>();
		Map<String, Team> teamMap = new HashMap<String, Team>();

		List<Spiel> spiele = getSpieleFiltered(Arrays.asList(spielerNamen), start, ende);
		List<GespieltesSpiel> spielErgebnis = new ArrayList<GespieltesSpiel>();
		for (Spiel spiel : spiele) {
			spielErgebnis.add(new GespieltesSpiel(calculateElo(spiel, spielerMap, teamMap), true));
		}

		List<Spieler> spielerSorted = new ArrayList<Spieler>(spielerMap.values());
		Collections.sort(spielerSorted, new Comparator<Spieler>() {
			public int compare(Spieler o1, Spieler o2) {
				return -1 * (o1.getPunktzahl().getPunktzahl() - o2.getPunktzahl().getPunktzahl());
			}
		});

		List<RanglistenEintrag> rangliste = new ArrayList<RanglistenEintrag>();
		int rang = 1;
		for (Spieler spieler : spielerSorted) {
			rangliste.add(new RanglistenEintrag(spieler.getId(), rang, spieler.getName(), spieler.getPunktzahl().getPunktzahl(), spieler
					.getAnzahlSpiele(), spieler.getAnzahlSiege()));
			rang++;
		}

		List<Team> teamSorted = new ArrayList<Team>(teamMap.values());
		Collections.sort(teamSorted, new Comparator<Team>() {
			public int compare(Team o1, Team o2) {
				return -1 * (o1.getPunktzahl().getPunktzahl() - o2.getPunktzahl().getPunktzahl());
			}
		});

		List<RanglistenEintrag> teamRangliste = new ArrayList<RanglistenEintrag>();
		int teamRang = 1;
		for (Team team : teamSorted) {

			int anzahlSpiele = team.getPunktzahlHistorie().size() - 1;
			int anzahlSiege = 0;

			for (EloPunktzahl punktzahl : team.getPunktzahlHistorie()) {
				if (punktzahl.getZuwachs() > 0) {
					anzahlSiege++;
				}
			}

			teamRangliste.add(new RanglistenEintrag(team.getId(), teamRang, team.getSpieler1().getName() + "/" + team.getSpieler2().getName(),
					team.getPunktzahl().getPunktzahl(), anzahlSpiele, anzahlSiege));
			teamRang++;
		}

		Collections.reverse(spielErgebnis);
		ReportResult result = new ReportResult(rangliste, teamRangliste, spielErgebnis);
		return result;
	}

	private Spiel calculateElo(Spiel spiel, Map<String, Spieler> spielerMap, Map<String, Team> teamMap) {
		Spieler gewinner1 = getSpieler(spiel.getGewinner().getSpieler1(), spielerMap);
		Spieler gewinner2 = getSpieler(spiel.getGewinner().getSpieler2(), spielerMap);
		Spieler verlierer1 = getSpieler(spiel.getVerlierer().getSpieler1(), spielerMap);
		Spieler verlierer2 = getSpieler(spiel.getVerlierer().getSpieler2(), spielerMap);

		Team gewinner = getTeam(spiel.getGewinner(), spielerMap, teamMap);
		Team verlierer = getTeam(spiel.getVerlierer(), spielerMap, teamMap);

		EloCalculationResult eloResult = eloService.calculateElo(gewinner1.getPunktzahl().getPunktzahl(), gewinner2.getPunktzahl()
				.getPunktzahl(), verlierer1.getPunktzahl().getPunktzahl(), verlierer2.getPunktzahl().getPunktzahl());

		EloCalculationResult teamEloResult = eloService.calculateElo(gewinner.getPunktzahl().getPunktzahl(), gewinner.getPunktzahl()
				.getPunktzahl(), verlierer.getPunktzahl().getPunktzahl(), verlierer.getPunktzahl().getPunktzahl());

		EloPunktzahl p1 = gewinner1.getPunktzahl();
		gewinner1.setPunktzahl(addElo(p1, eloResult, spiel.getZeitpunkt()));
		gewinner1.setAnzahlSpiele(gewinner1.getAnzahlSpiele() + 1);
		gewinner1.setAnzahlSiege(gewinner1.getAnzahlSiege() + 1);

		EloPunktzahl p2 = gewinner2.getPunktzahl();
		gewinner2.setPunktzahl(addElo(p2, eloResult, spiel.getZeitpunkt()));
		gewinner2.setAnzahlSpiele(gewinner2.getAnzahlSpiele() + 1);
		gewinner2.setAnzahlSiege(gewinner2.getAnzahlSiege() + 1);

		EloPunktzahl p3 = verlierer1.getPunktzahl();
		verlierer1.setPunktzahl(subtractElo(p3, eloResult, spiel.getZeitpunkt()));
		verlierer1.setAnzahlSpiele(verlierer1.getAnzahlSpiele() + 1);

		EloPunktzahl p4 = verlierer2.getPunktzahl();
		verlierer2.setPunktzahl(subtractElo(p4, eloResult, spiel.getZeitpunkt()));
		verlierer2.setAnzahlSpiele(verlierer2.getAnzahlSpiele() + 1);

		gewinner.setPunktzahl(addElo(gewinner.getPunktzahl(), teamEloResult, spiel.getZeitpunkt()));
		verlierer.setPunktzahl(subtractElo(verlierer.getPunktzahl(), teamEloResult, spiel.getZeitpunkt()));

		Spiel result = new Spiel();
		result.setGewinner(gewinner);
		result.setVerlierer(verlierer);
		result.setZeitpunkt(spiel.getZeitpunkt());
		result.setPunktzahl(createEloPunktzahl(eloResult, spiel.getZeitpunkt()));
		result.setTeamPunktzahl(createEloPunktzahl(teamEloResult, spiel.getZeitpunkt()));
		result.setToreGewinner(spiel.getToreGewinner());
		result.setToreVerlierer(spiel.getToreVerlierer());
		return result;
	}

	private Team getTeam(Team team, Map<String, Spieler> spielerMap, Map<String, Team> teamMap) {
		if (!teamMap.containsKey(team.getId())) {
			Team reportTeam = new Team();
			reportTeam.setId(team.getId());
			reportTeam.setSpieler1(getSpieler(team.getSpieler1(), spielerMap));
			reportTeam.setSpieler2(getSpieler(team.getSpieler2(), spielerMap));
			EloPunktzahl punktzahl = new EloPunktzahl();
			punktzahl.setPunktzahl(KickerGlobalSettings.ELO_START);
			reportTeam.setPunktzahl(punktzahl);
			teamMap.put(team.getId(), reportTeam);
		}
		return teamMap.get(team.getId());
	}

	private Spieler getSpieler(Spieler spieler, Map<String, Spieler> spielerMap) {
		if (!spielerMap.containsKey(spieler.getId())) {
			Spieler reportSpieler = new Spieler();
			reportSpieler.setId(spieler.getId());
			reportSpieler.setName(spieler.getName());
			EloPunktzahl punktzahl = new EloPunktzahl();
			punktzahl.setPunktzahl(KickerGlobalSettings.ELO_START);
			reportSpieler.setPunktzahl(punktzahl);
			spielerMap.put(spieler.getId(), reportSpieler);
		}
		return spielerMap.get(spieler.getId());
	}

	private List<Spiel> getSpieleFiltered(List<String> spielerNamen, Date start, Date ende) {
		return eloPersistence
				.createQuery(
						"select distinct s from Spiel s where s.zeitpunkt >= :start and s.zeitpunkt <= :ende "
								+ "and s.gewinner.spieler1.name in :spielerNamen and s.gewinner.spieler2.name in :spielerNamen "
								+ "and s.verlierer.spieler1.name in :spielerNamen and s.verlierer.spieler2.name in :spielerNamen "
								+ "order by s.zeitpunkt", Spiel.class).setParameter("spielerNamen", spielerNamen)
				.setParameter("start", start).setParameter("ende", ende).getResultList();
	}

	private EloPunktzahl addElo(EloPunktzahl current, EloCalculationResult eloResult, Date refDate) {
		EloPunktzahl eloPunktzahl = createEloPunktzahl(eloResult, refDate);
		eloPunktzahl.setPunktzahl(eloResult.getZuwachs());
		int eloPunkte = current.getPunktzahl();
		eloPunkte += eloResult.getZuwachs();
		if (eloPunkte < 0) {
			eloPunkte = 0;
		}
		eloPunktzahl.setPunktzahl(eloPunkte);
		return eloPunktzahl;
	}

	private EloPunktzahl subtractElo(EloPunktzahl current, EloCalculationResult eloResult, Date refDate) {
		EloPunktzahl eloPunktzahl = createEloPunktzahl(eloResult, refDate);
		eloPunktzahl.setPunktzahl(eloResult.getZuwachs());
		int eloPunkte = current.getPunktzahl();
		eloPunkte -= eloResult.getZuwachs();
		if (eloPunkte < 0) {
			eloPunkte = 0;
		}
		eloPunktzahl.setPunktzahl(eloPunkte);
		eloPunktzahl.setZuwachs(-1 * eloPunktzahl.getZuwachs());
		return eloPunktzahl;
	}

	private EloPunktzahl createEloPunktzahl(EloCalculationResult eloResult, Date refDate) {
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

	public void setEloPersistence(EloPersistence eloPersistence) {
		this.eloPersistence = eloPersistence;
	}

	public void setEloService(EloService eloService) {
		this.eloService = eloService;
	}

}
