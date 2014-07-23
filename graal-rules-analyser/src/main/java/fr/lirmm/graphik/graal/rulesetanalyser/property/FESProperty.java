/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class FESProperty extends AbstractRuleProperty {

	private static FESProperty instance;

	private FESProperty() {
	}

	public static FESProperty getInstance() {
		if (instance == null)
			instance = new FESProperty();

		return instance;
	}
	
	@Override
	public String getLabel() {
		return "fes";
	}

	@Override
	public boolean check(Rule rule) {
		return false;
	}

}