package de.hbt.kicker.elo.v2.service;

import static de.hbt.kicker.elo.KickerGlobalSettings.ELO_START;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.commons.lang3.time.DateUtils;

import de.hbt.kicker.elo.v2.model.EloPunktzahl;
import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.model.Spieler;
import de.hbt.kicker.elo.v2.model.Team;
import de.hbt.kicker.elo.v2.persistence.EloPersistence;

public class SpielService {

	private static Logger LOG = Logger.getLogger(SpielService.class.getName());

	private EloPersistence eloPersistence;
	private EloService eloService;

	public void calculateEloAndPersistSpiel(Spiel spiel) {

		initElo(spiel.getGewinner(), spiel.getZeitpunkt());
		initElo(spiel.getVerlierer(), spiel.getZeitpunkt());

		EloCalculationResult spielerEloResult = eloService.calculateElo(spiel.getGewinner().getSpieler1().getPunktzahl().getPunktzahl(),
				spiel.getGewinner().getSpieler2().getPunktzahl().getPunktzahl(), spiel.getVerlierer().getSpieler1().getPunktzahl()
						.getPunktzahl(), spiel.getVerlierer().getSpieler2().getPunktzahl().getPunktzahl());

		EloCalculationResult teamEloResult = eloService.calculateElo(spiel.getGewinner().getPunktzahl().getPunktzahl(), spiel.getGewinner()
				.getPunktzahl().getPunktzahl(), spiel.getVerlierer().getPunktzahl().getPunktzahl(), spiel.getVerlierer().getPunktzahl()
				.getPunktzahl());

		EloPunktzahl p1 = spiel.getGewinner().getSpieler1().getPunktzahl();
		spiel.getGewinner().getSpieler1().setPunktzahl(addElo(p1, spielerEloResult, spiel.getZeitpunkt()));
		spiel.getGewinner().getSpieler1().setAnzahlSpiele(spiel.getGewinner().getSpieler1().getAnzahlSpiele() + 1);
		spiel.getGewinner().getSpieler1().setAnzahlSiege(spiel.getGewinner().getSpieler1().getAnzahlSiege() + 1);

		EloPunktzahl p2 = spiel.getGewinner().getSpieler2().getPunktzahl();
		spiel.getGewinner().getSpieler2().setPunktzahl(addElo(p2, spielerEloResult, spiel.getZeitpunkt()));
		spiel.getGewinner().getSpieler2().setAnzahlSpiele(spiel.getGewinner().getSpieler2().getAnzahlSpiele() + 1);
		spiel.getGewinner().getSpieler2().setAnzahlSiege(spiel.getGewinner().getSpieler2().getAnzahlSiege() + 1);

		EloPunktzahl p3 = spiel.getVerlierer().getSpieler1().getPunktzahl();
		spiel.getVerlierer().getSpieler1().setPunktzahl(subtractElo(p3, spielerEloResult, spiel.getZeitpunkt()));
		spiel.getVerlierer().getSpieler1().setAnzahlSpiele(spiel.getVerlierer().getSpieler1().getAnzahlSpiele() + 1);

		EloPunktzahl p4 = spiel.getVerlierer().getSpieler2().getPunktzahl();
		spiel.getVerlierer().getSpieler2().setPunktzahl(subtractElo(p4, spielerEloResult, spiel.getZeitpunkt()));
		spiel.getVerlierer().getSpieler2().setAnzahlSpiele(spiel.getVerlierer().getSpieler2().getAnzahlSpiele() + 1);

		spiel.getGewinner().setPunktzahl(addElo(spiel.getGewinner().getPunktzahl(), teamEloResult, spiel.getZeitpunkt()));
		spiel.getVerlierer().setPunktzahl(subtractElo(spiel.getVerlierer().getPunktzahl(), teamEloResult, spiel.getZeitpunkt()));

		spiel.setPunktzahl(createElo(spielerEloResult, spiel.getZeitpunkt()));
		spiel.setTeamPunktzahl(createElo(teamEloResult, spiel.getZeitpunkt()));

		eloPersistence.persist(spiel);
	}

	private void initElo(Team team, Date zeitpunkt) {
		zeitpunkt = DateUtils.addSeconds(zeitpunkt, -1);
		if (team.getPunktzahl() == null) {
			LOG.info("Init ELO for " + team.getSpieler1().getName() + "/" + team.getSpieler2().getName());
			EloPunktzahl elopunkte = new EloPunktzahl();
			elopunkte.setBerechnetAm(zeitpunkt);
			elopunkte.setPunktzahl(ELO_START);
			team.setPunktzahl(elopunkte);
		}
		initElo(team.getSpieler1(), zeitpunkt);
		initElo(team.getSpieler2(), zeitpunkt);
	}

	private void initElo(Spieler spieler, Date zeitpunkt) {
		if (spieler.getPunktzahl() == null) {
			LOG.info("Init ELO for " + spieler.getName());
			EloPunktzahl elopunkte = new EloPunktzahl();
			elopunkte.setBerechnetAm(zeitpunkt);
			elopunkte.setPunktzahl(ELO_START);
			spieler.setPunktzahl(elopunkte);
		}
	}

