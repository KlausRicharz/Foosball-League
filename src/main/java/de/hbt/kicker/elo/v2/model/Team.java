package de.hbt.kicker.elo.v2.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Team extends EloModelElement {

	@ManyToOne(fetch = FetchType.LAZY)
	private Spieler spieler1;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Spieler spieler2;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private EloPunktzahl punktzahl;
	
	@OneToMany(cascade = CascadeType.ALL)
	private Set<EloPunktzahl> punktzahlHistorie = new HashSet<EloPunktzahl>();
	
	public Spieler getSpieler1() {
		return spieler1;
	}

	public void setSpieler1(Spieler spieler1) {
		this.spieler1 = spieler1;
	}

	public Spieler getSpieler2() {
		return spieler2;
	}

	public void setSpieler2(Spieler spieler2) {
		this.spieler2 = spieler2;
	}

	public EloPunktzahl getPunktzahl() {
		return punktzahl;
	}

	public void setPunktzahl(EloPunktzahl punktzahl) {
		this.punktzahl = punktzahl;
		if(punktzahl != null) {
			punktzahlHistorie.add(punktzahl);
		}
	}
	
	public Set<EloPunktzahl> getPunktzahlHistorie() {
		return punktzahlHistorie;
	}

}
