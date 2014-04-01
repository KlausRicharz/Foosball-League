package de.hbt.kicker.web;

import java.util.Date;
import java.util.HashSet;

import org.apache.commons.lang3.time.DateUtils;

import de.hbt.kicker.elo.GlobalLock;
import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.model.Spieler;
import de.hbt.kicker.elo.v2.model.Team;
import de.hbt.kicker.elo.v2.service.SpielService;
import de.hbt.kicker.elo.v2.service.SpielerService;
import de.hbt.kicker.elo.v2.service.TeamService;

public class SpielFormular extends AbstractFormular {

	private String gewinner1;
	private String gewinner2;
	private String verlierer1;
	private String verlierer2;

	private String toreTeamA1;
	private String toreTeamA2;
	private String toreTeamB1;
	private String toreTeamB2;

	private String spielId;

	private SpielerService spielerService;
	private SpielService spielService;
	private TeamService teamService;

	@Override
	protected void performAction() {
		synchronized (GlobalLock.WRITE_LOCK) {
			Spieler t1s1 = spielerService.findSpielerByName(gewinner1);
			Spieler t1s2 = spielerService.findSpielerByName(gewinner2);
			Spieler t2s1 = spielerService.findSpielerByName(verlierer1);
			Spieler t2s2 = spielerService.findSpielerByName(verlierer2);

			Date spielDatum = new Date();

			Team t1 = teamService.findOrCreateTeam(t1s1, t1s2, spielDatum);
			Team t2 = teamService.findOrCreateTeam(t2s1, t2s2, spielDatum);

			Team gewinner1 = null;
			Team verlierer1 = null;
			int toreGewinner1 = -1;
			int toreVerlierer1 = -1;
			Team gewinner2 = null;
			Team verlierer2 = null;
			int toreGewinner2 = -1;
			int toreVerlierer2 = -1;
			if (Integer.parseInt(toreTeamA1) > Integer.parseInt(toreTeamB1)) {
				gewinner1 = t1;
				verlierer1 = t2;
				toreGewinner1 = Integer.parseInt(toreTeamA1);
				toreVerlierer1 = Integer.parseInt(toreTeamB1);
			} else {
				gewinner1 = t2;
				verlierer1 = t1;
				toreGewinner1 = Integer.parseInt(toreTeamB1);
				toreVerlierer1 = Integer.parseInt(toreTeamA1);

			}
			if (toreTeamA2 != null && toreTeamA2.length() > 0) {
				if (Integer.parseInt(toreTeamA2) > Integer.parseInt(toreTeamB2)) {
					gewinner2 = t1;
					verlierer2 = t2;
					toreGewinner2 = Integer.parseInt(toreTeamA2);
					toreVerlierer2 = Integer.parseInt(toreTeamB2);
				} else {
					gewinner2 = t2;
					verlierer2 = t1;
					toreGewinner2 = Integer.parseInt(toreTeamB2);
					toreVerlierer2 = Integer.parseInt(toreTeamA2);
				}
			}

			if (spielId == null || spielId.length() == 0) {
				Spiel spiel = new Spiel();
				spiel.setZeitpunkt(spielDatum);
				spiel.setGewinner(gewinner1);
				spiel.setVerlierer(verlierer1);
				spiel.setToreGewinner(toreGewinner1);
				spiel.setToreVerlierer(toreVerlierer1);
				spielService.calculateEloAndPersistSpiel(spiel);
				if (toreTeamA2 != null && toreTeamA2.length() > 0) {
					spiel = new Spiel();
					spielDatum = DateUtils.addSeconds(spielDatum, 1);
					spiel.setZeitpunkt(spielDatum);
					spiel.setGewinner(gewinner2);
					spiel.setVerlierer(verlierer2);
					spiel.setToreGewinner(toreGewinner2);
					spiel.setToreVerlierer(toreVerlierer2);
					spielService.calculateEloAndPersistSpiel(spiel);
				}
			} else {
				boolean eloGeaendert = false;
				Spiel spiel = spielService.findSpielById(spielId);
				eloGeaendert |= (gewinner1 != spiel.getGewinner());
				eloGeaendert |= (verlierer1 != spiel.getVerlierer());

				spiel.setGewinner(gewinner1);
				spiel.setVerlierer(verlierer1);
				spiel.setToreGewinner(toreGewinner1);
				spiel.setToreVerlierer(toreVerlierer1);
				if (eloGeaendert) {
					spielService.recalculateElo(spiel.getZeitpunkt());
				}
			}
		}
	}

	@Override
	protected boolean validate() {
		HashSet<String> playerNames = new HashSet<String>();
		playerNames.add(this.gewinner1);
		playerNames.add(this.gewinner2);
		playerNames.add(this.verlierer1);
		playerNames.add(this.verlierer2);

		if (playerNames.size() != 4) {
			getErrorMessages().add("Bitte 4 verschiedene Spieler angeben.");
		}

		validateErgebnis(toreTeamA1, toreTeamB1);
		if (toreTeamA2 != null && toreTeamA2.length() > 0) {
			validateErgebnis(toreTeamA2, toreTeamB2);
		}

		return getErrorMessages().isEmpty();
	}

	private void validateErgebnis(String toreA, String toreB) {
		try {
			int a = Integer.parseInt(toreA);
			int b = Integer.parseInt(toreB);
			if (a < 0 || a > 10 || b < 0 || b > 10 || a == b) {
				getErrorMessages().add(
						"Tore/Ergenis machen keinen Sinn. Es muss einen Gewinner geben und es können maximal 10 Tore geschossen werden.");
			}
		} catch (NumberFormatException e) {
			getErrorMessages().add("Ungültige Tore eingegeben.");
		}
	}

	public String getGewinner1() {
		return gewinner1;
	}

	public void setGewinner1(String gewinner1) {
		this.gewinner1 = gewinner1;
	}

	public String getGewinner2() {
		return gewinner2;
	}

	public void setGewinner2(String gewinner2) {
		this.gewinner2 = gewinner2;
	}

	public String getVerlierer1() {
		return verlierer1;
	}

	public void setVerlierer1(String verlierer1) {
		this.verlierer1 = verlierer1;
	}

	public String getVerlierer2() {
		return verlierer2;
	}

	public void setVerlierer2(String verlierer2) {
		this.verlierer2 = verlierer2;
	}

	public String getSpielId() {
		return spielId;
	}

	public void setSpielId(String spielId) {
		this.spielId = spielId;
	}

	public void setSpielerService(SpielerService spielerService) {
		this.spielerService = spielerService;
	}

	public void setSpielService(SpielService spielService) {
		this.spielService = spielService;
	}

	public void setTeamService(TeamService teamService) {
		this.teamService = teamService;
	}

	public String getToreTeamA1() {
		return toreTeamA1;
	}

	public void setToreTeamA1(String toreTeamA1) {
		this.toreTeamA1 = toreTeamA1;
	}

	public String getToreTeamA2() {
		return toreTeamA2;
	}

	public void setToreTeamA2(String toreTeamA2) {
		this.toreTeamA2 = toreTeamA2;
	}

	public String getToreTeamB1() {
		return toreTeamB1;
	}

	public void setToreTeamB1(String toreTeamB1) {
		this.toreTeamB1 = toreTeamB1;
	}

	public String getToreTeamB2() {
		return toreTeamB2;
	}

	public void setToreTeamB2(String toreTeamB2) {
		this.toreTeamB2 = toreTeamB2;
	}

}
