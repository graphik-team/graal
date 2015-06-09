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

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

/**
 * Each marked variable occurs at most once in a rule body
 * (cf. {@link MarkedVariableSet}).
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public final class AGRDProperty implements RuleProperty {

	private static AGRDProperty instance = null;
	
	private AGRDProperty() {}
	
	public static synchronized AGRDProperty getInstance() {
		if(instance == null) {
			instance = new AGRDProperty();
		}
		return instance;
	}
	

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Boolean check(AnalyserRuleSet ruleset) {
		GraphOfRuleDependencies g = ruleset.getGraphOfRuleDependencies();
		return !g.hasCircuit();
	}

	@Override
	public String getLabel() {
		return "aGRD";
	}

	public Boolean check(Rule rule) { return false; }
	public Boolean check(Iterable<Rule> ruleSet) { return false; }
};

