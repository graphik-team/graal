/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader2AtomReader;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
class DefaultRdbmsIterator implements Iterator<Atom> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultRdbmsIterator.class);
	
	private RdbmsStore store;
	private boolean hasNextCallDone = false;
	private Iterator<Predicate> predicateStream;
	private Iterator<Atom> atomReader;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	DefaultRdbmsIterator(RdbmsStore store) throws AtomSetException {
		this.store = store;
		this.init();
	}

	private void init() throws AtomSetException {
		this.predicateStream = store.predicatesIterator();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		if (!this.hasNextCallDone) {
			this.hasNextCallDone = true;
			while (this.predicateStream.hasNext()
				   && (this.atomReader == null || !this.atomReader.hasNext())) {
				Predicate p = predicateStream.next();
				List<Term> terms = new LinkedList<Term>();
				for(int i=0; i<p.getArity(); ++i) {
					terms.add(DefaultTermFactory.instance().createVariable(
							"X" + i));
				}
				
				InMemoryAtomSet atomSet = new LinkedListAtomSet();
				Atom atom = new DefaultAtom(p, terms);
				atomSet.add(atom);
				
				ConjunctiveQuery query = ConjunctiveQueryFactory.instance().create(atomSet);
				
				SqlHomomorphism solver = SqlHomomorphism.getInstance();
				try {
					this.atomReader = new SubstitutionReader2AtomReader(atom, solver.execute(query, this.store));
				} catch (HomomorphismException e) {
					LOGGER.error(e.getMessage(), e);
					return false;
				}
			}
		}
		return this.atomReader != null && this.atomReader.hasNext();
	}

	@Override
	public Atom next() {
		if (!this.hasNextCallDone)
			this.hasNext();
		this.hasNextCallDone = false;

		return this.atomReader.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
