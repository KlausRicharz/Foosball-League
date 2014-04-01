package de.hbt.kicker.web;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractFormular {
	
	private final Logger LOG = Logger.getLogger(getClass().getName());

	private boolean validated = false;
	private boolean valid = false;

	private final List<String> errorMessages = new ArrayList<String>();

	public boolean isValid() {
		try {
			if (!validated) {
				valid = validate();
				if (valid) {
					performAction();
				}
				validated = true;
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Konnte Formular nicht validieren.", e);
			throw new RuntimeException(e);
		}
		return valid;
	}

	abstract protected void performAction();

	abstract protected boolean validate();

	public List<String> getErrorMessages() {
		return errorMessages;
	}

}
