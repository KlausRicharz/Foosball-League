package de.hbt.kicker.elo.v1.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "KICKER")
public class SpielAlt {
	
	@Id
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "SPIELER1")
	private SpielerAlt spieler1;
	
	@ManyToOne
	@JoinColumn(name = "SPIELER2")
	private SpielerAlt spieler2;
	
	@ManyToOne
	@JoinColumn(name = "SPIELER3")
	private SpielerAlt spieler3;
	
	@ManyToOne
	@JoinColumn(name = "SPIELER4")
	private SpielerAlt spieler4;
	
	private int punktewin;
	private int punktelost;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	public int getId() {
		return id;
	}

	public int getPunktewin() {
		return punktewin;
	}

	public int getPunktelost() {
		return punktelost;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public SpielerAlt getSpieler1() {
		return spieler1;
	}

	public SpielerAlt getSpieler2() {
		return spieler2;
	}

	public SpielerAlt getSpieler3() {
		return spieler3;
	}

	public SpielerAlt getSpieler4() {
		return spieler4;
	}
	
}
