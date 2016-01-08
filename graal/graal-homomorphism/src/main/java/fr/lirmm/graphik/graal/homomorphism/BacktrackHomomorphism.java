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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.ForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NoFC;
import fr.lirmm.graphik.util.Profilable;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * This Backtrack is inspired by the Baget Jean-François Thesis (Chapter 5)
 *
 * see also "Backtracking Through Biconnected Components of a Constraint Graph"
 * (Jean-François Baget, Yannic S. Tognetti IJCAI 2001)
 *
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BacktrackHomomorphism implements HomomorphismWithCompilation<ConjunctiveQuery, AtomSet>, Profilable {

	private Profiler profiler = null;
	private Scheduler scheduler;
	private ForwardChecking fc;

	public BacktrackHomomorphism() {
		this(new DefaultScheduler(), new NoFC());
	}

	public BacktrackHomomorphism(Scheduler s) {
		this(s, new NoFC());
	}

	public BacktrackHomomorphism(ForwardChecking fc) {
		this(new DefaultScheduler(), fc);
	}

	public BacktrackHomomorphism(Scheduler s, ForwardChecking fc) {
		super();
		this.fc = fc;
		this.scheduler = s;
	}

	// /////////////////////////////////////////////////////////////////////////
	// HOMOMORPHISM METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public <U1 extends ConjunctiveQuery, U2 extends AtomSet> CloseableIterator<Substitution> execute(U1 q, U2 a)
	    throws HomomorphismException {
		return this.execute(q, a, NoCompilation.instance());
	}

	@Override
	public <U1 extends ConjunctiveQuery, U2 extends AtomSet> CloseableIterator<Substitution> execute(U1 q, U2 a,
	    RulesCompilation compilation) throws HomomorphismException {
		// return new
		// ArrayBlockingQueueToCloseableIteratorAdapter<Substitution>(new
		// BT(q.getAtomSet(), a,
		// q.getAnswerVariables(),
		// compilation));
		BacktrackIterator backtrackIterator = new BacktrackIterator(q.getAtomSet(), a, q.getAnswerVariables(),
		                                                            this.scheduler, this.fc, compilation);
		if (this.profiler != null) {
			backtrackIterator.setProfiler(profiler);
		}
		return backtrackIterator;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROFILABLE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}

	// /////////////////////////////////////////////////////////////////////////
	// INTERN CLASSES
	// /////////////////////////////////////////////////////////////////////////



	/**
	 * The Scheduler interface provides a way to manage the backtracking order.
	 * The Var.previousLevel will be used when the backtracking algorithm is in
	 * a failure state (allow backjumping).
	 *
	 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
	 *
	 */
	public static interface Scheduler {

		/**
		 * @param h
		 * @param ans
		 * @return an array of Var
		 */
		Var[] execute(InMemoryAtomSet h, List<Term> ans);

		/**
		 * @param currentVar
		 * @param vars
		 * @return the previous level
		 */
		int previousLevel(Var currentVar, Var[] vars);

		/**
		 * @param currentVar
		 * @param vars
		 * @return the next level
		 */
		int nextLevel(Var currentVar, Var[] vars);

		/**
		 * @param var
		 * @param image
		 * @return true if the specified image is not forbidden for the
		 *         specified var
		 */
		boolean isAllowed(Var var, Term image);

	}

	/**
	 * Compute an order over variables from h. This scheduler put answer
	 * variables first, then other variables are put in the order from
	 * h.getTerms(Term.Type.VARIABLE).iterator().
	 *
	 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
	 *
	 */
	static class DefaultScheduler implements Scheduler {

		private static DefaultScheduler instance;

		protected DefaultScheduler() {
			super();
		}

		public static synchronized DefaultScheduler instance() {
			if (instance == null)
				instance = new DefaultScheduler();

			return instance;
		}

		/**
		 * Compute the order.
		 *
		 * @param h
		 * @return
		 */
		@Override
		public Var[] execute(InMemoryAtomSet h, List<Term> ans) {
			Set<Term> terms = h.getTerms(Term.Type.VARIABLE);
			Var[] vars = new Var[terms.size() + 2];

			int level = 0;
			vars[level] = new Var(level);

			Set<Term> alreadyAffected = new TreeSet<Term>();
			for (Term t : ans) {
				if (t instanceof Variable && !alreadyAffected.contains(t)) {
					++level;
					vars[level] = new Var(level);
					vars[level].value = (Variable) t;
					alreadyAffected.add(t);
				}
			}

			int lastAnswerVariable = level;

			for (Term t : terms) {
				if (!alreadyAffected.contains(t)) {
					++level;
					vars[level] = new Var(level);
					vars[level].value = (Variable) t;
				}
			}

			++level;
			vars[level] = new Var(level);
			vars[level].previousLevel = lastAnswerVariable;

			return vars;
		}

		@Override
		public int previousLevel(Var currentVar, Var[] vars) {
			return currentVar.previousLevel;
		}

		@Override
		public int nextLevel(Var currentVar, Var[] vars) {
			return currentVar.nextLevel;
		}

		@Override
		public boolean isAllowed(Var var, Term image) {
			return true;
		}

	}

}
