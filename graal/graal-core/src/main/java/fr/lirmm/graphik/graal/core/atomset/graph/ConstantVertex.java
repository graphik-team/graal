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
package fr.lirmm.graphik.graal.core.atomset.graph;

import fr.lirmm.graphik.graal.core.term.Constant;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class ConstantVertex extends AbstractTermVertex implements
		Constant {

	private static final long serialVersionUID = -1143798191017134399L;

	private Constant term;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public ConstantVertex(Constant term) {
		this.term = term;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	protected Constant getTerm() {
		return this.term;
	}

	@Override
	public String getLabel() {
		return this.term.getLabel();
	}

	@Override
	public Object getIdentifier() {
		return this.getTerm().getIdentifier();
	}

}
