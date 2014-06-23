/**
 * 
 */
package fr.lirmm.graphik.kb.core;

import fr.lirmm.graphik.kb.atomset.LinkedListAtomSet;

/**
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
