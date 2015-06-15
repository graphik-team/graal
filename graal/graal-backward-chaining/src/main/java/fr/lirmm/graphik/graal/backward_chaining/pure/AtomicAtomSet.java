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
 package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

@SuppressWarnings("unused")
/**
 * Fact composed by only one atom
 * @author Mélanie KÖNIG
 */
class AtomicAtomSet extends LinkedListAtomSet {
	private static final long serialVersionUID = 1L;

	public AtomicAtomSet() {
		super();
	}

	public AtomicAtomSet(Atom atom) {
		super();
		super.add(atom);

	}

	@Override
	public boolean add(Atom atom) {
		this.clear();
		return super.add(atom);
	}

	/**
	 * Return the atom that compose this atomic fact
	 */
	public Atom getAtom() {
		Iterator<Atom> it = this.iterator();
		if (it.hasNext())
			return this.iterator().next();
		else
			return null;
	}

}
