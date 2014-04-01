package de.hbt.kicker.elo.v2.service;

import static de.hbt.kicker.elo.KickerGlobalSettings.ELO_EXPERTE;
import static de.hbt.kicker.elo.KickerGlobalSettings.ELO_FAKTOR_EXPERTE;
import static de.hbt.kicker.elo.KickerGlobalSettings.ELO_FAKTOR_STANDARD;

public class EloService {

	private EloCalculationStrategy eloCalculationStrategy = new DefaultEloCalculationStrategy();

	public EloCalculationResult calculateElo(int eloGewinner1, int eloGewinner2, int eloVerlierer1, int eloVerlierer2) {
		
		double eloGewinnerD = eloGewinner1 + eloGewinner2;
		int eloGewinner = (int) Math.floor(eloGewinnerD / 2);
		double eloVerliererD = eloVerlierer1 + eloVerlierer2;
		int eloVerlierer = (int) Math.floor(eloVerliererD / 2);

		double erwartung = eloCalculationStrategy.calculateErwartungswert(eloGewinner, eloVerlierer);
		int punkte = 1;
		double faktor = eloCalculationStrategy.calculateFaktor(eloGewinner, eloVerlierer);
		double zuwachs = faktor * (punkte - erwartung);
		int roundedZuwachs = (int) Math.round(zuwachs);

		return new EloCalculationResult(roundedZuwachs, eloGewinner1, eloGewinner2, eloVerlierer1, eloVerlierer2, faktor, erwartung);
	}

	public void setEloCalculationStrategy(EloCalculationStrategy eloCalculationStrategy) {
		this.eloCalculationStrategy = eloCalculationStrategy;
	}

	public interface EloCalculationStrategy {
		double calculateErwartungswert(int eloGewinner, int eloVerlierer);

		double calculateFaktor(int eloGewinner, int eloVerlierer);
	}

	private static class DefaultEloCalculationStrategy implements EloCalculationStrategy {

		public double calculateErwartungswert(int eloGewinner, int eloVerlierer) {
			return (double) 1 / (1 + Math.pow(10, ((double) (eloVerlierer - eloGewinner) / 400)));
		}

		public double calculateFaktor(int eloGewinner, int eloVerlierer) {
			double faktor = ELO_FAKTOR_STANDARD;
			if (eloGewinner >= ELO_EXPERTE) {
				faktor = ELO_FAKTOR_EXPERTE;
			}
			return faktor;
		}

	}

}
