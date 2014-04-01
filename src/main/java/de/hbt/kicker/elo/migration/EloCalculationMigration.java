package de.hbt.kicker.elo.migration;

import de.hbt.kicker.elo.v2.service.SpielService;

public class EloCalculationMigration extends AbstractMigration {

	private SpielService spielService;
	
	@Override
	protected void performMigration() {
		spielService.recalculateElo(null);
	}

	public void setSpielService(SpielService spielService) {
		this.spielService = spielService;
	}

}
