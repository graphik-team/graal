/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class DefaultKnowledgeBase implements KnowledgeBase {

	private RuleSet ruleset;
	private AtomSet atomset;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DefaultKnowledgeBase() {
		this.ruleset = new LinkedListRuleSet();
		this.atomset = AtomSetFactory.getInstance().createAtomSet();
	}

	public DefaultKnowledgeBase(RuleSet ruleset, AtomSet atomset) {
		this.ruleset = ruleset;
		this.atomset = atomset;
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS/SETTERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return the ruleset
	 */
	@Override
	public RuleSet getRuleSet() {
		return ruleset;
	}

	/**
	 * @return the atomset
	 */
	@Override
	public AtomSet getAtomSet() {
		return atomset;
	}

};
