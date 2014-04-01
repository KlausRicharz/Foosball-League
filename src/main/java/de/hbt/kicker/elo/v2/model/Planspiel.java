package de.hbt.kicker.elo.v2.model;

import java.util.Date;

/**
 * TODO Turniersysteme und Ligen ermöglichen.
 * @author kr
 *
 */
@Deprecated
public class Planspiel {

	private String id;
	private String name;
	/*
	 * Kann vom Turnier-/Ligasystem verwendet werden, um eine bestimmte Ordnung
	 * zu ermöglichen, z.B. Turnierbäume.
	 */
	private String orderString;
	private Date geplantAm;
	private Spiel spiel;
	
	

}
