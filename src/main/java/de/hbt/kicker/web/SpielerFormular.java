package de.hbt.kicker.web;

import java.util.Date;

import de.hbt.kicker.elo.v2.service.SpielerService;

public class SpielerFormular extends AbstractFormular {

	private String name;

	private SpielerService spielerService;

	@Override
	protected void performAction() {
		spielerService.findOrCreateSpieler(name, new Date());
	}

	@Override
	protected boolean validate() {
		boolean valid = true;
		if (name == null && name.length() == 0) {
			valid = false;
			getErrorMessages().add("Es wurde kein Name angegeben.");
		} else if (spielerService.findSpielerByName(name) != null) {
			valid = false;
			getErrorMessages().add("Name schon vergeben: " + name);
		}
		return valid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSpielerService(SpielerService spielerService) {
		this.spielerService = spielerService;
	}

}
