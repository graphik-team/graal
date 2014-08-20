/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.AffectedPositionSet;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;
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
	public Boolean check(Rule rule) {
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(rule);
		return this.check(rules);
	}

	/**
	 * At least one atom in the body of each rule contains all affected
	 * variables from the body.
	 */
	@Override
	public Boolean check(Iterable<Rule> rules) {
		AffectedPositionSet affectedPositionSet = new AffectedPositionSet(rules);
		return this.check(affectedPositionSet);
	}
	
	@Override
	public Boolean check(AnalyserRuleSet ruleSet) {
		return this.check(ruleSet.getAffectedPositionSet());
	}

	@Override
	public String getLabel() {
		return "wfg";
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private boolean check(AffectedPositionSet affectedPositionSet) {
		for (Rule r : affectedPositionSet.getRules()) {
			Set<Term> affectedVars = affectedPositionSet
					.getAllAffectedFrontierVariables(r);
			if (!RuleUtil.thereIsOneAtomThatContainsAllVars(r.getBody(),
					affectedVars)) {
				return false;
			}
		}
		return true;
	}
}
