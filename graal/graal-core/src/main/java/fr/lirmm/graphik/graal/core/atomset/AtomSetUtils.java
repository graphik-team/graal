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
package fr.lirmm.graphik.graal.core.atomset;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public final class AtomSetUtils {

	private AtomSetUtils() {
	}
	
	/**
	 * 
	 * @param a1
	 * @param a2
	 * @return true if a1 contains a2, false otherwise.
	 */
	public static boolean contains(InMemoryAtomSet a1, InMemoryAtomSet a2) {
		for (Atom atom : a2) {
			if (!a1.contains(atom)) {
				return false;
			}
		}
		return true;
	}


	public static InMemoryAtomSet minus(InMemoryAtomSet a1, InMemoryAtomSet a2) {
		InMemoryAtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		for (Atom a : a1) {
			if (!a2.contains(a)) {
				atomset.add(a);
			}
		}
		return atomset;
	}

	/**
	 * Return the terms occuring both in Q\P and P
	 */
	public static LinkedList<Term> sep(InMemoryAtomSet a1, InMemoryAtomSet a2) {
		InMemoryAtomSet pBar = minus(a2, a1);
		LinkedList<Term> sep = new LinkedList<Term>();
		for (Term t : pBar.getTerms()) {
			for (Term x : a2.getTerms())
				if (x.equals(t))
					sep.add(t);
		}
		return sep;
	}
	
	public static InMemoryAtomSet union(AtomSet a1, AtomSet a2) {
		InMemoryAtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		for (Atom a : a1) {
			atomset.add(new DefaultAtom(a));
		}
		for (Atom a : a2) {
			if (!atomset.contains(a)) {
				atomset.add(new DefaultAtom(a));
			}				
		}
		return atomset;
	}

}
