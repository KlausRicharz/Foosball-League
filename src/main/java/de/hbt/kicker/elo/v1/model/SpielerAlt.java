package de.hbt.kicker.elo.v1.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ENTRY")
public class SpielerAlt {
	
	@Id
	private String name;
	
	private boolean visible;

	public String getName() {
		return name;
	}

	public boolean isVisible() {
		return visible;
	}
	
}
