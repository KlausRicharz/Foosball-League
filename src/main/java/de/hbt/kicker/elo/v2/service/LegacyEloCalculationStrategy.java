package de.hbt.kicker.elo.v2.service;

import de.hbt.kicker.elo.v2.service.EloService.EloCalculationStrategy;

public class LegacyEloCalculationStrategy implements EloCalculationStrategy {

	public double calculateErwartungswert(int eloGewinner, int eloVerlierer) {
		double EloDiff = eloGewinner - eloVerlierer;
		double Betrag = Math.abs(EloDiff);
		double x0 = .5;
		double x1 = .0014217;
		double x2 = -.00000024336;
		double x3 = -.000000002514;
		double x4 = .000000000001991;
		double Erwartung;
		if (Betrag > 735) {
			Erwartung = 1;
		} else {
			Erwartung = x0 + x1 * Betrag + x2 * Betrag * Betrag + x3 * Betrag * Betrag * Betrag + x4 * Betrag * Betrag * Betrag * Betrag;
		}
		if (EloDiff < 0) {
			Erwartung = 1 - Erwartung;
		}
		return Erwartung;
	}

	public double calculateFaktor(int eloGewinner, int eloVerlierer) {
		return (3400 - eloGewinner) * (3400 - eloGewinner) / 100000;
	}

}
