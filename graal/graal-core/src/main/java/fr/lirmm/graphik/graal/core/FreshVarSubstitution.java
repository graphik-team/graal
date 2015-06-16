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
package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class FreshVarSubstitution extends TreeMapSubstitution {
	
	private VariableGenerator gen;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public FreshVarSubstitution() {
		this(new DefaultVariableGenerator("X"
				+ Integer.toString(FreshVarSubstitution.class.hashCode())));
	}
	
	public FreshVarSubstitution(VariableGenerator gen) {
		this.gen = gen;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Term createImageOf(Term term) {
		Term substitut = term;
		if(Term.Type.VARIABLE.equals(term.getType())) {
			substitut = this.getMap().get(term);
			if(substitut == null) {
				substitut = gen.getFreshVar();
				this.put(term, substitut);
			}
		}
		return substitut;
	}
	

}
