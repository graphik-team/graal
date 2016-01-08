/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 package fr.lirmm.graphik.graal.transformation;

import java.util.Iterator;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.AbstractAtomSet;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.homomorphism.DefaultHomomorphismFactory;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.stream.GIterator;
import fr.lirmm.graphik.util.stream.IteratorAdapter;

public class TransformAtomSet extends AbstractAtomSet implements AtomSet {

	private AtomSet store;
	private AAtomTransformator transformator;

	static {
		DefaultHomomorphismFactory.instance().addChecker(
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
		Query query = ConjunctiveQueryFactory.instance().create(
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
	public GIterator<Atom> match(Atom atom) throws AtomSetException {
		throw new MethodNotImplementedError();
	}

	@Override
	public GIterator<Atom> iterator() {
		return this.transformator.transform(this.store.iterator());
	}

	@Override
	public Set<Term> getTerms() throws AtomSetException {
		return this.store.getTerms();
	}

	@Override
	public GIterator<Term> termsIterator() throws AtomSetException {
		return new IteratorAdapter<Term>(this.getTerms().iterator());
	}

	@Override
	public Set<Term> getTerms(Type type) throws AtomSetException {
		return this.store.getTerms(type);
	}

	@Override
	public GIterator<Term> termsIterator(Term.Type type) throws AtomSetException {
		return new IteratorAdapter<Term>(this.getTerms(type).iterator());
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
	public boolean addAll(Iterator<? extends Atom> atoms)
			throws AtomSetException {
		return this.getStore().addAll(
				this.getAtomTransformator().transform(atoms));
	}

	@Override
	public boolean removeAll(Iterator<? extends Atom> stream)
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

	@Override
	public GIterator<Predicate> predicatesIterator() throws AtomSetException {
		return new IteratorAdapter<Predicate>(this.getPredicates().iterator());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected AtomSet getStore() {
		return this.store;
	}

}
