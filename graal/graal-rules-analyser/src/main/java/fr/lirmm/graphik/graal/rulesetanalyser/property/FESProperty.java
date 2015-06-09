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
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class FESProperty implements RuleProperty {

	private static FESProperty instance;

	private FESProperty() {
	}

	public static synchronized FESProperty getInstance() {
		if (instance == null)
			instance = new FESProperty();

		return instance;
	}
	
	@Override
	public String getLabel() {
		return "fes";
	}

	@Override
	public Boolean check(Rule rule) {
		return null;
	}

	@Override
	public Boolean check(AnalyserRuleSet ruleSet) {
		return null;
	}

	@Override
	public Boolean check(Iterable<Rule> ruleSet) {
		return null;
	}

}