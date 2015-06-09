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
 * Immutable object
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class DefaultConstant extends AbstractTerm implements Constant {

	private static final long serialVersionUID = 3531038070349085454L;

	private final Object identifier;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultConstant(Constant cst) {
		this.identifier = cst.getIdentifier();
	}
	
	public DefaultConstant(Object identifier) {
		this.identifier = identifier;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean isConstant() {
		return true;
	}
	
	@Override
	public Term.Type getType() {
		return Term.Type.CONSTANT;
	}

	@Override
	public Object getIdentifier() {
		return this.identifier;
	}

}
