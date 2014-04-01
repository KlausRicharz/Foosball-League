package de.hbt.kicker.elo.statistik;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.hbt.kicker.elo.v2.model.Spieler;

public class Flop3Statistik {

	private Map<Spieler, List<Integer>> einzelpunkteMap = new HashMap<Spieler, List<Integer>>();
	private Map<Spieler, Integer> punkteMap = new HashMap<Spieler, Integer>();
	private Spieler spieler1, spieler2, spieler3;
	private int punkte1, punkte2, punkte3;

	public void add(Spieler spieler, int punkte) {
		
		if(!spieler.isVisible() || !spieler.isTopFlopAktiviert()) {
			return;
		}
		
		List<Integer> punkteValue = einzelpunkteMap.get(spieler);
		if (punkteValue != null) {
			punkteValue.add(punkte);
		} else {
			punkteValue = new ArrayList<Integer>();
			punkteValue.add(punkte);
		}
		einzelpunkteMap.put(spieler, punkteValue);
	}

	public void calculate() {
		
		/* berechne Punktsummen */
		for (Iterator<Entry<Spieler, List<Integer>>> i = einzelpunkteMap.entrySet().iterator(); i.hasNext();) {
			Entry<Spieler, List<Integer>> entry = i.next();
			List<Integer> werte = entry.getValue();
			int summe = 0;
			for (Integer wert : werte) {
				summe += wert;
			}
			punkteMap.put(entry.getKey(), summe);
		}
		
		Set<Entry<Spieler, Integer>> entrySet = punkteMap.entrySet();
		
		for (Map.Entry<Spieler, Integer> entry : entrySet) {
			if (spieler1 == null) {
				spieler1 = entry.getKey();
				punkte1 = entry.getValue();
			} else if (punkte1 > entry.getValue()) {
				spieler1 = entry.getKey();
				punkte1 = entry.getValue();
			}
		}
		for (Map.Entry<Spieler, Integer> entry : entrySet) {
			if (entry.getKey() == spieler1) {
				continue;
			}
			if (spieler2 == null) {
				spieler2 = entry.getKey();
				punkte2 = entry.getValue();
			} else if (punkte2 > entry.getValue()) {
				spieler2 = entry.getKey();
				punkte2 = entry.getValue();
			}
		}
		for (Map.Entry<Spieler, Integer> entry : entrySet) {
			if (entry.getKey() == spieler1 || entry.getKey() == spieler2)
				continue;
			if (spieler3 == null) {
				spieler3 = entry.getKey();
				punkte3 = entry.getValue();
			} else if (punkte3 > entry.getValue()) {
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
