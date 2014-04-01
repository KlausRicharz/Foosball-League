package de.hbt.kicker.elo.statistik;

import java.util.HashMap;
import java.util.Map;

import de.hbt.kicker.elo.v2.model.Spieler;

public class Top3Statistik {

	private Map<Spieler, Integer> punkteMap = new HashMap<Spieler, Integer>();
	private Spieler spieler1, spieler2, spieler3;
	private int punkte1, punkte2, punkte3;

	public void add(Spieler spieler, int punkte) {
		if(!spieler.isVisible() || !spieler.isTopFlopAktiviert()) {
			return;
		}
		Integer punkteValue = punkteMap.get(spieler);
		if (punkteValue != null) {
			punkteValue = punkteValue + punkte;
		} else {
			punkteValue = punkte;
		}
		punkteMap.put(spieler, punkteValue);
	}

	public void calculate() {
		for (Map.Entry<Spieler, Integer> entry : punkteMap.entrySet()) {
			if (spieler1 == null) {
				spieler1 = entry.getKey();
				punkte1 = entry.getValue();
			} else if (punkte1 < entry.getValue()) {
				spieler1 = entry.getKey();
				punkte1 = entry.getValue();
			}
		}
		for (Map.Entry<Spieler, Integer> entry : punkteMap.entrySet()) {
			if (entry.getKey() == spieler1) {
				continue;
			}
			if (spieler2 == null) {
				spieler2 = entry.getKey();
				punkte2 = entry.getValue();
			} else if (punkte2 < entry.getValue()) {
				spieler2 = entry.getKey();
				punkte2 = entry.getValue();
			}
		}
		for (Map.Entry<Spieler, Integer> entry : punkteMap.entrySet()) {
			if (!entry.getKey().isVisible() || entry.getKey() == spieler1 || entry.getKey() == spieler2)
				continue;
			if (spieler3 == null) {
				spieler3 = entry.getKey();
				punkte3 = entry.getValue();
			} else if (punkte3 < entry.getValue()) {
				spieler3 = entry.getKey();
				punkte3 = entry.getValue();
			}
		}
	}

	public Spieler getSpieler1() {
		return spieler1;
	}

	public Spieler getSpieler2() {
		return spieler2;
	}

	public Spieler getSpieler3() {
		return spieler3;
	}

	public int getPunkte1() {
		return punkte1;
	}

	public int getPunkte2() {
		return punkte2;
	}

	public int getPunkte3() {
		return punkte3;
	}

}
