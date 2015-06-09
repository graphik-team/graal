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

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * A substitution represents a set of transformation of a variable into a term.
 * To apply a substitution to a logical expression replace each variable symbols
 * by its substitute.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 */
public interface Substitution {

	/** 
	 * Get all terms that have a substitute.
	 * @return
	 */
	Set<Term> getTerms();
	
	/**
	 * Get all substitutes of this substitution.
	 * @return
	 */
	Set<Term> getValues();

	/**
	 * Get the substitute of the given term.
	 * @param term
	 * @return the substitute.
	 */
	Term createImageOf(Term term);

	/**
	 * Add a term substitution.
	 * @param term the term to substitute.
	 * @param substitut its substitute.
	 * @return false if a constant term is substituted by another constant term,
	 *         true otherwise.
	 */
	boolean put(Term term, Term substitut);

	/**
	 * Add all term substitution of an other substitution instance.
	 * @param s
	 */
	void put(Substitution s);

	/**
	 * Apply this substitution on an atom.
	 * @param atom (const)
	 * @return
	 */
	Atom createImageOf(Atom atom);

	/**
	 * Apply this substitution on an atom set.
	 * 
	 * @param src
	 *            (const)
	 * @return
	 * @throws AtomSetException
	 */
	InMemoryAtomSet createImageOf(AtomSet src);

	/**
	 * Apply this substitution on the given rule.
	 * 
	 * @param rule
	 *            (const)
	 * @return
	 * @throws AtomSetException
	 */
	Rule createImageOf(Rule rule);

	/**
	 * Insert the application of this substitution on the src atom set into the
	 * target atom set.
	 * 
	 * @param src
	 *            (const)
	 * @param dest
	 * @throws AtomSetException
	 */
	void apply(AtomSet src, AtomSet target) throws AtomSetException;

	/**
	 * Insert the application of this substitution on the src atom set into the
	 * target atom set.
	 * 
	 * @param src
	 *            (const)
	 * @param dest
	 */
	void apply(AtomSet src, InMemoryAtomSet target);

	/**
	 * The composition of a substitution is more complex that just put an other
	 * term substitution. If the current substitution is {X -> Y} and you add
	 * a term substitution {Y -> 'a'}, the result is {X -> 'a', Y -> 'a'}.
	 * 
	 * An other example is {X -> 'a'} + {X -> Y} => {Y -> 'a', X -> 'a'}.
	 * 
	 * @param term
	 * @param substitut
	 * @return false if the composition imply a substitution of a constant term
	 *         by another constant term, true otherwise.
	 * @throws SubstitutionException
	 */
	boolean compose(Term term, Term substitut);

	/**
	 * (const)
	 * 
	 * @param s
	 * @return null if the composition imply a substitution of a constant term
	 *         by another constant term, the result of the composition
	 *         otherwise.
	 * @throws SubstitutionException
	 */
	Substitution compose(Substitution s);

};
