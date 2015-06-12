/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 package fr.lirmm.graphik.graal.core.atomset;

public class AtomSetException extends Exception {

	private static final long serialVersionUID = -7793681455338699527L;

	public AtomSetException(String message, Throwable cause) {
		super(message, cause);
	}

	public AtomSetException(String message) {
		super(message);
	}
	
	public AtomSetException(Throwable e) {
		super(e);
	}
}
