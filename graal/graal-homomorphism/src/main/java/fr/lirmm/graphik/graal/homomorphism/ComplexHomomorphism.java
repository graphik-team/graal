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
 package fr.lirmm.graphik.graal.homomorphism;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.BuiltInPredicate;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.AbstractIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;


public class ComplexHomomorphism<Q extends ConjunctiveQuery, F extends AtomSet> implements Homomorphism<Q,F> {

	private Homomorphism<ConjunctiveQuery,F> rawSolver;
	private LinkedList<Atom> builtInAtoms;
	private Profiler                          profiler;

	public ComplexHomomorphism(Homomorphism<ConjunctiveQuery,F> rawSolver) {
		this.rawSolver = rawSolver;
	}

	@Override
	public <U1 extends Q, U2 extends F> CloseableIterator<Substitution> execute(U1 q, U2 f)
			throws HomomorphismException {
    	InMemoryAtomSet rawAtoms = new LinkedListAtomSet();
		this.builtInAtoms = new LinkedList<Atom>();
		for (Atom a : q) {
			if (a.getPredicate() instanceof BuiltInPredicate) {
				this.builtInAtoms.add(a);
			}
			else {
				rawAtoms.add(a);
			}
		}
		ConjunctiveQuery rawQuery = ConjunctiveQueryFactory.instance().create(rawAtoms);
		rawQuery.setAnswerVariables(q.getAnswerVariables());
		return new BuiltInSubstitutionIterator(this.rawSolver.execute(rawQuery,f));
	}

	protected class BuiltInSubstitutionIterator extends AbstractIterator<Substitution> implements
	        CloseableIterator<Substitution> {

		public BuiltInSubstitutionIterator(CloseableIterator<Substitution> reader) {
			this.rawReader = reader;
		}

		@Override
    	public boolean hasNext() {
			if(this.next == null)
				this.next = this.computeNext();
			return this.next != null;
		}

		@Override
		public Substitution next() {
			hasNext();
			Substitution res = this.next;
			this.next = null;
			return res;
		}

		protected Substitution computeNext() {
			if (this.rawReader.hasNext()) {
				Substitution res = this.rawReader.next();
				if (check(res)) {
					return res;
				}
				else {
					return computeNext();
				}
			}
			else {
				return null;
			}
		}

		protected boolean check(Substitution s) {
			for (Atom a : builtInAtoms) {
				Atom a2 = s.createImageOf(a);
				if (!((BuiltInPredicate)a2.getPredicate()).evaluate(a2.getTerms().toArray(new Term[a2.getTerms().size()]))) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void close() {
			this.rawReader.close();
		}

		private Substitution next;
		private CloseableIterator<Substitution> rawReader;

	}

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	};
};

