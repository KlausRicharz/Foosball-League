package de.hbt.kicker.elo.v2.service;

public class EloCalculationResult {

	private final int zuwachs;
	private final int eloGewinner1;
	private final int eloGewinner2;
	private final int eloVerlierer1;
	private final int eloVerlierer2;
	private final double faktor;
	private final double erwartung;

	public EloCalculationResult(int zuwachs, int eloGewinner1, int eloGewinner2, int eloVerlierer1, int eloVerlierer2, double faktor,
			double erwartung) {
		this.zuwachs = zuwachs;
		this.eloGewinner1 = eloGewinner1;
		this.eloGewinner2 = eloGewinner2;
		this.eloVerlierer1 = eloVerlierer1;
		this.eloVerlierer2 = eloVerlierer2;
		this.faktor = faktor;
		this.erwartung = erwartung;
	}

	public int getZuwachs() {
		return zuwachs;
	}

	public double getFaktor() {
		return faktor;
	}

	public double getErwartung() {
		return erwartung;
	}

	public int getEloGewinner1() {
		return eloGewinner1;
	}

	public int getEloGewinner2() {
		return eloGewinner2;
	}

	public int getEloVerlierer1() {
		return eloVerlierer1;
	}

	public int getEloVerlierer2() {
		return eloVerlierer2;
	}

}
