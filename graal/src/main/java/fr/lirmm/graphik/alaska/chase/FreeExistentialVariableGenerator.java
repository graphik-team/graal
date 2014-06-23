/**
 * 
 */
package fr.lirmm.graphik.alaska.chase;

import java.util.Set;

import fr.lirmm.graphik.kb.core.Substitution;
import fr.lirmm.graphik.kb.core.Term;
import fr.lirmm.graphik.kb.core.Term.Type;
import fr.lirmm.graphik.kb.core.factory.Factory;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class FreeExistentialVariableGenerator {

	private int currentApplication = 0;
	
	public Substitution getExistentialSubstitution(Set<Term> existentialVars) {
		Substitution substitution = Factory.getInstance().createSubstitution();
		for (Term t : existentialVars) {
			substitution.put(t, new Term(t + "_" + this.currentApplication, Type.VARIABLE));
		}
		++this.currentApplication;
		return substitution;
	}
}
