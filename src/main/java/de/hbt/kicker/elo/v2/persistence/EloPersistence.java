package de.hbt.kicker.elo.v2.persistence;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.hbt.kicker.elo.v2.model.EloModelElement;
import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.model.Spieler;
import de.hbt.kicker.elo.v2.model.Team;

public class EloPersistence {

	private EntityManager entityManager;

	public void persist(EloModelElement element) {
		if(entityManager.contains(element)) {
			return; // bereits managed, silent ignore ;-)
		}
		entityManager.persist(element);
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	public <T extends EloModelElement> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		return entityManager.createQuery(qlString, resultClass);
	}

	public <T extends EloModelElement> List<T> findAll(Class<T> type) {
		return entityManager.createQuery("select m from " + type.getSimpleName() + " m", type).getResultList();
	}

	public <T extends EloModelElement> List<T> findAll(Class<T> type, int maxResults, String additionalQueryString) {
		return entityManager.createQuery("select m from " + type.getSimpleName() + " m " + additionalQueryString, type).setMaxResults(maxResults)
				.getResultList();
	}

	public <T extends EloModelElement> T findById(Class<T> type, String id) {
		return entityManager.find(type, id);
	}

	public Team findTeamBySpieler(Spieler spieler1, Spieler spieler2) {
		List<Team> matches = entityManager
				.createQuery(
						"select t from Team t " + "where (t.spieler1 = :sp1 and t.spieler2 = :sp2) "
								+ "or (t.spieler1 = :sp2 and t.spieler2 = :sp1)", Team.class).setParameter("sp1", spieler1)
				.setParameter("sp2", spieler2).getResultList();
		if (matches.size() == 1) {
			return matches.get(0);
		}
		if (matches.size() > 1) {
			throw new RuntimeException("horrible!!! size=" + matches.size());
		}
		return null;
	}

	public Spieler findSpielerByName(String spielerName) {
		List<Spieler> matches = entityManager.createQuery("select s from Spieler s " + "where s.name = :name", Spieler.class)
				.setParameter("name", spielerName).getResultList();
		if (matches.size() == 1) {
			return matches.get(0);
		}
		if (matches.size() > 1) {
			throw new RuntimeException("horrible!!! size=" + matches.size());
		}
		return null;
	}

	public List<Spiel> findAllSpieleNotBefore(Date zeitpunkt) {
		return entityManager.createQuery("select s from Spiel s where s.zeitpunkt >= :zeitpunkt order by s.zeitpunkt", Spiel.class).setParameter("zeitpunkt", zeitpunkt)
				.getResultList();
	}

	public void remove(EloModelElement element) {
		entityManager.remove(element);
	}

	public void flush() {
		entityManager.flush();
	}

}
