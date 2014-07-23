package fr.lirmm.graphik.graal.rulesetanalyser.property;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface RuleProperty {
	
	String getLabel();
	
	/**
	 * This is the recommended method because an {@link AnalyserRuleSet} share the
	 * different graphs used by the different RuleProperties.
	 * @param ruleSet
	 * @return
	 */
	boolean check(AnalyserRuleSet ruleSet);

	boolean check(Rule rule);
	
	boolean check(Iterable<Rule> ruleSet);
}
