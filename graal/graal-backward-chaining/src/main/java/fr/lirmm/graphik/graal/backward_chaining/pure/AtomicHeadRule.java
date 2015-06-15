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

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * rule with only one atom in its head
 * 
 * @author Mélanie KÖNIG
 * 
 */
class AtomicHeadRule extends DefaultRule {

	/**
	 * Construct an AtomicHeadRule
	 * 
	 * @param b
	 *            a fact
	 * @param h
	 *            must be an AtomicFact
	 * @throws Exception
	 */
	public AtomicHeadRule(AtomSet b, Atom h) {
		super(b, new AtomicAtomSet(h));
	}

	/**
	 * change the head of this rule by the given fact
	 * 
	 * @param h
	 *            must be an AtomicFact
	 */
	public void setHead(Atom h) {
		LinkedListAtomSet atomset = new LinkedListAtomSet();
		atomset.add(h);
		super.getHead();
	}

	@Override
	public AtomicAtomSet getHead() {
		return (AtomicAtomSet) super.getHead();
	}

}
