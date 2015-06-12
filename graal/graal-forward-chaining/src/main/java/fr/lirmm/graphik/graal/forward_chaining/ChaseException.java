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
package fr.lirmm.graphik.graal.forward_chaining;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ChaseException extends Exception {

	private static final long serialVersionUID = -8123025266971025431L;

	/**
	 * @param message
	 * @param e
	 */
	public ChaseException(String message, Exception e) {
		super(message, e);
	}

}
