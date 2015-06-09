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
 /**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.FileNotFoundException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PureException extends Exception {
	
	private static final long serialVersionUID = -1997725285866124335L;

	/**
	 * @param message
	 */
	PureException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param e
	 */
	public PureException(String message, FileNotFoundException e) {
		super(message, e);
	}

}
