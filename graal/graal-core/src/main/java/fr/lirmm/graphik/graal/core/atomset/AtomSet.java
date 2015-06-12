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
 package fr.lirmm.graphik.graal.core.atomset;

import java.util.Iterator;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public interface AtomSet extends Iterable<Atom> {

	/**
	 * Returns true if this atom set contains the specified atom.
	 * 
	 * @param atom
	 * @return true if this atom set contains the specified atom.
	 * @throws AtomSetException
	 */
	boolean contains(Atom atom) throws AtomSetException;

	/**
	 * Returns a Set of all predicates in this atom set.
	 * 
	 * @return
	 * @throws AtomSetException
	 */
	Set<Predicate> getPredicates() throws AtomSetException;

	/**
	 * Returns an iterator of all predicates in this atom set. Each predicate is
	 * iterated only once time.
	 * 
	 * @return
	 * @throws AtomSetException
	 */
	Iterator<Predicate> predicatesIterator() throws AtomSetException;

	/**
	 * Returns a Set of all terms in this atom set.
	 * 
	 * @return
	 * @throws IAtomSetException
	 */
	Set<Term> getTerms() throws AtomSetException;

	/**
	 * Returns an iterator of all terms in this atom set. Each term is iterated
	 * only once time.
	 * 
	 * @return
	 * @throws AtomSetException
	 */
	Iterator<Term> termsIterator() throws AtomSetException;
	
	/**
	 * Returns a Set of all terms of the specified type in this atom set.
	 * 
	 * @param type
	 * @return a collection of all terms of the specified type in this atom set.
	 * @throws AtomSetException
	 */
	Set<Term> getTerms(Term.Type type) throws AtomSetException;


	/**
	 * Retuns on iterator of all terms of the specified type in this atom set.
	 * Each term is iterated only once time.
	 * 
	 * @param type
	 * @return
	 * @throws AtomSetException
	 */
	Iterator<Term> termsIterator(Term.Type type) throws AtomSetException;

	/**
	 * Use AtomSets.contains instead.
	 * 
	 * Check if all atoms of this AtomSet are also contained in the specified
	 * AtomSet.
	 * 
	 * @param atomset
	 * @return
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
	 * @return true if this atomset changed as a result of the call
	 * @throws
	 */
	boolean addAll(Iterable<? extends Atom> atoms) throws AtomSetException;
	
	/**
	 * Add the specified atom stream to this atom set.
	 * 
	 * @param atoms
	 *            - the atom iterator to be added
	 * @return true if this atomset changed as a result of the call
	 * @throws
	 */
	boolean addAll(Iterator<? extends Atom> atoms) throws AtomSetException;

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
	 * @param stream
	 *            - the atom stream to be removed.
	 * @return true if this atomset changed as a result of the call
	 * @throws AtomSetException
	 */
	boolean removeAll(Iterable<? extends Atom> atoms) throws AtomSetException;
	
	/**
	 * Remove the specified atom stream from this atom set.
	 * 
	 * @param stream
	 *            - the atom stream to be removed.
	 * @return true if this atomset changed as a result of the call
	 * @throws AtomSetException
	 */
	boolean removeAll(Iterator<? extends Atom> atoms) throws AtomSetException;

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
	Iterator<Atom> iterator();

}
