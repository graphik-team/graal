/**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class AtomSets {

	private AtomSets() {
	}

	public static AtomSet minus(AtomSet a1, AtomSet a2) {
		AtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		for (Atom a : a1) {
			try {
				if (!a2.contains(a)) {
					atomset.add(a);
				}
			} catch (AtomSetException e) {}
		}
		return atomset;
	}

	/**
	 * Return the terms occuring both in Q\P and P
	 */
	public static LinkedList<Term> sep(AtomSet p, AtomSet q) {
		AtomSet pBar = minus(q, p);
		LinkedList<Term> sep = new LinkedList<Term>();
		try {
			for (Term t : pBar.getTerms()) {
				for (Term x : q.getTerms())
					if (x.equals(t))
						sep.add(t);
			}
		} catch (AtomSetException e) {}
		return sep;
	}
	
	public static AtomSet union(AtomSet a1, AtomSet a2) {
		AtomSet atomset = AtomSetFactory.getInstance().createAtomSet();
		for (Atom a : a1) {
			atomset.add(new DefaultAtom(a));
		}
		for (Atom a : a2) {
			try {
			if (!atomset.contains(a)) {
				atomset.add(new DefaultAtom(a));
			}
			} catch (AtomSetException e) {}
				
		}
		return atomset;
	}

}
