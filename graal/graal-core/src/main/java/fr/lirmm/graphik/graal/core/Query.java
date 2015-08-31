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
 package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

/**
 * This interface represents a generic query.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public interface Query extends AppendableToStringBuilder {

	/**
	 * @return true if the expected answer is boolean, false otherwise.
	 */
	public boolean isBoolean();

};