/**
 * 
 */
package fr.lirmm.graphik.graal.core.factory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public final class AtomSetFactory {

	private static AtomSetFactory instance = new AtomSetFactory();

	private AtomSetFactory() {
	}

	public static AtomSetFactory getInstance() {
		return instance;
	}

	public InMemoryAtomSet createAtomSet() {
		return new LinkedListAtomSet();
	}

	public InMemoryAtomSet createAtomSet(AtomSet src) {
		InMemoryAtomSet atomset = this.createAtomSet();
		for (Atom a : src) {
			atomset.add(a);
		}
		return atomset;
	}
}
