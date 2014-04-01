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

public class LoosingSpreeStatistic implements Statistic {

	private Map<Spieler, Integer> maxLostInRow = new HashMap<Spieler, Integer>();
	private Map<Spieler, Integer> currentLostInRow = new HashMap<Spieler, Integer>();
	private final boolean nurAktuelle;
	private final Date aktivAb;

	public LoosingSpreeStatistic(boolean nurAktuelle, Date aktivAb) {
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
			if (currentLostInRow.containsKey(spieler)) {
				int counter = currentLostInRow.get(spieler);
				counter++;
				currentLostInRow.put(spieler, counter);
			} else {
				currentLostInRow.put(spieler, 1);
			}
		}
	}

	private void calcSpreeForSpieler(Spieler spieler) {
		if (currentLostInRow.containsKey(spieler)) {
			int counter = currentLostInRow.get(spieler);
			if (maxLostInRow.containsKey(spieler)) {
				int maxCounter = maxLostInRow.get(spieler);
				if (counter > maxCounter) {
					maxLostInRow.put(spieler, counter);
				}
			} else {
				maxLostInRow.put(spieler, counter);
			}
			currentLostInRow.remove(spieler);
		}
	}

	private void processGewinner(Spieler spieler) {
		if (spieler.isVisible()) {
			calcSpreeForSpieler(spieler);
		}
	}

	public List<StatistikMeldung> getMeldungen() {

		if (nurAktuelle) {
			maxLostInRow.clear();
		}

		Collection<Spieler> offeneSpieler = new HashSet<Spieler>(currentLostInRow.keySet());
		for (Spieler spieler : offeneSpieler) {
			calcSpreeForSpieler(spieler);
		}

		List<Map.Entry<Spieler, Integer>> entries = new ArrayList<Map.Entry<Spieler, Integer>>(maxLostInRow.entrySet());
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
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " liegt K.O. am Boden!", 1));
			} else if (entry.getValue() > 8) {
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " wird ziemlich vermöbelt!", 2));
			} else if (entry.getValue() > 6) {
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " kriegt's einfach nicht hin!", 3));
			} else if (entry.getValue() > 4) {
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " hat den falschen Weg eingeschlagen.", 4));
			} else {
				meldungen.add(new StatistikMeldung(entry.getKey(), entry.getKey().getName() + " hat 'nen Hänger.", 5));
			}
		}
		return meldungen;
	}

}
