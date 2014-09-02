/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * A negative constraint prevents to have some set of atoms. Its logical
 * representation is: "¬animal(X) ∧ ¬plant(X)". <br/>
 * The NegativeConstraint Class represent negative constraint by a rule that
 * produce the Bottom atom if the specified atom set is met.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class NegativeConstraint extends DefaultRule {

	private static final AtomSet head;
	static {
		head = new LinkedListAtomSet();
		head.add(Atom.BOTTOM);
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public NegativeConstraint() {
		super("", new LinkedListAtomSet(), head);
	}

	public NegativeConstraint(Iterable<Atom> atomSet) {
		super("", atomSet, head);
	}

	public NegativeConstraint(String label, Iterable<Atom> atomSet) {
		super(label, atomSet, head);
	}

};
