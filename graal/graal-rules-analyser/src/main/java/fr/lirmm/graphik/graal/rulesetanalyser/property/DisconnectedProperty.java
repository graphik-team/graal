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
public final class DisconnectedProperty extends AbstractRuleProperty {

	private static DisconnectedProperty instance = null;
	
	private DisconnectedProperty(){}
	
	public static synchronized DisconnectedProperty getInstance() {
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
