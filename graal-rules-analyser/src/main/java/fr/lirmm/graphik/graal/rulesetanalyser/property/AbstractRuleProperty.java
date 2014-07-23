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
	public boolean check(Iterable<Rule> rules) {
		for(Rule rule : rules)
			if(!this.check(rule))
				return false;
		
		return true;
	}
	
	@Override
	public boolean check(AnalyserRuleSet ruleSet) {
		return this.check((Iterable<Rule>) ruleSet);
	}

}
