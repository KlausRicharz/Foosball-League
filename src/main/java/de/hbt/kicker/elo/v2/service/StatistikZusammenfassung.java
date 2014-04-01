package de.hbt.kicker.elo.v2.service;


public class StatistikZusammenfassung<T> {

	private final T data;
	private final int spiele;
	private final int siege;
	private final float siegeProzent;
	private final int toreGeschossen;
	private final int toreGefangen;
	private final float toreGeschossenProSpiel;
	private final float toreGefangenProSpiel;
	private final int eloMinimum;
	private final int eloMaximum;

	public StatistikZusammenfassung(T data, int spiele, int siege, int toreGeschossen, int toreGefangen, int eloMinimum, int eloMaximum) {
		this.data = data;
		this.spiele = spiele;
		this.siege = siege;
		this.toreGeschossen = toreGeschossen;
		this.toreGefangen = toreGefangen;
		this.eloMinimum = eloMinimum;
		this.eloMaximum = eloMaximum;

		this.siegeProzent = 100f * siege / spiele;
		this.toreGeschossenProSpiel = 1f * toreGeschossen / spiele;
		this.toreGefangenProSpiel = 1f * toreGefangen / spiele;
	}

	public int getSpiele() {
		return spiele;
	}

	public int getSiege() {
		return siege;
	}

	public float getSiegeProzent() {
		return siegeProzent;
	}

	public int getToreGeschossen() {
		return toreGeschossen;
	}

	public int getToreGefangen() {
		return toreGefangen;
	}

	public float getToreGeschossenProSpiel() {
		return toreGeschossenProSpiel;
	}

	public float getToreGefangenProSpiel() {
		return toreGefangenProSpiel;
	}

	public int getEloMinimum() {
		return eloMinimum;
	}

	public int getEloMaximum() {
		return eloMaximum;
	}

	public T getData() {
		return data;
	}

}
