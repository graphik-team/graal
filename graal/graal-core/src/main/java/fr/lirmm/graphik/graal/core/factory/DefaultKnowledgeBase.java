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
package fr.lirmm.graphik.graal.core.factory;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.KnowledgeBase;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
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

	public DefaultKnowledgeBase(RuleSet ontology, AtomSet facts) {
		this.ruleset = ontology;
		this.atomset = facts;
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS/SETTERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return the ruleset
	 */
	@Override
	public RuleSet getOntology() {
		return ruleset;
	}

	/**
	 * @return the atomset
	 */
	@Override
	public AtomSet getFacts() {
		return atomset;
	}

	@Override
	public void load(Iterator<Object> parser) throws AtomSetException {
		Object o;
		while (parser.hasNext()) {
			o = parser.next();
			if (o instanceof Rule) {
				this.getOntology().add((Rule) o);
			} else if (o instanceof Atom) {
				this.getFacts().add((Atom) o);
			}
		}
	}

};
