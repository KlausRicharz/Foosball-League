package de.hbt.kicker.elo.v2.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Spiel extends EloModelElement {

	@Temporal(TemporalType.TIMESTAMP)
	private Date zeitpunkt;

	private int toreGewinner;

	private int toreVerlierer;

	@ManyToOne(fetch = FetchType.LAZY)
	private Team gewinner;

	@ManyToOne(fetch = FetchType.LAZY)
	private Team verlierer;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private EloPunktzahl punktzahl;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private EloPunktzahl teamPunktzahl;

	public Date getZeitpunkt() {
		return zeitpunkt;
	}

	public void setZeitpunkt(Date zeitpunkt) {
		this.zeitpunkt = zeitpunkt;
	}

	public Team getGewinner() {
		return gewinner;
	}

	public void setGewinner(Team gewinner) {
		this.gewinner = gewinner;
	}

	public Team getVerlierer() {
		return verlierer;
	}

	public void setVerlierer(Team verlierer) {
		this.verlierer = verlierer;
	}

	public int getToreGewinner() {
		return toreGewinner;
	}

	public void setToreGewinner(int toreGewinner) {
		this.toreGewinner = toreGewinner;
	}

	public int getToreVerlierer() {
		return toreVerlierer;
	}

	public void setToreVerlierer(int toreVerlierer) {
		this.toreVerlierer = toreVerlierer;
	}

	public EloPunktzahl getPunktzahl() {
		return punktzahl;
	}

	public void setPunktzahl(EloPunktzahl punktzahl) {
		this.punktzahl = punktzahl;
	}

	public EloPunktzahl getTeamPunktzahl() {
		return teamPunktzahl;
	}

	public void setTeamPunktzahl(EloPunktzahl teamPunktzahl) {
		this.teamPunktzahl = teamPunktzahl;
	}

}
