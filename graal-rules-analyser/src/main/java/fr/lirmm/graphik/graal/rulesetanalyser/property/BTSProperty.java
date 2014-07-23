/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BTSProperty extends AbstractRuleProperty {

	private static BTSProperty instance;

	private BTSProperty() {
	}

	public static BTSProperty getInstance() {
		if (instance == null)
			instance = new BTSProperty();

		return instance;
	}
	
	@Override
	public String getLabel() {
		return "bts";
	}

	@Override
	public boolean check(Rule rule) {
		return false;
	}

}