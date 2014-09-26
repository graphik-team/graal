package fr.lirmm.graphik.graal.core.atomset;

import fr.lirmm.graphik.graal.core.Atom;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public interface AtomSet extends ReadOnlyAtomSet {

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
	 * @throws
	 */
	void addAll(Iterable<Atom> atoms) throws AtomSetException;

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
	 * @throws AtomSetException
	 */
	void remove(Iterable<Atom> atoms) throws AtomSetException;

	/**
	 * 
	 */
	void clear();

}
