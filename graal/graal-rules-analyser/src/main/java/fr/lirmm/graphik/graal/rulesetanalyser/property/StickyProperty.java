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
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

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
	
	private StickyProperty(){}
	
	public static synchronized StickyProperty getInstance() {
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
	public Boolean check(Rule rule) {
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(rule);
		return this.check(rules);
	}

	public Boolean check(Iterable<Rule> rules) {
		MarkedVariableSet markedVariableSet = new MarkedVariableSet(rules);
		return this.check(markedVariableSet);
	}
	
	public Boolean check(AnalyserRuleSet ruleSet) {
		return this.check(ruleSet.getMarkedVariableSet());
	}

	public boolean check(MarkedVariableSet markedVariableSet) {
		int nbOccurence;
		for (MarkedRule mrule : markedVariableSet.getMarkedRuleCollection()) {
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

	@Override
	public String getLabel() {
		return "s";
	}

	

}
