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

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public interface KnowledgeBase {

	/**
	 * Get the ontology attached to this knowledge base.
	 * 
	 * @return a RuleSet representing the ontology.
	 */
	RuleSet getOntology();

	/**
	 * Get the facts attached to this knowledgebase.
	 * 
	 * @return an AtomSet representing a conjunction of facts.
	 */
	AtomSet getFacts();

	void load(Iterator<Object> parser) throws AtomSetException;
}
