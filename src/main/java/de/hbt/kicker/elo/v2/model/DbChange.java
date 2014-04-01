package de.hbt.kicker.elo.v2.model;

import javax.persistence.Entity;

@Entity
public class DbChange extends EloModelElement {

	private String change;

	public String getChange() {
		return change;
	}

	public void setChange(String change) {
		this.change = change;
	}

}
