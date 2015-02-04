package fr.lirmm.graphik.graal.core.atomset;

import java.util.Iterator;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;

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
	 * 
	 * @return
	 * @throws AtomSetException
	 */
	Iterable<Predicate> getAllPredicates() throws AtomSetException;

	/**
	 * Returns a collection of all terms in this atom set.
	 * 
	 * @return
	 * @throws IAtomSetException
	 */
	Set<Term> getTerms() throws AtomSetException;

	/**
	 * Returns a collection of all terms of the specified type in this atom set.
	 * 
	 * @param type
	 * @return a collection of all terms of the specified type in this atom set.
	 */
	Set<Term> getTerms(Term.Type type);

	/**
	 * Check if all atoms of this AtomSet are also contained in the specified
	 * AtomSet.
	 * 
	 * @param atomset
	 * @return
	 */
	boolean isSubSetOf(AtomSet atomset);

	boolean isEmpty();

	/**
	 * Add the specified atom to this atom set if is not already present.
	 * 
	 * @param atom
	 *            - atom to be added to this atom set
	 * @return true if this atom set did not already contain the specified atom
	 */
	boolean add(Atom atom);

	/**
	 * Add the specified atom stream to this atom set.
	 * 
	 * @param stream
	 *            - the atom stream to be added
	 * @return true if this atomset changed as a result of the call
	 * @throws
	 */
	boolean addAll(Iterable<? extends Atom> atoms) throws AtomSetException;

	/**
	 * Remove the specified atom from this this atom set.
	 * 
	 * @param atom
	 *            - the atom to be removed
	 * @return true if this atom set contained the specified atom.
	 */
	boolean remove(Atom atom);

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
	 * 
	 */
	void clear();
	
	/**
	 * Returns an iterator over the atoms in this atom set.
	 * 
	 * @return an iterator over the atoms in this atom set.
	 */
	@Override
	Iterator<Atom> iterator();	

}
