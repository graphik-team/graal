/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.property;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * All variables that appear in the head also occur in the body.
 *
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 *
 */
public class RangeRestrictedProperty extends AbstractRuleProperty {

	private static RangeRestrictedProperty instance = null;
	
	private RangeRestrictedProperty(){}
	
	public static RuleProperty getInstance() {
		if(instance == null) {
			instance = new RangeRestrictedProperty();
		}
		return instance;	
	}
	
	@Override
	public boolean check(Rule rule) {
		return rule.getExistentials().isEmpty();
	}

}
