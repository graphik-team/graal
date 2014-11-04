/**
 * 
 */
package fr.lirmm.graphik.graal.core.factory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class AtomSetFactory {

	private static AtomSetFactory instance = new AtomSetFactory();

	private AtomSetFactory() {
	}

	public static AtomSetFactory getInstance() {
		return instance;
	}

	public AtomSet createAtomSet() {
		return new LinkedListAtomSet();
	}

	public AtomSet createAtomSet(AtomSet src) {
		AtomSet atomset = this.createAtomSet();
		for (Atom a : src) {
			atomset.add(a);
		}
		return atomset;
	}
}
