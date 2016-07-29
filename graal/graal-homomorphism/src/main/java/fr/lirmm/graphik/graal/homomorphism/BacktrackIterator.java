/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.backjumping.BackJumping;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.Bootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.ForwardChecking;
import fr.lirmm.graphik.util.NoProfiler;
import fr.lirmm.graphik.util.Profilable;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterableWithoutException;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class BacktrackIterator extends AbstractCloseableIterator<Substitution>
                        implements CloseableIterator<Substitution>, Profilable {

	private Scheduler scheduler;
	private Bootstrapper bootstrapper;
	private ForwardChecking fc;
	private BackJumping bj;

	private InMemoryAtomSet h;
	private AtomSet g;
	private RulesCompilation compilation;
	private Substitution next = null;

	private Var[] vars;
	private Map<Variable, Var> index;
	private Var currentVar;

	private int levelMax;
	private int level;
	private boolean goBack;
	private List<Term> ans;

	private Profiler profiler;

	private int nbCall = 0;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public BacktrackIterator(InMemoryAtomSet h, AtomSet g, List<Term> ans, Scheduler scheduler,
	    Bootstrapper boostrapper, ForwardChecking fc, BackJumping bj, RulesCompilation compilation, Profiler profiler) {

		this.h = h;
		this.g = g;
		this.ans = ans;
		this.scheduler = scheduler;
		this.bootstrapper = boostrapper;
		this.fc = fc;
		this.bj = bj;
		this.compilation = compilation;

		this.currentVar = null;
		this.level = 0;
		this.goBack = false;

		this.profiler = profiler;
		this.bootstrapper.setProfiler(profiler);
		this.scheduler.setProfiler(profiler);
		this.fc.setProfiler(profiler);
		this.bj.setProfiler(profiler);

		this.preprocessing();
	}

	/**
	 * Look for an homomorphism of h into g.
	 * 
	 * @param h
	 * @param g
	 */
	public BacktrackIterator(InMemoryAtomSet h, AtomSet g, List<Term> ans, Scheduler scheduler,
	    Bootstrapper boostrapper, ForwardChecking fc, BackJumping bj, RulesCompilation compilation) {
		this(h, g, ans, scheduler, boostrapper, fc, bj, compilation, NoProfiler.instance());
	}

	private void preprocessing() {
		profiler.start("preprocessingTime");

		// Compute order on query variables and atoms
		vars = scheduler.execute(this.h, ans, this.g, this.compilation);
		levelMax = vars.length - 2;
		if (this.profiler.isProfilingEnabled()) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < vars.length - 1; ++i) {
				sb.append(vars[i].value.toString());
				sb.append(' ');
			}
			this.profiler.incr("__#cqs", 1);
			this.profiler.put("SchedulingSubQuery" + this.profiler.get("__#cqs"), sb.toString());
		}

		index = new TreeMap<Variable, Var>();
		for (Var v : vars) {
			if (v.value != null)
				this.index.put(v.value, v);
		}

		if (ans.isEmpty()) {
			vars[levelMax + 1].previousLevel = -1;
		}

		computeAtomOrder(h, vars);
		fc.init(vars, index);
		bj.init(vars, index);

		profiler.stop("preprocessingTime");

	}

	// /////////////////////////////////////////////////////////////////////////
	// CLOSEABLE ITERATOR METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() throws IteratorException {
		if (this.next == null) {
			try {
				this.next = computeNext();
			} catch (BacktrackException e) {
				this.next = null;
				throw new IteratorException("An errors occurs during backtrack iteration", e);
			}
		}
		return this.next != null;
	}

	@Override
	public Substitution next() throws IteratorException {
		Substitution tmp = null;
		if (this.hasNext()) {
			tmp = this.next;
			this.next = null;
		}
		return tmp;
	}

	@Override
	public void close() {
		for (int i = 1; i < vars.length; ++i) {
			if (vars[i].domain != null) {
				this.vars[i].domain.close();
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROFILABLE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void setProfiler(Profiler profiler) {
		if (profiler == null) {
			profiler = NoProfiler.instance();
		}
		this.profiler = profiler;
		this.bootstrapper.setProfiler(profiler);
		this.scheduler.setProfiler(profiler);
		this.fc.setProfiler(profiler);
		this.bj.setProfiler(profiler);
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n").append("\t{query->").append(h).append("},\n\t{level->").append(level).append("},\n\t{");
		int i = 0;
		for (Var v : vars) {
			if (i == level)
				sb.append('*');
			String s = v.toString();
			sb.append(s.substring(0, s.length() - 1)).append("->").append(v.image).append("\n");
			++i;
		}
		sb.append("}\n}\n");

		return sb.toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * level -1 : no more answers level 0 : not initialized
	 */
	private Substitution computeNext() throws BacktrackException {
		if (profiler != null) {
			profiler.start("backtrackingTime");
		}

		if (level >= 0) {
			try {
				if (level == 0) { // first call
					// check if the full instantiated atoms from the query are
					// in the data
					if (BacktrackUtils.isHomomorphism(vars[level].preAtoms, g, this.index, this.compilation)) {
						++level;
						if (level > levelMax) { // there is no variable
							level = -1;
							if (profiler != null) {
								profiler.stop("backtrackingTime");
							}
							return solutionFound(vars, ans);
						}
					} else {
						--level;
					}
				}

				while (level > 0) {
					++nbCall;
					currentVar = vars[level];

					// Homomorphism found
					if (level > levelMax) {
						goBack = true;

						Substitution sol = solutionFound(vars, ans);

						int nextLevel = currentVar.previousLevel;
						for (; level > nextLevel; --level) {
							vars[level].image = null;
						}
						if (profiler != null) {
							profiler.stop("backtrackingTime");
						}
						return sol;
					}

					//
					if (goBack) {
						if (hasMoreValues(currentVar, g)) {
							goBack = false;
							++level;
						} else {
							int nextLevel = bj.previousLevel(currentVar, vars);
							for (; level > nextLevel; --level) {
								vars[level].image = null;
							}
						}
					} else {
						if (getFirstValue(currentVar, g)) {
							++level;
						} else {
							goBack = true;
							int nextLevel = bj.previousLevel(currentVar, vars);
							for (; level > nextLevel; --level) {
								vars[level].image = null;
							}
						}
					}
				}
			} catch (AtomSetException e) {
				throw new BacktrackException("Exception during backtracking", e);
			}
			--level;
		}
		if (profiler != null) {
			profiler.stop("backtrackingTime");
			profiler.put("#calls", nbCall);
		}

		return null;
	}

	private Substitution solutionFound(Var[] vars, List<Term> ans) {
		Substitution s = new TreeMapSubstitution();
		for (Term t : ans) {
			if (t instanceof Variable) {
				Var v = this.index.get((Variable) t);
				s.put(v.value, v.image);
			} else {
				s.put(t, t);
			}
		}

		for (int i = 0; i < vars.length - 1; ++i) {
			vars[i].success = true;
		}

		return s;
	}

	private boolean getFirstValue(Var var, AtomSet g) throws BacktrackException {
		if (fc.isInit(var)) {
			var.domain = fc.getCandidatsIterator(g, var, this.index, this.compilation);
		} else {
			var.domain = new HomomorphismIteratorChecker(var, bootstrapper.exec(var, h, g, this.compilation),
			                                             var.preAtoms, g, this.index, this.compilation);
		}
		return this.hasMoreValues(var, g);
	}

	private boolean hasMoreValues(Var var, AtomSet g) throws BacktrackException {
		try {
			while (var.domain.hasNext()) {
				// TODO explicit var.success
				var.success = false;
				var.image = var.domain.next();

				// Fix for existential variable in data
				if (!var.image.isConstant()) {
					var.image = DefaultTermFactory.instance().createConstant(var.image.getLabel());
				}

				if (scheduler.isAllowed(var, var.image)) {
					if (fc.checkForward(var, g, this.index, this.compilation)) {
						return true;
					}
				}
			}
		} catch (IteratorException e) {
			throw new BacktrackException("An exception occurs during data iteration", e);
		}
		var.domain.close();
		return false;
	}

	/**
	 * The index 0 contains the fully instantiated atoms.
	 * 
	 * @param atomset
	 * @param varsOrdered
	 * @return
	 */
	private void computeAtomOrder(CloseableIterableWithoutException<Atom> atomset, Var[] vars) {
		int tmp, rank;

		// initialisation preAtoms and postAtoms Collections
		for (int i = 0; i < vars.length; ++i) {
			vars[i].preAtoms = new TreeSet<Atom>();
			vars[i].postAtoms = new TreeSet<Atom>();
			vars[i].postVars = new TreeSet<Var>();
			vars[i].preVars = new TreeSet<Var>();
		}

		//
		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			rank = 0;
			for (Term t : a.getTerms(Type.VARIABLE)) {
				tmp = this.index.get((Variable) t).level;
				vars[tmp].postAtoms.add(a);

				if (rank < tmp)
					rank = tmp;
			}
			vars[rank].postAtoms.remove(a);
			vars[rank].preAtoms.add(a);
		}

		for (int i = 0; i < vars.length; ++i) {
			for (Atom a : vars[i].postAtoms) {
				for (Term t : a.getTerms(Type.VARIABLE)) {
					Var v = this.index.get((Variable) t);
					if (v.level > i) {
						vars[i].postVars.add(v);
						v.preVars.add(vars[i]);
					}
				}
			}
		}
	}

}
