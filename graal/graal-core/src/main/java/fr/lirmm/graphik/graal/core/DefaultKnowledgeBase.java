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
package fr.lirmm.graphik.graal.core;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class DefaultKnowledgeBase implements KnowledgeBase {

	private RuleSet ruleset;
	private AtomSet atomset;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DefaultKnowledgeBase() {
		this.ruleset = new LinkedListRuleSet();
		this.atomset = AtomSetFactory.getInstance().createAtomSet();
	}

	public DefaultKnowledgeBase(RuleSet ruleset, AtomSet atomset) {
		this.ruleset = ruleset;
		this.atomset = atomset;
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS/SETTERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return the ruleset
	 */
	@Override
	public RuleSet getRuleSet() {
		return ruleset;
	}

	/**
	 * @return the atomset
	 */
	@Override
	public AtomSet getAtomSet() {
		return atomset;
	}

};
