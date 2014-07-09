/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.util;

import java.util.Collection;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public final class RuleUtil {

	private RuleUtil() {}
	
	public static boolean thereIsOneAtomThatContainsAllVars(Iterable<Atom> atomset, Collection<Term> terms) {
		for(Atom atom : atomset) {
			if(atom.getTerms(Type.VARIABLE).containsAll(terms)) {
				return true;
			}
		}
		return false;
	}
}
