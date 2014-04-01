package de.hbt.kicker.elo.v1.persistence;

import java.util.List;

import javax.persistence.EntityManager;

import de.hbt.kicker.elo.v1.model.SpielAlt;
import de.hbt.kicker.elo.v1.model.SpielerAlt;

public class EloPersistenceAlt {
	
	private EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public List<SpielAlt> findAllSpiele() {
		return entityManager.createQuery("select s from SpielAlt s order by s.id").getResultList();
	}
	
	public SpielerAlt findSpielerByName(String name) {
		return entityManager.find(SpielerAlt.class, name);
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
}
