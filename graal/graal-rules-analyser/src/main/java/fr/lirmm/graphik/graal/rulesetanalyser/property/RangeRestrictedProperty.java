/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
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
public final class RangeRestrictedProperty extends AbstractRuleProperty {

	private static RangeRestrictedProperty instance = null;
	
	private RangeRestrictedProperty(){}
	
	public static synchronized RangeRestrictedProperty getInstance() {
		if(instance == null) {
			instance = new RangeRestrictedProperty();
		}
		return instance;	
	}
	
	@Override
	public Boolean check(Rule rule) {
		return rule.getExistentials().isEmpty();
	}

	@Override
	public String getLabel() {
		return "rr";
	}

}
