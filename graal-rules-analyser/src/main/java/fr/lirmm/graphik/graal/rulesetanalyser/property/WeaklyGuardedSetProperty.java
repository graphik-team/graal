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
 * from the body ({@see AffectedPositionSet}).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class WeaklyGuardedSetProperty implements RuleProperty {

	private AffectedPositionSet affectedPositionSet;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private static WeaklyGuardedSetProperty instance = null;

	private WeaklyGuardedSetProperty() {
	}

	public static WeaklyGuardedSetProperty getInstance() {
		if (instance == null) {
			instance = new WeaklyGuardedSetProperty();
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
					.getAllAffectedVariables(r.getBody());
			if (!RuleUtil.thereIsOneAtomThatContainsAllVars(r.getBody(),
					affectedVars)) {
				return false;
			}
		}
		return true;
	}

}
