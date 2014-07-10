/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.MarkedVariableSet.MarkedRule;

/**
 * Each marked variable occurs at most once in a rule body
 * (cf. {@link MarkedVariableSet}).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class StickyProperty implements RuleProperty {

	private static StickyProperty instance = null;
	private static MarkedVariableSet markedRuleSet;
	
	private StickyProperty(){}
	
	public static StickyProperty getInstance() {
		if(instance == null) {
			instance = new StickyProperty();
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
		markedRuleSet = new MarkedVariableSet(rules);
		return this.check();
	}

	public boolean check(Iterable<Rule> rules) {
		markedRuleSet = new MarkedVariableSet(rules);
		return this.check();
	}

	public boolean check() {
		int nbOccurence;
		for (MarkedRule mrule : markedRuleSet.getMarkedRuleCollection()) {
			for (Term mvar : mrule.markedVars) {
				nbOccurence = 0;
				for (Atom a : mrule.rule.getBody()) {
					for (Term t : a) {
						if (mvar.equals(t)) {
							++nbOccurence;
							if (nbOccurence > 1) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	

}
