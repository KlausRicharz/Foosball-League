package de.hbt.kicker.elo.v2.service.crazystats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.model.Spieler;
import de.hbt.kicker.elo.v2.service.StatistikMeldung;

public class WinningSpreeStatistic implements Statistic {

	private Map<Spieler, Integer> maxWonInRow = new HashMap<Spieler, Integer>();
	private Map<Spieler, Integer> currentWonInRow = new HashMap<Spieler, Integer>();
	private final boolean nurAktuelle;
	private final Date aktivAb;

	public WinningSpreeStatistic(boolean nurAktuelle, Date aktivAb) {
		this.nurAktuelle = nurAktuelle;
		this.aktivAb = aktivAb;
	}

	public void addSpiel(Spiel spiel) {
		processGewinner(spiel.getGewinner().getSpieler1());
		processGewinner(spiel.getGewinner().getSpieler2());
		processVerlierer(spiel.getVerlierer().getSpieler1());
		processVerlierer(spiel.getVerlierer().getSpieler2());
	}

	private void processVerlierer(Spieler spieler) {
		if (spieler.isVisible()) {
			calcSpreeForSpieler(spieler);
		}
	}

	private void calcSpreeForSpieler(Spieler spieler) {
		if (currentWonInRow.containsKey(spieler)) {
			int counter = currentWonInRow.get(spieler);
			if (maxWonInRow.containsKey(spieler)) {
				int maxCounter = maxWonInRow.get(spieler);
				if (counter > maxCounter) {
					maxWonInRow.put(spieler, counter);
				}
			} else {
				maxWonInRow.put(spieler, counter);
			}
			currentWonInRow.remove(spieler);
		}
	}

	private void processGewinner(Spieler spieler) {
		if (spieler.isVisible()) {
			if (currentWonInRow.containsKey(spieler)) {
				int counter = currentWonInRow.get(spieler);
				counter++;
				currentWonInRow.put(spieler, counter);
			} else {
				currentWonInRow.put(spieler, 1);
			}
		}
	}

	public List<StatistikMeldung> getMeldungen() {

		if (nurAktuelle) {
			maxWonInRow.clear();
		}

		Collection<Spieler> offeneSpieler = new HashSet<Spieler>(currentWonInRow.keySet());
		for (Spieler spieler : offeneSpieler) {
			calcSpreeForSpieler(spieler);
		}

		List<Map.Entry<Spieler, Integer>> entries = new ArrayList<Map.Entry<Spieler, Integer>>(maxWonInRow.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<Spieler, Integer>>() {
			public int compare(Entry<Spieler, Integer> o1, Entry<Spieler, Integer> o2) {
				return -1 * o1.getValue().compareTo(o2.getValue());
			}
		});

		List<StatistikMeldung> meldungen = new ArrayList<StatistikMeldung>();

		for (Entry<Spieler, Integer> entry : entries) {
			Date berechnetAm = entry.getKey().getPunktzahl().getBerechnetAm();
			if (berechnetAm.before(aktivAb))
				continue;
			if (entry.getValue() < 3)
				continue;
			
			if (entry.getValue() > 10) {
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " ist simply unstoppable!", 1));
			} else if (entry.getValue() > 8) {
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " kennt keine Gnade!", 2));
			} else if (entry.getValue() > 6) {
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " geht ab!", 3));
			} else if (entry.getValue() > 4) {
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " schnuppert Höhenluft.", 4));
			} else {
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " hat's drauf.", 5));
			}
		}
		return meldungen;
	}

}
