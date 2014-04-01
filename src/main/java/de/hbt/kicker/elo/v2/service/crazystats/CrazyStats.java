package de.hbt.kicker.elo.v2.service.crazystats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.service.StatistikMeldung;

public class CrazyStats {
	
	private List<Spiel> spiele = new ArrayList<Spiel>();
	private List<StatistikMeldung> meldungen = new ArrayList<StatistikMeldung>();
	private final Statistic[] statistics;
	
	public CrazyStats(Statistic... sta) {
		this.statistics = sta;
	}
	
	public void addSpiel(Spiel spiel) {
		spiele.add(spiel);
	}
	
	public void addAllSpiele(Collection<Spiel> spiele) {
		this.spiele.addAll(spiele);
	}

	public List<StatistikMeldung> getMeldungen() {
		return meldungen ;
	}

	public void calculate() {
		
		meldungen.clear();
		
		Collections.sort(spiele, new Comparator<Spiel>() {
			public int compare(Spiel o1, Spiel o2) {
				return o1.getZeitpunkt().compareTo(o2.getZeitpunkt());
			}
		});
		
		for (Spiel spiel : spiele) {
			for (Statistic stat : statistics) {
				stat.addSpiel(spiel);
			}
		}
		
		for (Statistic stat : statistics) {
			meldungen.addAll(stat.getMeldungen());
		}
	}

}
