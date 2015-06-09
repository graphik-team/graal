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
package fr.lirmm.graphik.util;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface URI {
	
	/**
	 * Get the prefix of this URI.
	 * 
	 * {@literal (.*)[:/#]([^:/#]*)}
	 * 
	 * @return the first group of the regex pattern above.
	 */
	Prefix getPrefix();
	
	/**
	 * Get the localname of this URI.
	 * 
	 * {@literal (.*)[:/#]([^:/#]*)}
	 * 
	 * @return the second group of the regex pattern above.
	 */
	String getLocalname();

}
