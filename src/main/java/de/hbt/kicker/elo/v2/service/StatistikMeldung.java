package de.hbt.kicker.elo.v2.service;

import de.hbt.kicker.elo.v2.model.Spieler;

public class StatistikMeldung implements Comparable<StatistikMeldung> {

	private final Spieler spieler;
	private final String meldung;
	/** 1 = hoch, 99 = niedrig */
	private int prioritaet;

	public StatistikMeldung(Spieler spieler, String meldung, int prioritaet) {
		this.spieler = spieler;
		this.meldung = meldung;
		this.prioritaet = prioritaet;
	}

	public Spieler getSpieler() {
		return spieler;
	}

	public String getMeldung() {
		return meldung;
	}

	public int compareTo(StatistikMeldung o) {
		return prioritaet - o.prioritaet;
	}
	
	

}
