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
 package fr.lirmm.graphik.graal.core;

import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * This interface represents an existential rule.
 * A Rule is a pair (B,H) of atom set such as "B -> H".
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 */
public interface Rule extends Comparable<Rule> {

	/**
	 * Get the label (the name) for this rule.
	 * 
	 * @return
	 */
	String getLabel();

	/**
	 * Set the label (the name) for this rule.
	 * 
	 * @param label
	 */
	void setLabel(String label);

	/**
	 * Get the body (the hypothesis) of this rule.
	 * 
	 * @return
	 */
	InMemoryAtomSet getBody();

	/**
	 * Get the head (the conclusion) of this rule.
	 * 
	 * @return
	 */
	InMemoryAtomSet getHead();

	/**
	 * Compute and return the set of frontier variables of this rule.
	 * 
	 * @return
	 */
	Set<Term> getFrontier();

	/**
	 * Compute and return the set of existential variables of this rule.
	 * 
	 * @return
	 */
	Set<Term> getExistentials();

	/**
	 * Get terms by Type.
	 * 
	 * @return
	 */
	Set<Term> getTerms(Term.Type type);

	/**
	 * Get all terms of this rule.
	 * 
	 * @return
	 */
	Set<Term> getTerms();


	void appendTo(StringBuilder sb);

};
