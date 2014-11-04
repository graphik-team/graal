package fr.lirmm.graphik.graal.store.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.parser.misc.StringAtomReader;
import fr.lirmm.graphik.graal.parser.misc.StringAtomWriter;
import fr.lirmm.graphik.graal.parser.misc.StringFormat;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.util.stream.ObjectReader;
import fr.lirmm.graphik.util.stream.ObjectWriter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class StdStreamStore implements Store {

    private static final Logger logger = LoggerFactory
            .getLogger(StdStreamStore.class);

    private ObjectWriter<Atom> atomWriter;
    private ObjectReader<Atom> atomReader;

    public StdStreamStore(StringFormat representation) {
        this.atomWriter = new StringAtomWriter(new BufferedWriter(
                new OutputStreamWriter(System.out)), representation);
        this.atomReader = new StringAtomReader(new BufferedReader(
                new InputStreamReader(System.in)), representation);
    }

    @Override
    public void addAll(Iterable<Atom> atoms) throws AtomSetException {
        try {
            this.atomWriter.write(atoms);
        } catch (IOException e) {
            logger.error("Error while writting on std out", e);
        }
    }

    @Override
    public ObjectReader<Atom> iterator() {
        return this.atomReader;
    }

    @Override
    public SymbolGenerator getFreeVarGen() {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    @Override
    public Set<Term> getTerms() {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    @Override
    public boolean contains(Atom atom) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    @Override
    public boolean remove(Atom atom) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.kb.IAtomSet#getTerms(fr.lirmm.graphik.kb.ITerm.Type)
     */
    @Override
    public Set<Term> getTerms(Type type) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.kb.core.IWriteableAtomSet#add(fr.lirmm.graphik.kb.core
     * .IAtom)
     */
    @Override
    public boolean add(Atom atom) {
        // TODO implement this method
        throw new Error("This method isn't implemented");
    }

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.AtomSet#getAllPredicate()
	 */
	@Override
	public Iterable<Predicate> getAllPredicates() throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.graal.store.Store#remove(java.lang.Iterable)
	 */
	@Override
	public void remove(Iterable<Atom> atoms) throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public boolean isSubSetOf(AtomSet atomset) {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public boolean isEmpty() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

	@Override
	public void clear() {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

}
