/**
 * 
 */
package fr.lirmm.graphik.graal.chase;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class FreeExistentialVariableGenerator {

	private int currentApplication = 0;
	
	public Substitution getExistentialSubstitution(Set<Term> existentialVars) {
		Substitution substitution = SubstitutionFactory.getInstance().createSubstitution();
		for (Term t : existentialVars) {
			substitution.put(t, new Term(t + "_" + this.currentApplication, Type.VARIABLE));
		}
		++this.currentApplication;
		return substitution;
	}
}
