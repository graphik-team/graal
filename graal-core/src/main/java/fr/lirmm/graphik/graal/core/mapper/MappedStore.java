/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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
package fr.lirmm.graphik.graal.core.mapper;

import java.io.Closeable;
import java.io.IOException;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.TermGenerator;
import fr.lirmm.graphik.graal.api.core.mapper.Mapper;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.core.store.AbstractStore;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@SuppressWarnings("deprecation")
public class MappedStore extends AbstractStore {

	private Store store;
	private Mapper mapper;
	private MapperAtomConverter converter;
	private MapperAtomConverter unconverter;
	private MapperPredicateConverter predicateUnconverter;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public MappedStore(Store store, Mapper mapper) {
		this.store = store;
		this.mapper = mapper;
		this.converter = new MapperAtomConverter(this.mapper);
		this.unconverter = new MapperAtomConverter(this.mapper.inverse());
		this.predicateUnconverter = new MapperPredicateConverter(this.mapper.inverse());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean isWriteable() throws AtomSetException {
		return store.isWriteable();
	}
		
	@Override
	public CloseableIterator<Atom> iterator() {
		return new ConverterCloseableIterator<Atom, Atom>(store.iterator(), unconverter);
	}

	@Override
	public CloseableIterator<Atom> match(Atom atom, Substitution s) throws AtomSetException {
		CloseableIterator<Atom> match = store.match(mapper.map(atom), s);
		return new ConverterCloseableIterator<Atom, Atom>(match, unconverter);
	}

	@Override
	public CloseableIterator<Atom> atomsByPredicate(Predicate p) throws AtomSetException {
		CloseableIterator<Atom> atomsByPredicate = store.atomsByPredicate(mapper.map(p));
		return new ConverterCloseableIterator<Atom, Atom>(atomsByPredicate, unconverter);
	}

	@Override
	public CloseableIterator<Term> termsByPredicatePosition(Predicate p, int position) throws AtomSetException {
		return store.termsByPredicatePosition(mapper.map(p), position);
	}

	@Override
	public CloseableIterator<Predicate> predicatesIterator() throws AtomSetException {
		return new ConverterCloseableIterator<Predicate, Predicate>(store.predicatesIterator(), predicateUnconverter);
	}

	@Override
	public CloseableIterator<Term> termsIterator() throws AtomSetException {
		return store.termsIterator();
	}

	@Override
	@Deprecated
	public CloseableIterator<Term> termsIterator(Type type) throws AtomSetException {
		return store.termsIterator(type);
	}

	@Override
	public void close() {
		if(store instanceof Closeable) {
			try {
				((Closeable) store).close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public boolean add(Atom atom) throws AtomSetException {
		return store.add(mapper.map(atom));
	}
	
	@SuppressWarnings("unchecked")
	public boolean addAll(CloseableIterator<? extends Atom> it) throws AtomSetException {
		return store.addAll(new ConverterCloseableIterator<Atom, Atom>((CloseableIterator<Atom>) it, this.converter));
	}


	@Override
	public boolean remove(Atom atom) throws AtomSetException {
		return store.remove(mapper.map(atom));

	}

	@Override
	public void clear() throws AtomSetException {
		store.clear();
	}

	@Override
	public TermGenerator getFreshSymbolGenerator() {
		return store.getFreshSymbolGenerator();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
