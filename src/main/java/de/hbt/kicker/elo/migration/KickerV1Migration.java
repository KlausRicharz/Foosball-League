package de.hbt.kicker.elo.migration;

import de.hbt.kicker.elo.Migration;

public class KickerV1Migration extends AbstractMigration {

	@Override
	protected void performMigration() {
		Migration.main(new String[0]);		
	}

}
