/**
 * 
 */
package fr.lirmm.graphik.graal.core.factory;

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
}
