package fr.lirmm.graphik.graal.transformation;

import java.util.Iterator;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.homomorphism.DefaultHomomorphismFactory;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.ObjectReader;

public class TransformAtomSet implements AtomSet {

    private AtomSet store;
    private AAtomTransformator transformator;
    
    static  {
		DefaultHomomorphismFactory.getInstance().addChecker(
				new TransformatorSolverChecker());
	}

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

    public TransformAtomSet(AtomSet atomSet, AAtomTransformator transformator) {
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
    public boolean contains(Atom atom) throws AtomSetException {
        Query query = new DefaultConjunctiveQuery(this.transformator.transform(atom));
        try {
			return StaticHomomorphism.executeQuery(query, this).hasNext();
		} catch (HomomorphismFactoryException e) {
			throw new AtomSetException(e);
		} catch (HomomorphismException e) {
			throw new AtomSetException(e);
		}
    }

    @Override
    public Iterator<Atom> iterator() {
        return this.transformator.transform(this.store).iterator();
    }

    @Override
    public Set<Term> getTerms() throws AtomSetException {
        try {
			return this.store.getTerms();
		} catch (AtomSetException e) {
			throw new AtomSetException(e);
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
    
    @Override
    public boolean add(Atom atom) {
        try {
            this.getStore().addAll(
                    this.getAtomTransformator().transform(atom));
        } catch (AtomSetException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean remove(Atom atom) {
        try {
            return this.getStore().removeAll(
                    this.getAtomTransformator().transform(atom));
        } catch (AtomSetException e) {
            return false;
        }
    }

    @Override
    public boolean addAll(Iterable<? extends Atom> atoms) throws AtomSetException {
        try {
            return this.getStore().addAll(this.getAtomTransformator().transform(atoms));
        } catch (AtomSetException e) {
            throw new AtomSetException(e);
        }
    }

    @Override
    public boolean removeAll(Iterable<? extends Atom> stream) throws AtomSetException {
        try {
            return this.getStore().removeAll(
                    this.getAtomTransformator().transform(stream));
        } catch (AtomSetException e) {
            throw new AtomSetException(e);
        }
    }

	@Override
	public void clear() {
		this.getStore().clear();
	}

    // /////////////////////////////////////////////////////////////////////////
    // PROTECTED METHODS
    // /////////////////////////////////////////////////////////////////////////

    protected AtomSet getStore() {
        return this.store;
    }

	@Override
	public ObjectReader<Predicate> getAllPredicates() throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public boolean isSubSetOf(AtomSet atomset) {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public boolean isEmpty() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}
	
	

}
