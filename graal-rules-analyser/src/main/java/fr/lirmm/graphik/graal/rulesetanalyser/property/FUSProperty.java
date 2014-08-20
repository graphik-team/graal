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
public class FUSProperty implements RuleProperty {

	private static FUSProperty instance;

	private FUSProperty() {
	}

	public static FUSProperty getInstance() {
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
