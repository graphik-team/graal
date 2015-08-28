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
package fr.lirmm.graphik.graal.core.impl;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * A negative constraint prevents to have some set of atoms. Its logical
 * representation is: "¬animal(X) ∧ ¬plant(X)". <br/>
 * The NegativeConstraint Class represent negative constraint by a rule that
 * produce the Bottom atom if the specified atom set is met.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class DefaultNegativeConstraint extends DefaultRule implements NegativeConstraint {

	private static final InMemoryAtomSet HEAD;
	static {
		HEAD = new LinkedListAtomSet();
		HEAD.add(Atom.BOTTOM);
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DefaultNegativeConstraint() {
		super("", new LinkedListAtomSet(), HEAD);
	}

	public DefaultNegativeConstraint(Iterable<Atom> atomSet) {
		super("", atomSet, HEAD);
	}

	public DefaultNegativeConstraint(String label, Iterable<Atom> atomSet) {
		super(label, atomSet, HEAD);
	}

};
