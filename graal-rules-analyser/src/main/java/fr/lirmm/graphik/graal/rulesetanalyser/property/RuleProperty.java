package fr.lirmm.graphik.graal.rulesetanalyser.property;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface RuleProperty {
	
	boolean check(Rule rule);
	
	boolean check(Iterable<Rule> rule);
}
