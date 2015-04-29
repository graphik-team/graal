package fr.lirmm.graphik.graal.transformation;

import java.util.Iterator;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.AbstractAtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;
import fr.lirmm.graphik.graal.homomorphism.DefaultHomomorphismFactory;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;

public class TransformAtomSet extends AbstractAtomSet implements AtomSet {

	private AtomSet store;
	private AAtomTransformator transformator;

	static {
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
		Query query = new DefaultConjunctiveQuery(
				this.transformator.transform(atom));
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
		return this.store.getTerms();
	}

	@Override
	public Set<Term> getTerms(Type type) throws AtomSetException {
		return this.store.getTerms(type);
	}

	@Override
	public boolean add(Atom atom) throws AtomSetException {
		return this.getStore().addAll(
				this.getAtomTransformator().transform(atom));

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
	public boolean addAll(Iterable<? extends Atom> atoms)
			throws AtomSetException {
		return this.getStore().addAll(
				this.getAtomTransformator().transform(atoms));
	}

	@Override
	public boolean removeAll(Iterable<? extends Atom> stream)
			throws AtomSetException {
		return this.getStore().removeAll(
				this.getAtomTransformator().transform(stream));
	}

	@Override
	public void clear() throws AtomSetException {
		this.getStore().clear();
	}
	
	@Override
	public Set<Predicate> getPredicates() throws AtomSetException {
		return this.getStore().getPredicates();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected AtomSet getStore() {
		return this.store;
	}

}
