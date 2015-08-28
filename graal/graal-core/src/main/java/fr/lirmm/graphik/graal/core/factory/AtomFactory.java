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
package fr.lirmm.graphik.graal.core.factory;

import java.util.List;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class AtomFactory {

	private static AtomFactory instance = new AtomFactory();
	
	private AtomFactory() {
		super();
	}

	public static AtomFactory instance() {
		return instance;
	}

	public Atom create(Predicate predicate) {
		return new DefaultAtom(predicate);
	}

	public Atom create(Predicate predicate, List<Term> terms) {
		return new DefaultAtom(predicate, terms);
	}

	public Atom create(Predicate predicate, Term... terms) {
		return new DefaultAtom(predicate, terms);
	}

	public Atom create(Atom atom) {
		return new DefaultAtom(atom);
	}

}