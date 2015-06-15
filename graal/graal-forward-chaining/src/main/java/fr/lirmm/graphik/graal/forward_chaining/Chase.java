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
 * This interface represents a chase (forward chaining) algorithm seen as an
 * iterative process.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface Chase {

	/**
	 * Calls next() until hasNext() return false.
	 */
	public void execute() throws ChaseException;;
	
	/**
	 * Execute the next step of the saturation process.
	 * 
	 * @throws ChaseException
	 */
	public void next() throws ChaseException;
	
	/**
	 * 
	 * @return true if the saturation process needs other steps.
	 */
	public boolean hasNext();
	
}
