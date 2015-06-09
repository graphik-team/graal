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
package fr.lirmm.graphik.graal.io;

import java.io.IOException;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ParseException extends IOException {

	private static final long serialVersionUID = -4455111019098315998L;
	
	/**
	 * @param message
	 * @param e
	 */
	public ParseException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * @param message
	 */
	public ParseException(String message) {
		super(message);
	}

}
