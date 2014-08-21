/**
 * 
 */
package fr.lirmm.graphik.graal.core.factory;

import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RuleFactory {

	private static RuleFactory instance = new RuleFactory();
	
	private RuleFactory() {
	}

	public static RuleFactory getInstance() {
		return instance;
	}

	public Rule createRule() {
		return new DefaultRule();
	}

}