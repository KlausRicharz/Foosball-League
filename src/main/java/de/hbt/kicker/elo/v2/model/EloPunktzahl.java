package de.hbt.kicker.elo.v2.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class EloPunktzahl extends EloModelElement {

	private int punktzahl;
	private int zuwachs;
	private int eloGewinner1;
	private int eloGewinner2;
	private int eloVerlierer1;
	private int eloVerlierer2;
	private double faktor;
	private double erwartung;

	@Temporal(TemporalType.TIMESTAMP)
	private Date berechnetAm;

	public int getPunktzahl() {
		return punktzahl;
	}

	public void setPunktzahl(int punktzahl) {
		this.punktzahl = punktzahl;
	}

	public Date getBerechnetAm() {
		return berechnetAm;
	}

	public void setBerechnetAm(Date berechnetAm) {
		this.berechnetAm = berechnetAm;
	}

	public int getZuwachs() {
		return zuwachs;
	}

	public void setZuwachs(int zuwachs) {
		this.zuwachs = zuwachs;
	}

	public double getFaktor() {
		return faktor;
	}

	public void setFaktor(double faktor) {
		this.faktor = faktor;
	}

	public double getErwartung() {
		return erwartung;
	}

	public void setErwartung(double erwartung) {
		this.erwartung = erwartung;
	}

	public int getEloGewinner1() {
		return eloGewinner1;
	}

	public void setEloGewinner1(int eloGewinner1) {
		this.eloGewinner1 = eloGewinner1;
	}

	public int getEloGewinner2() {
		return eloGewinner2;
	}

	public void setEloGewinner2(int eloGewinner2) {
		this.eloGewinner2 = eloGewinner2;
	}

	public int getEloVerlierer1() {
		return eloVerlierer1;
	}

	public void setEloVerlierer1(int eloVerlierer1) {
		this.eloVerlierer1 = eloVerlierer1;
	}

	public int getEloVerlierer2() {
		return eloVerlierer2;
	}

	public void setEloVerlierer2(int eloVerlierer2) {
		this.eloVerlierer2 = eloVerlierer2;
	}

}
