package de.hbt.kicker.elo.v2.service;

import static de.hbt.kicker.elo.util.FormatUtils.formatDouble;
import static de.hbt.kicker.elo.util.FormatUtils.formatPercent;

import java.util.Date;

import de.hbt.kicker.elo.v2.model.EloPunktzahl;
import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.model.Team;

public class GespieltesSpiel {

	public String getId() {
		return spiel.getId();
	}

	public Date getZeitpunkt() {
		return spiel.getZeitpunkt();
	}

	public Team getGewinner() {
		return spiel.getGewinner();
	}

	public Team getVerlierer() {
		return spiel.getVerlierer();
	}

	public int getToreGewinner() {
		return spiel.getToreGewinner();
	}

	public int getToreVerlierer() {
		return spiel.getToreVerlierer();
	}

	public EloPunktzahl getPunktzahl() {
		return spiel.getPunktzahl();
	}
	
	public EloPunktzahl getTeamPunktzahl() {
		return spiel.getTeamPunktzahl();
	}

	private final Spiel spiel;
	private final boolean gewonnen;

	public GespieltesSpiel(Spiel spiel, boolean gewonnen) {
		this.spiel = spiel;
		this.gewonnen = gewonnen;
	}

	public Spiel getSpiel() {
		return spiel;
	}

	public boolean isGewonnen() {
		return gewonnen;
	}

	public String getCalculationDetails() {
		double erwartung = spiel.getPunktzahl().getErwartung();
		if (!gewonnen) {
			erwartung = 1 - erwartung;
		}
		String params = "E=" + formatPercent(erwartung) + ",F=" + formatDouble(spiel.getPunktzahl().getFaktor());
		return params;
	}
	
	public String getTeamCalculationDetails() {
		double erwartung = spiel.getTeamPunktzahl().getErwartung();
		if (!gewonnen) {
			erwartung = 1 - erwartung;
		}
		String params = "E=" + formatPercent(erwartung) + ",F=" + formatDouble(spiel.getPunktzahl().getFaktor());
		return params;
	}

}
