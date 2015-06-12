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
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractRuleProperty implements RuleProperty {
	
	@Override
	public Boolean check(Iterable<Rule> rules) {
		for(Rule rule : rules)
			if(!this.check(rule))
				return false;
		
		return true;
	}
	
	@Override
	public Boolean check(AnalyserRuleSet ruleSet) {
		return this.check((Iterable<Rule>) ruleSet);
	}

}
