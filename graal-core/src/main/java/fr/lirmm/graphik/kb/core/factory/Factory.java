/**
 * 
 */
package fr.lirmm.graphik.kb.core.factory;

import fr.lirmm.graphik.kb.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.DefaultRule;
import fr.lirmm.graphik.kb.core.HashMapSubstitution;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.kb.core.Substitution;

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
