package fr.lirmm.graphik.graal.rulesetanalyser.property;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * The rule frontier is empty. Note that any disconnected rule needs to be
 * applied only once.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Swan Rocher
 * 
 */
public class DisconnectedProperty extends AbstractRuleProperty {

	private static DisconnectedProperty instance = null;
	
	private DisconnectedProperty(){}
	
	public static DisconnectedProperty getInstance() {
		if(instance == null) {
			instance = new DisconnectedProperty();
		}
		return instance;	
	}
	
	@Override
	public Boolean check(Rule rule) {
		return rule.getFrontier().isEmpty();
	}

	@Override
	public String getLabel() {
		return "disc";
	}

}
