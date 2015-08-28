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
 package fr.lirmm.graphik.graal.core.impl;

import fr.lirmm.graphik.graal.core.VariableGenerator;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Variable;

/**
 * Generate fresh variables by appending an incremental counter to the specified
 * prefix.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultVariableGenerator implements VariableGenerator {
	
	private String prefix;
	private int index;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DefaultVariableGenerator(String prefix) {
		this.index = 0;
		this.prefix = prefix;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Variable getFreshVar() {
		return DefaultTermFactory.instance()
				.createVariable(this.prefix + getFreshIndex());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private int getFreshIndex() {
		return index++;
	}

};
