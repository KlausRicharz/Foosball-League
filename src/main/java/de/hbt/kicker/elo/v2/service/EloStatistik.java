package de.hbt.kicker.elo.v2.service;

public class EloStatistik {

	private final String label;
	private final long time; // epoch
	private final int punktzahl;

	public EloStatistik(String label, long time, int punktzahl) {
		this.label = label;
		this.time = time;
		this.punktzahl = punktzahl;
	}

	public String getLabel() {
		return label;
	}

	public int getPunktzahl() {
		return punktzahl;
	}

	public long getTime() {
		return time;
	}

}
