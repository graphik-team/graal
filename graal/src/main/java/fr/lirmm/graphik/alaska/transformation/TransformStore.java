package fr.lirmm.graphik.alaska.transformation;

import java.util.Set;

import fr.lirmm.graphik.alaska.Alaska;
import fr.lirmm.graphik.alaska.solver.SolverException;
import fr.lirmm.graphik.alaska.solver.SolverFactoryException;
import fr.lirmm.graphik.alaska.store.ReadOnlyStore;
import fr.lirmm.graphik.alaska.store.StoreException;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.kb.core.Predicate;
import fr.lirmm.graphik.kb.core.Query;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.core.Term;
import fr.lirmm.graphik.kb.core.Term.Type;
import fr.lirmm.graphik.kb.exception.AtomSetException;
import fr.lirmm.graphik.kb.SymbolGenerator;
import fr.lirmm.graphik.util.stream.ObjectReader;

public class TransformStore implements ReadOnlyStore {

    private ReadOnlyAtomSet store;
    private AAtomTransformator transformator;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    public TransformStore(ReadOnlyAtomSet atomSet, AAtomTransformator transformator) {
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
			return Alaska.execute(query, this).hasNext();
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
	public ObjectReader<Predicate> getAllPredicate() throws AtomSetException {
		// TODO implement this method
		throw new Error("This method isn't implemented");
	}

}
