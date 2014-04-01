package de.hbt.kicker.elo.migration;

import java.util.List;
import java.util.logging.Logger;

import de.hbt.kicker.elo.v2.model.DbChange;
import de.hbt.kicker.elo.v2.persistence.EloPersistence;

public abstract class AbstractMigration {
	
	private Logger log = Logger.getLogger(getClass().getName());

	protected EloPersistence eloPersistence;

	public void migrate() {
		boolean needsMigration = true;
		List<DbChange> changes = eloPersistence.findAll(DbChange.class);
		for (DbChange change : changes) {
			if (change.getChange().equals(getClass().getName())) {
				needsMigration = false;
			}
		}
		log.info("Needs migration: " + needsMigration);
		if (needsMigration) {
			log.info("Running migration " + getClass().getName() + " ...");
			long start = System.currentTimeMillis();
			performMigration();
			DbChange change = new DbChange();
			change.setChange(getClass().getName());
			eloPersistence.persist(change);
			eloPersistence.flush();
			long ms = System.currentTimeMillis() - start;
			log.info("Migration complete. Took " + ms + "ms.");
		}
	}

	protected abstract void performMigration();

	public void setEloPersistence(EloPersistence eloPersistence) {
		this.eloPersistence = eloPersistence;
	}

}
