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
package fr.lirmm.graphik.graal.core.term;



/**
 * Immutable Object
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class DefaultVariable extends AbstractTerm implements Variable {
	
	private static final long serialVersionUID = -8985351967341123126L;

	private final Object identifier;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultVariable(Variable var) {
		this.identifier = var.getIdentifier();
	}

	public DefaultVariable(Object identifier) {
		this.identifier = identifier;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public Term.Type getType() {
		return Term.Type.VARIABLE;
	}

	@Override
	public Object getIdentifier() {
		return this.identifier;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
