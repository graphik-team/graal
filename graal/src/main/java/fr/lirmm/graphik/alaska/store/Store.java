package fr.lirmm.graphik.alaska.store;

import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.exception.AtomSetException;
import fr.lirmm.graphik.util.stream.ObjectReader;

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
    void add(Iterable<Atom> atoms) throws AtomSetException;

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
    void remove(ObjectReader<Atom> stream) throws AtomSetException;

    /**
     * Remove this atom from this store.
     * 
     * @param atom
     * @return true if the atom is correctly removed, false otherwise.
     */
    boolean remove(Atom atom);

}
