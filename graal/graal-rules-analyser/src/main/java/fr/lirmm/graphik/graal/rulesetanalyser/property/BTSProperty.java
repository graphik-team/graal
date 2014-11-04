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
public class BTSProperty implements RuleProperty {

	private static BTSProperty instance;

	private BTSProperty() {
	}

	public static synchronized BTSProperty getInstance() {
		if (instance == null)
			instance = new BTSProperty();

		return instance;
	}
	
	@Override
	public String getLabel() {
		return "bts";
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