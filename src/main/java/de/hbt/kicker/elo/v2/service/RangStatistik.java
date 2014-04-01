package de.hbt.kicker.elo.v2.service;

public class RangStatistik {

	private final String label;
	private final long time; // epoch
	private final int rang;

	public RangStatistik(String label, long time, int rang) {
		this.label = label;
		this.rang = rang;
		this.time = time;
	}

	public String getLabel() {
		return label;
	}

	public int getRang() {
		return rang;
	}

	public long getTime() {
		return time;
	}

}
