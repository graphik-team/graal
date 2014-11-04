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
public class GBTSProperty implements RuleProperty {

	private static GBTSProperty instance;

	private GBTSProperty() {
	}

	public static synchronized GBTSProperty getInstance() {
		if (instance == null)
			instance = new GBTSProperty();

		return instance;
	}
	
	@Override
	public String getLabel() {
		return "gbts";
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