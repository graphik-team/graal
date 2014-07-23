/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class FUSProperty extends AbstractRuleProperty {

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
	public boolean check(Rule rule) {
		return false;
	}

}
