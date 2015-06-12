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
import java.util.List;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * This interface represents a conjunctive query.
 * A conjunctive query is composed of a fact and a set of answer variables.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ConjunctiveQuery extends Query, Iterable<Atom> {

	/**
	 * The label (the name) for this query.
	 * 
	 * @return
	 */
	String getLabel();
	
	/**
	 * Get the atom conjunction representing the query.
	 * @return an atom set representing the atom conjunction of the query.
	 */
	InMemoryAtomSet getAtomSet();

	/**
	 * Get the answer variables
	 * @return an Collection of Term representing the answer variables.
	 */
	List<Term> getAnswerVariables();

	void setAnswerVariables(List<Term> ans);
	/**
	 * Iterator of the atom query conjunction.
	 */
	@Override
	Iterator<Atom> iterator();
	
}
