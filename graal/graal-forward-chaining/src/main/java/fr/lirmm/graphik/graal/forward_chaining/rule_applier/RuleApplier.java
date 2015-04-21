/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining.rule_applier;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface RuleApplier<R extends Rule, A extends AtomSet> {

	/**
	 * Apply the given Rule over the given AtomSet
	 * 
	 * @param rule
	 * @param atomSet
	 * @return true iff the atom-set has been modified.
	 * @throws RuleApplicationException
	 */
	boolean apply(R rule, A atomSet) throws RuleApplicationException;
	
}
