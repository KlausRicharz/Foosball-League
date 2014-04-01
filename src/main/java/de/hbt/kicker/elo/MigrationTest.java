package de.hbt.kicker.elo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import de.hbt.kicker.elo.v1.persistence.EloPersistenceAlt;
import de.hbt.kicker.elo.v2.model.EloPunktzahl;
import de.hbt.kicker.elo.v2.model.Spieler;
import de.hbt.kicker.elo.v2.persistence.EloPersistence;
import de.hbt.kicker.elo.v2.service.EloService;
import de.hbt.kicker.elo.v2.service.LegacyEloCalculationStrategy;
import de.hbt.kicker.elo.v2.service.SpielService;
import de.hbt.kicker.elo.v2.service.SpielerService;
import de.hbt.kicker.elo.v2.service.TeamService;

public class MigrationTest {

	private static EntityManager entityManager;
	private static Map<String, List<Integer>> alteEloWerte = new HashMap<String, List<Integer>>();

	public static void main(String[] args) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("elo");
		entityManager = factory.createEntityManager();
		entityManager.getTransaction().begin();

		initAlteElo(entityManager);

		EloPersistenceAlt epa = new EloPersistenceAlt();
		epa.setEntityManager(entityManager);
		EloPersistence ep = new EloPersistence();
		ep.setEntityManager(entityManager);
		EloService es = new EloService();
		es.setEloCalculationStrategy(new LegacyEloCalculationStrategy());
		SpielerService ssv = new SpielerService();
		ssv.setEloPersistence(ep);
		SpielService sps = new SpielService();
		sps.setEloPersistence(ep);
		sps.setEloService(es);
		TeamService ts = new TeamService();
		ts.setEloPersistence(ep);

		String spielerName = args[0];
		Spieler spieler = ssv.findSpielerByName(spielerName);
		Set<EloPunktzahl> punktzahlHistorie = spieler.getPunktzahlHistorie();
		List<EloPunktzahl> punktzahlListe = new ArrayList<EloPunktzahl>(punktzahlHistorie);
		Collections.sort(punktzahlListe, new Comparator<EloPunktzahl>() {
			public int compare(EloPunktzahl o1, EloPunktzahl o2) {
				return o1.getBerechnetAm().compareTo(o2.getBerechnetAm());
			}
		});

		List<Integer> altePunktzahlListe = alteEloWerte.get(spielerName);

		if (punktzahlListe.size() != altePunktzahlListe.size()) {
			System.out.println("ungleiche anzahl von Einträgen! " + punktzahlListe.size() + "/" + altePunktzahlListe.size());
		}

		for (int i = 0; i < punktzahlListe.size(); i++) {
			EloPunktzahl punktzahl = punktzahlListe.get(i);
			int altePunktzahl = altePunktzahlListe.get(i);
			if (punktzahl.getPunktzahl() != altePunktzahl) {
				System.out.println("Diff! " + punktzahl.getPunktzahl() + "/" + altePunktzahl + " - " + punktzahl.getBerechnetAm() + " / "
						+ punktzahl.getZuwachs());
			}
		}

		entityManager.getTransaction().rollback();

	}

	private static void initAlteElo(EntityManager entityManager) {
		@SuppressWarnings("unchecked")
		List<Object[]> resultList = entityManager.createNativeQuery("select s.NAME, e.ELOWERT, e.id " + //
				"from ELOWERTE e join ENTRY s ON (e.SPIELERID = s.ID) " + //
				"order by s.name, e.id").getResultList();
		String spielerName = "";
		List<Integer> eloWerte = null;
		for (Object[] row : resultList) {
			if (!spielerName.equals(row[0])) {
				spielerName = (String) row[0];
				eloWerte = new ArrayList<Integer>();
				eloWerte.add(KickerGlobalSettings.ELO_START);
				alteEloWerte.put(spielerName, eloWerte);
			}
			eloWerte.add((Integer) row[1]);
		}
	}

}
