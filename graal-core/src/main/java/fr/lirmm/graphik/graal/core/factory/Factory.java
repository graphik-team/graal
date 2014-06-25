/**
 * 
 */
package fr.lirmm.graphik.graal.core.factory;

import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class Factory {

private static Factory instance = new Factory();
	
	public static Factory getInstance() {
		return instance;
	}

	public Rule createRule() {
		return new DefaultRule();
	}
	
	public AtomSet createAtomSet() {
		return new LinkedListAtomSet();
	}
	
	public Substitution createSubstitution() {
		return new HashMapSubstitution();
	}
}
