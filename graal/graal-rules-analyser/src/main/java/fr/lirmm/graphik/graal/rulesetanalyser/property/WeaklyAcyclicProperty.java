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
import java.util.List;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.rulesetanalyser.graph.GraphPositionDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

/**
 * All predicate positions in the graph of position dependencies have finite
 * rank (i.e., there is no circuit with a special edge) (cf.
 * {@link GraphPositionDependencies}).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public final class WeaklyAcyclicProperty implements RuleProperty {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private static WeaklyAcyclicProperty instance = null;

	private WeaklyAcyclicProperty() {
	}

	public static synchronized WeaklyAcyclicProperty getInstance() {
		if (instance == null) {
			instance = new WeaklyAcyclicProperty();
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
		List<Rule> rules = new LinkedList<Rule>();
		rules.add(rule);
		return this.check(rules);
	}

	@Override
	public Boolean check(Iterable<Rule> rules) {
		GraphPositionDependencies graph = new GraphPositionDependencies(rules);
		return graph.isWeaklyAcyclic();
	}
	
	@Override
	public Boolean check(AnalyserRuleSet ruleSet) {
		return ruleSet.getGraphPositionDependencies().isWeaklyAcyclic();
	}

	@Override
	public String getLabel() {
		return "wa";
	}

}
