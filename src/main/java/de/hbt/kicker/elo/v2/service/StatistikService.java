package de.hbt.kicker.elo.v2.service;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.time.DateUtils;

import de.hbt.kicker.elo.statistik.Flop3Statistik;
import de.hbt.kicker.elo.statistik.Top3Statistik;
import de.hbt.kicker.elo.v2.model.Spiel;
import de.hbt.kicker.elo.v2.persistence.EloPersistence;
import de.hbt.kicker.elo.v2.service.crazystats.CrazyStats;
import de.hbt.kicker.elo.v2.service.crazystats.LoosingSpreeStatistic;
import de.hbt.kicker.elo.v2.service.crazystats.WinningSpreeStatistic;

public class StatistikService {
	
	private EloPersistence eloPersistence;
	
	public Top3Statistik calculateTop3Wochenstatistik() {
		Calendar wochenanfang = Calendar.getInstance(Locale.GERMAN);
		wochenanfang = DateUtils.truncate(wochenanfang, Calendar.DATE);
		while(wochenanfang.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			wochenanfang.setTime(DateUtils.addDays(wochenanfang.getTime(), -1));
		}
		
		return createTop3Statistik(wochenanfang.getTime());
	}
	
	public Top3Statistik calculateTop3Monatsstatistik() {
		Calendar monatsanfang = Calendar.getInstance(Locale.GERMAN);
		monatsanfang = DateUtils.truncate(monatsanfang, Calendar.MONTH);
		
		return createTop3Statistik(monatsanfang.getTime());
	}
	
	public Top3Statistik calculateTop3Jahresstatistik() {
		Calendar jahresanfang = Calendar.getInstance(Locale.GERMAN);
		jahresanfang = DateUtils.truncate(jahresanfang, Calendar.YEAR);
		
		return createTop3Statistik(jahresanfang.getTime());
	}
	
	public Flop3Statistik calculateFlop3Wochenstatistik() {
		Calendar wochenanfang = Calendar.getInstance(Locale.GERMAN);
		wochenanfang = DateUtils.truncate(wochenanfang, Calendar.DATE);
		while(wochenanfang.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			wochenanfang.setTime(DateUtils.addDays(wochenanfang.getTime(), -1));
		}
		
		return createFlop3Statistik(wochenanfang.getTime());
	}
	
	public Flop3Statistik calculateFlop3Monatsstatistik() {
		Calendar monatsanfang = Calendar.getInstance(Locale.GERMAN);
		monatsanfang = DateUtils.truncate(monatsanfang, Calendar.MONTH);
		
		return createFlop3Statistik(monatsanfang.getTime());
	}
	
	public Flop3Statistik calculateFlop3Jahresstatistik() {
		Calendar jahresanfang = Calendar.getInstance(Locale.GERMAN);
		jahresanfang = DateUtils.truncate(jahresanfang, Calendar.YEAR);
		
		return createFlop3Statistik(jahresanfang.getTime());
	}
	
	public List<StatistikMeldung> getCrazyStats() {
		Date aktivAb = DateUtils.addDays(new Date(), -21);
		CrazyStats crazyStats = new CrazyStats(new WinningSpreeStatistic(true, aktivAb), new LoosingSpreeStatistic(true, aktivAb));
		crazyStats.addAllSpiele(getSpiele(Integer.MAX_VALUE));
		crazyStats.calculate();
		List<StatistikMeldung> meldungen = crazyStats.getMeldungen();
		Collections.sort(meldungen);
		return meldungen;
	}
	
	private List<Spiel> getSpiele(int maxResults) {
		List<Spiel> result = eloPersistence.createQuery("select distinct s from Spiel s " + //
				"join fetch s.gewinner g " + //
				"join fetch g.punktzahl gp " + //
				"join fetch s.verlierer v " + //
				"join fetch v.punktzahl vp " + //
				"join fetch g.spieler1 s1 " + //
				"join fetch g.spieler2 s2 " + //
				"join fetch v.spieler1 s3 " + //
				"join fetch v.spieler2 s4 " + //
				"join fetch s.punktzahl p " + //
				"order by s.zeitpunkt desc", Spiel.class)
				.getResultList();
		
		return result.subList(0, Math.min(result.size(), maxResults));
	}

	private Top3Statistik createTop3Statistik(Date start) {
		Top3Statistik statistik = new Top3Statistik();
		List<Spiel> spiele = eloPersistence.findAllSpieleNotBefore(start);
		for (Spiel spiel : spiele) {
			statistik.add(spiel.getGewinner().getSpieler1(), spiel.getPunktzahl().getPunktzahl());
			statistik.add(spiel.getGewinner().getSpieler2(), spiel.getPunktzahl().getPunktzahl());
			statistik.add(spiel.getVerlierer().getSpieler1(), -spiel.getPunktzahl().getPunktzahl());
			statistik.add(spiel.getVerlierer().getSpieler2(), -spiel.getPunktzahl().getPunktzahl());
		}
		statistik.calculate();
		return statistik;
	}
	
	private Flop3Statistik createFlop3Statistik(Date start) {
		Flop3Statistik statistik = new Flop3Statistik();
		List<Spiel> spiele = eloPersistence.findAllSpieleNotBefore(start);
		for (Spiel spiel : spiele) {
			statistik.add(spiel.getGewinner().getSpieler1(), spiel.getPunktzahl().getPunktzahl());
			statistik.add(spiel.getGewinner().getSpieler2(), spiel.getPunktzahl().getPunktzahl());
			statistik.add(spiel.getVerlierer().getSpieler1(), -spiel.getPunktzahl().getPunktzahl());
			statistik.add(spiel.getVerlierer().getSpieler2(), -spiel.getPunktzahl().getPunktzahl());
		}
		statistik.calculate();
		return statistik;
	}

	public void setEloPersistence(EloPersistence eloPersistence) {
		this.eloPersistence = eloPersistence;
	}
	
}
