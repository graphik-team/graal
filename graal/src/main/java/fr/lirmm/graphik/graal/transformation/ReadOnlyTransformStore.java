package fr.lirmm.graphik.graal.transformation;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.SolverException;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;
import fr.lirmm.graphik.graal.solver.StaticSolver;
import fr.lirmm.graphik.graal.store.ReadOnlyStore;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.util.stream.ObjectReader;

public class ReadOnlyTransformStore implements ReadOnlyStore {

    private ReadOnlyAtomSet store;
    private AAtomTransformator transformator;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    public ReadOnlyTransformStore(ReadOnlyAtomSet atomSet, AAtomTransformator transformator) {
        this.store = atomSet;
        this.transformator = transformator;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    public AAtomTransformator getAtomTransformator() {
        return this.transformator;
    }

    @Override
    public boolean contains(Atom atom) throws StoreException {
        Query query = new DefaultConjunctiveQuery(this.transformator.transform(atom));
        try {
			return StaticSolver.executeQuery(query, this).hasNext();
		} catch (SolverFactoryException e) {
			throw new StoreException(e);
		} catch (SolverException e) {
			throw new StoreException(e);
		}
    }

    /*@Override
    public SubstitutionReader execute(Query query) throws StoreException {
        if (query instanceof ConjunctiveQuery) {
            ConjunctiveQuery cquery = (ConjunctiveQuery) query;
            cquery.setAtomSet(this.transformator.transform(cquery.getAtomSet()));
        } else {
            throw new StoreException("Unsupported kind of query");
        }
        return this.store.execute(query);
    }*/

    @Override
    public ObjectReader<Atom> iterator() {
        return this.transformator.transform(this.store.iterator());
    }

    @Override
    public Set<Term> getTerms() throws StoreException {
        try {
			return this.store.getTerms();
		} catch (AtomSetException e) {
			throw new StoreException(e);
		}
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
     * @see fr.lirmm.graphik.alaska.store.IStore#getFreeVarGen()
     */
    @Override
    public SymbolGenerator getFreeVarGen() {
    	// TODO implement this method
        throw new Error("This method isn't implemented");
        //return this.store.getFreeVarGen();
    }

    // /////////////////////////////////////////////////////////////////////////
    // PROTECTED METHODS
    // /////////////////////////////////////////////////////////////////////////

    protected ReadOnlyAtomSet getStore() {
        return this.store;
    }

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.AtomSet#getAllPredicate()
	 */
	@Override
	public ObjectReader<Predicate> getAllPredicates() throws AtomSetException {
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

}
