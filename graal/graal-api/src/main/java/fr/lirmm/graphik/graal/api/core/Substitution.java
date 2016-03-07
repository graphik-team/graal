/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 package fr.lirmm.graphik.graal.api.core;

import java.util.Set;

import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

/**
 * A substitution is a syntactic transformation of a logical expression. This
 * transformation replace some variables by other terms.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 */
public interface Substitution extends AppendableToStringBuilder {

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
	 * Get the image of the given term by this substitution.
	 * 
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
