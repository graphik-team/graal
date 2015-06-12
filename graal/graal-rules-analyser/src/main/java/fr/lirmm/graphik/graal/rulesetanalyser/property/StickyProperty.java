/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.term.Term;
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
public final class StickyProperty implements RuleProperty {

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
