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
 /**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms;

import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.VariableGenerator;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.stream.SubstitutionIterator2AtomIterator;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
class RdbmsAtomIterator implements CloseableIterator<Atom> {
	
	private RdbmsStore store;
	private boolean hasNextCallDone = false;
	private CloseableIterator<Predicate> predicateIt;
	private CloseableIterator<Atom> atomIt;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	RdbmsAtomIterator(RdbmsStore store) throws AtomSetException {
		this.store = store;
		this.init();
	}

	private void init() throws AtomSetException {
		this.predicateIt = store.predicatesIterator();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() throws IteratorException {
		if (!this.hasNextCallDone) {
			this.hasNextCallDone = true;
			while (this.predicateIt.hasNext()
				   && (this.atomIt == null || !this.atomIt.hasNext())) {
				Predicate p = predicateIt.next();
				List<Term> terms = new LinkedList<Term>();
				VariableGenerator gen = new DefaultVariableGenerator("X");
				for(int i=0; i<p.getArity(); ++i) {
					terms.add(gen.getFreshSymbol());
				}
				
				InMemoryAtomSet atomSet = new LinkedListAtomSet();
				Atom atom = new DefaultAtom(p, terms);
				atomSet.add(atom);
				
				ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(atomSet);
				
				SqlHomomorphism solver = SqlHomomorphism.instance();
				try {
					this.atomIt = new SubstitutionIterator2AtomIterator(atom, solver.execute(query, this.store));
				} catch (HomomorphismException e) {
					throw new IteratorException(e);
				}
			}
		}
		return this.atomIt != null && this.atomIt.hasNext();
	}

	@Override
	public Atom next() throws IteratorException {
		if (!this.hasNextCallDone)
			this.hasNext();
		this.hasNextCallDone = false;

		return this.atomIt.next();
	}

	@Override
    public void close() {
		if (this.predicateIt != null)
			this.predicateIt.close();

		if (this.atomIt != null)
			this.atomIt.close();
    }

}
