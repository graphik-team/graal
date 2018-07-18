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

import java.util.Set;

import fr.lirmm.graphik.util.stream.CloseableIterable;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * This interface represents a set of atoms. You can interpret this set like
 * you want, disjunction or conjunction of atoms. However, in Graal, it is 
 * almost always interpreted as a <em>conjunction</em>.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public interface AtomSet extends CloseableIterable<Atom> {

	/**
	 * Returns true if this atom set contains the specified atom.
	 * 
	 * @param atom
	 * @return true if this atom set contains the specified atom.
	 * @throws AtomSetException
	 */
	boolean contains(Atom atom) throws AtomSetException;
	
	/**
	 * Returns an iterator over atoms that match predicate and constants from
	 * the specified atom.
	 * 
	 * @param atom
	 * @return an iterator over atoms.
	 * @throws AtomSetException
	 */
	CloseableIterator<Atom> match(Atom atom) throws AtomSetException;
	
	/**
	 * Returns an iterator over atoms that match predicate and constants from
	 * the specified atom.
	 * 
	 * @param atom
	 * @param s a substitution of Variable from atom into Term from this atom set.
	 * @return an iterator over atoms.
	 * @throws AtomSetException
	 */
	CloseableIterator<Atom> match(Atom atom, Substitution s) throws AtomSetException;

	/**
	 * Returns an iterator over all atoms with the specified predicate.
	 * 
	 * @param predicate
	 * @return an iterator over all atoms with the specified predicate.
	 * @throws AtomSetException
	 */
	CloseableIterator<Atom> atomsByPredicate(Predicate predicate) throws AtomSetException;

	/**
	 * Returns an iterator over terms which are in a specific position in at
	 * least one atom with the specified predicate.
	 * 
	 * @param p
	 * @param position
	 *            the term position in atoms, positions starts from 0.
	 * @return an iterator over terms which appear in the specified position of the specified predicate.
	 */
	CloseableIterator<Term> termsByPredicatePosition(Predicate p, int position) throws AtomSetException;

	/**
	 * Returns a Set of all predicates in this atom set.
	 * 
	 * @return a Set of all predicates.
	 * @throws AtomSetException
	 */
	Set<Predicate> getPredicates() throws AtomSetException;

	/**
	 * Returns an iterator over all predicates in this atom set. Each predicate is
	 * iterated only once time.
	 * 
	 * @return an iterator over all predicates.
	 * @throws AtomSetException
	 */
	CloseableIterator<Predicate> predicatesIterator() throws AtomSetException;

	/**
	 * Returns a Set of all terms in this atom set.
	 * 
	 * @return a Set of all terms.
	 * @throws AtomSetException
	 */
	Set<Term> getTerms() throws AtomSetException;
	
	/**
	 * Returns a Set of all variables in this atom set.
	 * 
	 * @return a Set of all variables.
	 * @throws AtomSetException
	 */
	Set<Variable> getVariables() throws AtomSetException;
	
	/**
	 * Returns a Set of all constants in this atom set.
	 * 
	 * @return a Set of all constants.
	 * @throws AtomSetException
	 */
	Set<Constant> getConstants() throws AtomSetException;
	
	/**
	 * Returns a Set of all literals in this atom set.
	 * 
	 * @return a Set of all literals.
	 * @throws AtomSetException
	 */
	Set<Literal> getLiterals() throws AtomSetException;
	
	/**
	 * This method is deprecated since 1.3, use {@link #getVariables()}, {@link #getConstants()} or {@link #getLiterals()} instead.
	 * <br><br>
	 * 
	 * Returns a Set of all terms of the specified type in this atom set.
	 * 
	 * @param type
	 * @return a collection of all terms of the specified type in this atom set.
	 * @throws AtomSetException
	 */
	@Deprecated
	Set<Term> getTerms(Term.Type type) throws AtomSetException;

	/**
	 * Returns an iterator over all terms in this atom set. Each term is iterated
	 * only once time.
	 * 
	 * @return an iterator over all terms.
	 * @throws AtomSetException
	 */
	CloseableIterator<Term> termsIterator() throws AtomSetException;
	
	/**
	 * Returns an iterator over all variables in this atom set. Each term is iterated
	 * only once time.
	 * 
	 * @return an iterator over all variables.
	 * @throws AtomSetException
	 */
	CloseableIterator<Variable> variablesIterator() throws AtomSetException;
	
	/**
	 * Returns an iterator over all constants in this atom set. Each term is iterated
	 * only once time.
	 * 
	 * @return an iterator over all constants.
	 * @throws AtomSetException
	 */
	CloseableIterator<Constant> constantsIterator() throws AtomSetException;
	
	/**
	 * Returns an iterator over all literals in this atom set. Each term is iterated
	 * only once time.
	 * 
	 * @return an iterator over all literals.
	 * @throws AtomSetException
	 */
	CloseableIterator<Literal> literalsIterator() throws AtomSetException;
	


	/**
	 * This method is deprecated since 1.3, use {@link #variablesIterator()}, {@link #constantsIterator()} or {@link #literalsIterator()} instead.
	 * <br><br>
	 * 
	 * Returns an iterator of all terms of the specified type in this atom set.
	 * Each term is iterated only once time.
	 * 
	 * @param type
	 * @return an iterator of all terms.
	 * @throws AtomSetException
	 */
	@Deprecated
	CloseableIterator<Term> termsIterator(Term.Type type) throws AtomSetException;

	/**
	 * Use AtomSets.contains instead.
	 * 
	 * Check if all atoms of this AtomSet are also contained in the specified
	 * AtomSet.
	 * 
	 * @param atomset
	 * @return true if all atoms of this AtomSet are also contained in the specified atomset, false otherwise.
	 */
	@Deprecated 
	boolean isSubSetOf(AtomSet atomset) throws AtomSetException;

	/**
	 * 
	 * @return true if this atom set is empty, false otherwise.
	 */
	boolean isEmpty() throws AtomSetException;

	/**
	 * Add the specified atom to this atom set if is not already present.
	 * 
	 * @param atom
	 *            - atom to be added to this atom set
	 * @return true if this atom set did not already contain the specified atom
	 */
	boolean add(Atom atom) throws AtomSetException;

	/**
	 * Add the specified atom stream to this atom set.
	 * 
	 * @param atoms
	 * @return true if this atomset changed as a result of the call.
	 * @throws AtomSetException
	 */
	boolean addAll(CloseableIterator<? extends Atom> atoms) throws AtomSetException;

	/**
	 * @param atoms
	 * @return rue if this atomset changed as a result of the call.
	 */
	boolean addAll(AtomSet atoms) throws AtomSetException;

	/**
	 * Remove the specified atom from this this atom set.
	 * 
	 * @param atom
	 *            - the atom to be removed
	 * @return true if this atom set contained the specified atom.
	 */
	boolean remove(Atom atom) throws AtomSetException;

	/**
	 * Remove the specified atom stream from this atom set.
	 * 
	 * @param atoms
	 *            - the atom stream to be removed.
	 * @return true if this atomset changed as a result of the call
	 * @throws AtomSetException
	 */
	boolean removeAll(CloseableIterator<? extends Atom> atoms) throws AtomSetException;

	boolean removeAll(AtomSet atoms) throws AtomSetException;

	/**
	 * 
	 */
	void clear() throws AtomSetException;

	/**
	 * Returns an iterator over the atoms in this atom set.
	 * 
	 * @return an iterator over the atoms in this atom set.
	 */
	@Override
	CloseableIterator<Atom> iterator();
	

	/**
	 * @return the SymbolGenerator used by this atomset.
	 */
	TermGenerator getFreshSymbolGenerator();

}
