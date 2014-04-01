package de.hbt.kicker.elo.v2.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UC_NAME", columnNames = "NAME"))
public class Spieler extends EloModelElement {

	private String name;
	private int anzahlSpiele = 0;
	private int anzahlSiege = 0;
	private boolean visible = true;
	private Boolean topFlopAktiviert = false;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private EloPunktzahl punktzahl;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<EloPunktzahl> punktzahlHistorie = new HashSet<EloPunktzahl>();

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAnzahlSpiele() {
		return anzahlSpiele;
	}

	public void setAnzahlSpiele(int anzahlSpiele) {
		this.anzahlSpiele = anzahlSpiele;
	}

	public int getAnzahlSiege() {
		return anzahlSiege;
	}

	public void setAnzahlSiege(int anzahlSiege) {
		this.anzahlSiege = anzahlSiege;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isTopFlopAktiviert() {
		return topFlopAktiviert == null || topFlopAktiviert.booleanValue();
	}

	public void setTopFlopAktiviert(boolean topFlopAktiviert) {
		this.topFlopAktiviert = topFlopAktiviert;
	}
	
	

}