	public void recalculateElo(Date startDate) {

		boolean recalcAll = startDate == null;

		/* Elo-Berechnungen bei Spielern entfernen */
		List<Spieler> spielerliste = eloPersistence.findAll(Spieler.class);
		for (Spieler spieler : spielerliste) {
			if (recalcAll) {
				spieler.getPunktzahlHistorie().clear();
				spieler.setAnzahlSiege(0);
				spieler.setAnzahlSpiele(0);
				spieler.setPunktzahl(null);
			} else {
				Set<EloPunktzahl> historie = spieler.getPunktzahlHistorie();
				TreeSet<EloPunktzahl> sortiert = new TreeSet<EloPunktzahl>(new Comparator<EloPunktzahl>() {
					public int compare(EloPunktzahl o1, EloPunktzahl o2) {
						return o1.getBerechnetAm().compareTo(o2.getBerechnetAm());
					}
				});
				sortiert.addAll(historie);
				EloPunktzahl lastElo = null;
				boolean initialePunktzahl = true;
				for (EloPunktzahl eloPunktzahl : sortiert) {
					if (initialePunktzahl || eloPunktzahl.getBerechnetAm().before(startDate)) {
						lastElo = eloPunktzahl;
					}
					if (!eloPunktzahl.getBerechnetAm().before(startDate)) {
						if (!initialePunktzahl) {
							spieler.setAnzahlSpiele(spieler.getAnzahlSpiele() - 1);
							if (eloPunktzahl.getZuwachs() > 0) {
								spieler.setAnzahlSiege(spieler.getAnzahlSiege() - 1);
							}
						}
						historie.remove(eloPunktzahl);
					}
					initialePunktzahl = false;
				}
				spieler.setPunktzahl(lastElo);
			}
		}

		/* Elo-Berechnungen bei Teams entfernen */
		List<Team> teamliste = eloPersistence.findAll(Team.class);
		for (Team team : teamliste) {
			if (recalcAll) {
				team.getPunktzahlHistorie().clear();
				team.setPunktzahl(null);
			} else {
				Set<EloPunktzahl> historie = team.getPunktzahlHistorie();
				TreeSet<EloPunktzahl> sortiert = new TreeSet<EloPunktzahl>(new Comparator<EloPunktzahl>() {
					public int compare(EloPunktzahl o1, EloPunktzahl o2) {
						return o1.getBerechnetAm().compareTo(o2.getBerechnetAm());
					}
				});
				sortiert.addAll(historie);
				EloPunktzahl lastElo = null;
				boolean initialePunktzahl = true;
				for (EloPunktzahl eloPunktzahl : sortiert) {
					if (initialePunktzahl || eloPunktzahl.getBerechnetAm().before(startDate)) {
						lastElo = eloPunktzahl;
					}
					if (!eloPunktzahl.getBerechnetAm().before(startDate)) {
						historie.remove(eloPunktzahl);
					}
					initialePunktzahl = false;
				}
				team.setPunktzahl(lastElo);
			}
		}

		/* Elo je Spiel neu berechnen */
		List<Spiel> spiele = null;
		if (recalcAll) {
			spiele = eloPersistence.findAllSpieleNotBefore(new Date(0));
		} else {
			spiele = eloPersistence.findAllSpieleNotBefore(startDate);
		}
		for (Spiel spiel : spiele) {
			calculateEloAndPersistSpiel(spiel);
		}

		// for (Spieler spieler : spielerliste) {
		// verify(spieler);
		// }
		//
		// for (Team team : teamliste) {
		// verify(team);
		// }
	}

	public void removeSpiel(String spielId) {
		Spiel spiel = eloPersistence.findById(Spiel.class, spielId);
		eloPersistence.remove(spiel);
		recalculateElo(spiel.getZeitpunkt());
	}

	// private void verify(Spieler spieler) {
	// if (spieler.getPunktzahl() == null) {
	// throw new RuntimeException("Spieler hat keine Punktzahl: " +
	// spieler.getName());
	// }
	// Set<EloPunktzahl> punktzahlHistorie = spieler.getPunktzahlHistorie();
	// for (EloPunktzahl eloPunktzahl : punktzahlHistorie) {
	// if (eloPunktzahl == null) {
	// throw new RuntimeException("Spieler hat null Punktzahl in Historie: " +
	// spieler.getName());
	// }
	// }
	// }
	//
	// private void verify(Team team) {
	// if (team.getPunktzahl() == null) {
	// throw new RuntimeException("Team hat keine Punktzahl: " +
	// team.getSpieler1().getName() + "/" + team.getSpieler2().getName());
	// }
	// Set<EloPunktzahl> punktzahlHistorie = team.getPunktzahlHistorie();
	// for (EloPunktzahl eloPunktzahl : punktzahlHistorie) {
	// if (eloPunktzahl == null) {
	// throw new RuntimeException("Team hat null Punktzahl in Historie: " +
	// team.getSpieler1().getName() + "/"
	// + team.getSpieler2().getName());
	// }
	// }
	// }

	public Spiel findSpielById(String id) {
		return eloPersistence.findById(Spiel.class, id);
	}

	public List<GespieltesSpiel> findAllLetzteSpiele(int maxResults) {
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
				"order by s.zeitpunkt desc", Spiel.class).getResultList();

		List<GespieltesSpiel> result = new ArrayList<GespieltesSpiel>();
		for (Spiel spiel : alleSpiele) {
			boolean gewonnen = true;
			result.add(new GespieltesSpiel(spiel, gewonnen));
		}
		return result.subList(0, Math.min(result.size(), maxResults));
	}

	public void setEloPersistence(EloPersistence eloPersistence) {
		this.eloPersistence = eloPersistence;
	}

	public void setEloService(EloService eloService) {
		this.eloService = eloService;
	}

	private EloPunktzahl createElo(EloCalculationResult eloResult, Date refDate) {
		EloPunktzahl eloPunktzahl = createEloPunktzahl(eloResult, refDate);
		eloPunktzahl.setPunktzahl(eloResult.getZuwachs());
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

}
