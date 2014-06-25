package fr.lirmm.graphik.graal.store;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.util.stream.ObjectReader;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 */
public interface ReadOnlyStore extends ReadOnlyAtomSet {

    /**
     * Get an atom stream from this store.
     */
    ObjectReader<Atom> iterator();

    SymbolGenerator getFreeVarGen();

    boolean contains(Atom atom) throws StoreException;

    Set<Term> getTerms() throws StoreException;
    
    

};
