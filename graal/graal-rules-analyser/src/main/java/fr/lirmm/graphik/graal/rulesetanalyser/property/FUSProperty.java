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
public final class FUSProperty implements RuleProperty {

	private static FUSProperty instance;

	private FUSProperty() {
	}

	public static synchronized FUSProperty getInstance() {
		if (instance == null)
			instance = new FUSProperty();

		return instance;
	}
	
	@Override
	public String getLabel() {
		return "fus";
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
