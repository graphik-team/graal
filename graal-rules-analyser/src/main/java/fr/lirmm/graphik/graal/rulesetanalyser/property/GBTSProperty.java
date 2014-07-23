/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class GBTSProperty extends AbstractRuleProperty {

	private static GBTSProperty instance;

	private GBTSProperty() {
	}

	public static GBTSProperty getInstance() {
		if (instance == null)
			instance = new GBTSProperty();

		return instance;
	}
	
	@Override
	public String getLabel() {
		return "gbts";
	}

	@Override
	public boolean check(Rule rule) {
		return false;
	}

}