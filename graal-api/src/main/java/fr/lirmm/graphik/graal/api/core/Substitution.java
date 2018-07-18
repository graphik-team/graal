/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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

import java.util.Collection;
import java.util.List;
import java.util.Set;

import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

/**
 * A substitution is an application from a set of variables into a set of terms.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 */
public interface Substitution extends Comparable<Substitution>, AppendableToStringBuilder {

	/**
	 * Get the domain of this substitution.
	 * 
	 * @return the domain of this substitution.
	 */
	Set<Variable> getTerms();
	
	/**
	 * Get the codomain of this substitution.
	 * 
	 * @return the codomain of this substitution.
	 */
	Set<Term> getValues();

	/**
	 * Get the image of the given term by this substitution, if there is no
	 * image specified return the term itself.
	 * 
	 * @param term
	 * @return the image of the specified term.
	 */
	Term createImageOf(Term term);

	/**
	 * Get the image of each terms of the specified list.
	 * 
	 * @param terms
	 * @return A list of images of each terms.
	 */
	List<Term> createImageOf(Collection<? extends Term> terms);

	/**
	 * Adds a mapping from the specified variable into the specified image to
	 * this substitution. Returns false if there already exists an other image
	 * for this variable, true otherwise.
	 * 
	 * @param var
	 * @param image
	 * 
	 */
	boolean put(Variable var, Term image);

	/**
	 * Add all mappings from an other substitution instance. Returns false if
	 * there already exists an other image for a variable from the domain of s,
	 * true otherwise.
	 * 
	 * @param s
	 * @return false if there already exists an other image for a variable from the domain of s, true otherwise.
	 */
	boolean put(Substitution s);

	/**
	 * Remove the mapping for the specified variable.
	 * 
	 * @param var
	 * @return true if there was a mapping.
	 */
	boolean remove(Variable var);

	/**
	 * The aggregation of a substitution is more complex that just add an new
	 * mapping for a new variable. Especially, it does not conserve the domain
	 * and codomain. It choose a representative term for each connected
	 * component by successive application of the mapping.
	 * 
	 * For example, if the current substitution is {X -> Y, Z -> U} and you add a
	 * mapping {Y -> 'a', Z -> V}, the result is {Y -> 'a', X -> 'a', Z -> V, U -> V}
	 * or {Y -> 'a', X -> 'a', U -> Z, V -> Z} or {Y -> 'a', X -> 'a', Z -> U, V -> U}.
	 * 
	 * 
	 * @param var
	 * @param image
	 * @return false, if the aggregation put two constants into a same connected
	 *         component.
	 */
	boolean aggregate(Variable var, Term image);

	/**
	 * (const) This method construct a new Substitution which is the aggregation
	 * of this substitution with the specified one.
	 * 
	 * @param s
	 * @return null, if the aggregation put two constants into a same connected
	 *         component.
	 */
	Substitution aggregate(Substitution s);
	
	/**
	 * For example, if the current substitution is {Y -> Z} and you compose it with
	 * {X -> Y, V -> U}, the result is {X -> Z, Y -> Z, V -> U}.
	 * 
	 * @param term
	 * @param substitut
	 * @return TODO
	 */
	boolean compose(Variable term, Term substitut);
	
	/**
	 * (CONST) <br>
	 * This method construct a new Substitution which is the composition of this
	 * substitution with the specified one. 
	 * 
	 * For example, if the current substitution is {Y -> Z} and you compose it with
	 * {X -> Y, V -> U}, the result is {X -> Z, Y -> Z, V -> U}.
	 * 
	 * @param s
	 * @return TODO
	 */
	Substitution compose(Substitution s);

	/**
	 * Apply this substitution on an atom.
	 * @param atom (const)
	 * @return an Atom which is the image of specified one by this substitution.
	 */
	Atom createImageOf(Atom atom);

	/**
	 * Apply this substitution on an atom set.
	 * 
	 * @param src
	 *            (const)
	 * @return an InMemoryAtomSet which is the image of specified one by this substitution.
	 * @throws AtomSetException
	 */
	InMemoryAtomSet createImageOf(AtomSet src) throws AtomSetException;

	/**
	 * Apply this substitution on an atom set.
	 * 
	 * @param src
	 *            (const)
	 * @return an InMemoryAtomSet which is the image of specified one by this substitution.
	 */
	InMemoryAtomSet createImageOf(InMemoryAtomSet src);

	/**
	 * Apply this substitution on the given rule.
	 * 
	 * @param rule
	 *            (const)
	 * @return an Rule which is the image of specified one by this substitution.
	 */
	Rule createImageOf(Rule rule);
	
	/**
	 * Apply this substitution on the given conjunctive query.
	 * 
	 * @param cq
	 *            (const)
	 * @return an conjunctive query which is the image of specified one by this substitution.
	 */
	ConjunctiveQuery createImageOf(ConjunctiveQuery cq);

	/**
	 * Insert the application of this substitution on the src atom set into the
	 * target atom set.
	 * 
	 * @param src
	 *            (const)
	 * @param target
	 * @throws AtomSetException
	 */
	void apply(AtomSet src, AtomSet target) throws AtomSetException;

	/**
	 * Insert the application of this substitution on the src atom set into the
	 * target atom set.
	 * 
	 * @param src
	 *            (const)
	 * @param target
	 */
	void apply(InMemoryAtomSet src, InMemoryAtomSet target);


};
