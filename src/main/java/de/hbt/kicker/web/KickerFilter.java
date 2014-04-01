package de.hbt.kicker.web;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import de.hbt.kicker.elo.v2.persistence.EloPersistence;
import de.hbt.kicker.elo.v2.service.EloService;
import de.hbt.kicker.elo.v2.service.ReportService;
import de.hbt.kicker.elo.v2.service.SpielService;
import de.hbt.kicker.elo.v2.service.SpielerService;
import de.hbt.kicker.elo.v2.service.StatistikService;
import de.hbt.kicker.elo.v2.service.TeamService;

public class KickerFilter implements Filter {
	
	private EntityManagerFactory factory;

	public void init(FilterConfig filterConfig) throws ServletException {
		factory = (EntityManagerFactory) filterConfig.getServletContext().getAttribute(EntityManagerFactory.class.getName());
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		EntityManager entityManager = null;
		try {
			entityManager = factory.createEntityManager();
			EloPersistence persistence = new EloPersistence();
			persistence.setEntityManager(entityManager);
			SpielerService spielerService = new SpielerService();
			spielerService.setEloPersistence(persistence);
			SpielService spielService = new SpielService();
			spielService.setEloPersistence(persistence);
			EloService eloService = new EloService();
			spielService.setEloService(eloService);
			StatistikService statistikService = new StatistikService();
			statistikService.setEloPersistence(persistence);
			TeamService teamService = new TeamService();
			teamService.setEloPersistence(persistence);
			ReportService reportService = new ReportService();
			reportService.setEloPersistence(persistence);
			reportService.setEloService(eloService);
			request.setAttribute("spielService", spielService);
			request.setAttribute("spielerService", spielerService);
			request.setAttribute("eloService", eloService);
			request.setAttribute("statistikService", statistikService);
			request.setAttribute("teamService", teamService);
			request.setAttribute("reportService", reportService);
			entityManager.getTransaction().begin();
			
//			persistence.findAll(Spieler.class, Integer.MAX_VALUE, " left join fetch m.punktzahlHistorie");
//			persistence.findAll(Team.class, Integer.MAX_VALUE, " left join fetch m.punktzahlHistorie");
//			persistence.findAll(Spiel.class);
//			persistence.findAll(EloPunktzahl.class);
			
			chain.doFilter(request, response);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			if(entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			throw new IOException(e);
		} finally {
			request.removeAttribute("spielService");
			request.removeAttribute("spielerService");
			request.removeAttribute("eloService");
			request.removeAttribute("statistikService");
			request.removeAttribute("teamService");
			request.removeAttribute("reportService");
			if(entityManager != null) {
				if(entityManager.getTransaction() != null && entityManager.getTransaction().isActive()) {
					entityManager.getTransaction().rollback();
				}
				entityManager.close();
			}
		}
	}

	public void destroy() {
		factory = null;
	}

}
