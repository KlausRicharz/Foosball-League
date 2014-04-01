package de.hbt.kicker.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import de.hbt.kicker.elo.migration.EloCalculationMigration;
import de.hbt.kicker.elo.migration.KickerV1Migration;
import de.hbt.kicker.elo.v2.persistence.EloPersistence;
import de.hbt.kicker.elo.v2.service.EloService;
import de.hbt.kicker.elo.v2.service.SpielService;

public class KickerWebLifecycle implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sce) {
		/* start database */
		HsqldbManager dbmanager = new HsqldbManager();
		dbmanager.setPort(9001);
		dbmanager.setName("elo");
		dbmanager.setPath("file:/var/lib/hsqldb/kicker");
		sce.getServletContext().setAttribute(HsqldbManager.class.getName(), dbmanager);
		dbmanager.start();
		
		/* init JPA/hibernate */
		Map<String, String> props = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Enumeration<String> parameterNames = sce.getServletContext().getInitParameterNames();
		while (parameterNames.hasMoreElements()) {
			String name = parameterNames.nextElement();
			props.put(name, sce.getServletContext().getInitParameter(name));
		}
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("elo", props);
		sce.getServletContext().setAttribute(EntityManagerFactory.class.getName(), factory);
	
		performMigrations(factory);
	}

	private void performMigrations(EntityManagerFactory factory) {
		EntityManager entityManager = factory.createEntityManager();
		EntityTransaction transaction = entityManager.getTransaction();
		EloPersistence eloPersistence = new EloPersistence();
		eloPersistence.setEntityManager(entityManager);
		
		SpielService spielService = new SpielService();
		spielService.setEloPersistence(eloPersistence);
		EloService eloService = new EloService();
		spielService.setEloService(eloService);
		
		KickerV1Migration kickerV1Migration = new KickerV1Migration();
		kickerV1Migration.setEloPersistence(eloPersistence);
		
		EloCalculationMigration eloCalculationMigration = new EloCalculationMigration();
		eloCalculationMigration.setEloPersistence(eloPersistence);
		eloCalculationMigration.setSpielService(spielService);
		try {
			transaction.begin();
			kickerV1Migration.migrate();
			eloCalculationMigration.migrate();
			transaction.commit();
		} finally {
			if(transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
		}
		
		entityManager.close();
	}

	public void contextDestroyed(ServletContextEvent sce) {
		/* stop JPA/hibernate */
		EntityManagerFactory factory = (EntityManagerFactory) sce.getServletContext().getAttribute(EntityManagerFactory.class.getName());
		if(factory != null) {
			try {
				factory.close();
				sce.getServletContext().removeAttribute(EntityManagerFactory.class.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		/* stop database */
		HsqldbManager dbmanager = (HsqldbManager) sce.getServletContext().getAttribute(HsqldbManager.class.getName());
		if(dbmanager != null) {
			try {
				dbmanager.stop();
				sce.getServletContext().removeAttribute(HsqldbManager.class.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
