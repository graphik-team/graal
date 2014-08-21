/**
 * 
 */
package fr.lirmm.graphik.graal.core.factory;

import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SubstitutionFactory {

	private static SubstitutionFactory instance = new SubstitutionFactory();
	
	private SubstitutionFactory() {
	}

	public static SubstitutionFactory getInstance() {
		return instance;
	}

	public Rule createRule() {
		return new DefaultRule();
	}
	
	public Substitution createSubstitution() {
		return new HashMapSubstitution();
	}
}
