package de.hbt.kicker.elo.v2.service;

import de.hbt.kicker.elo.util.FormatUtils;

public class RanglistenEintrag {

	private final String id;
	private int rang;
	private final String name;
	private final int punktzahl;
	private final int spiele;
	private final int siege;

	public RanglistenEintrag(String id, int rang, String name, int punktzahl, int spiele, int siege) {
		this.id = id;
		this.rang = rang;
		this.name = name;
		this.punktzahl = punktzahl;
		this.spiele = spiele;
		this.siege = siege;
	}
	
	public int getRang() {
		return rang;
	}

	public String getName() {
		return name;
	}

	public int getPunktzahl() {
		return punktzahl;
	}

	public int getSpiele() {
		return spiele;
	}

	public int getSiege() {
		return siege;
	}

	public String getId() {
		return id;
	}

	public String getSiegeProzent() {
		return FormatUtils.formatPercent((double) siege / (double) spiele);
	}



	public void setRang(int rang) {
		this.rang = rang;
	}

}
