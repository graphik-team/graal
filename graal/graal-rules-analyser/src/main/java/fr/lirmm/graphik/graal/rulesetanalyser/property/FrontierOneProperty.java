package fr.lirmm.graphik.graal.rulesetanalyser.property;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * The frontier contains only one variable.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class FrontierOneProperty extends AbstractRuleProperty {

	private static FrontierOneProperty instance = null;

	private FrontierOneProperty() {
	}

	public static synchronized FrontierOneProperty getInstance() {
		if (instance == null) {
			instance = new FrontierOneProperty();
		}
		return instance;
	}

	@Override
	public Boolean check(Rule rule) {
		return rule.getFrontier().size() == 1;
	}

	@Override
	public String getLabel() {
		return "fr1";
	}

}
