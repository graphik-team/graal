/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.AffectedPositionSet;
import fr.lirmm.graphik.graal.rulesetanalyser.util.RuleUtil;

/**
 * At least one atom in the body of each rule contains all affected variable
 * from the frontier ({@see AffectedPositionSet}).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class WeaklyFrontierGuardedSetProperty implements RuleProperty {

	private AffectedPositionSet affectedPositionSet;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private static WeaklyFrontierGuardedSetProperty instance = null;

	private WeaklyFrontierGuardedSetProperty() {
	}

	public static WeaklyFrontierGuardedSetProperty getInstance() {
		if (instance == null) {
			instance = new WeaklyFrontierGuardedSetProperty();
		}
		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.lirmm.graphik.graal.rulesetanalyser.UnitProperty#check(fr.lirmm.graphik
	 * .graal.core.Rule)
	 */
	@Override
	public boolean check(Rule rule) {
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(rule);
		return this.check(rules);
	}

	/**
	 * At least one atom in the body of each rule contains all affected
	 * variables from the body.
	 */
	@Override
	public boolean check(Iterable<Rule> rules) {
		this.affectedPositionSet = new AffectedPositionSet(rules);
		for (Rule r : rules) {
			Set<Term> affectedVars = this.affectedPositionSet
					.getAllAffectedFrontierVariables(r);
			if (!RuleUtil.thereIsOneAtomThatContainsAllVars(r.getBody(),
					affectedVars)) {
				return false;
			}
		}
		return true;
	}

}
