package fr.lirmm.graphik.graal.store;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public interface Store extends ReadOnlyStore, AtomSet {

    /**
     * Write this atom stream on this store.
     * 
     * @param atoms
     * @throws AtomSetException
     */
    void addAll(Iterable<Atom> atoms) throws AtomSetException;

    /**
     * Write this atom on this store.
     * 
     * @param atom
     * @return true if the atom is correctly added, false otherwise.
     */
    boolean add(Atom atom);
    
    /**
     * Remove this atom stream from this store.
     * 
     * @param stream
     */
    void remove(Iterable<Atom> atoms) throws AtomSetException;

    /**
     * Remove this atom from this store.
     * 
     * @param atom
     * @return true if the atom is correctly removed, false otherwise.
     */
    boolean remove(Atom atom);

}
