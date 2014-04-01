package de.hbt.kicker.elo.v2.service.crazystats;

import java.util.List;

import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.service.StatistikMeldung;

public interface Statistic {
	
	void addSpiel(Spiel spiel);
	List<StatistikMeldung> getMeldungen();

}
