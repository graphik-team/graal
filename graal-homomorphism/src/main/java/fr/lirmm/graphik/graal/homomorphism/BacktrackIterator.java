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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.PreparedExistentialHomomorphism;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.backjumping.BackJumping;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.Bootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.ForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.scheduler.Scheduler;
import fr.lirmm.graphik.graal.homomorphism.utils.BacktrackUtils;
import fr.lirmm.graphik.graal.homomorphism.utils.HomomorphismIteratorChecker;
import fr.lirmm.graphik.util.profiler.NoProfiler;
import fr.lirmm.graphik.util.profiler.Profilable;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class BacktrackIterator extends AbstractCloseableIterator<Substitution>
		implements CloseableIterator<Substitution>, Profilable {

	private BacktrackIteratorData data; 
	private Substitution initialSubstitution;
	private Substitution next = null;
	
	private Var vars[];
	
	private int level;
	private boolean goBack;

	private Profiler profiler;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public BacktrackIterator(BacktrackIteratorData data, Substitution initialSubstitution) throws HomomorphismException {
		
		this.data = data;
		synchronized (this.data) {
			if(this.data.isOpen) {
				throw new HomomorphismException("Prepared Homomorphism already in use");
			}
			this.data.isOpen = true;			
		}
		this.vars = new Var[this.data.varsOrder.length];
		for(int i = 0; i < vars.length; ++i) {
			this.vars[i] = new Var(this.data.varsOrder[i]);
		}
		
		for (int i = 0; i < this.data.varsOrder.length; ++i) {
			this.vars[i].preAtomsFixed = new LinkedList<Atom>();
			for(Atom a : this.data.varsOrder[i].preAtoms) {
				this.vars[i].preAtomsFixed.add(initialSubstitution.createImageOf(a));
			}
			this.vars[i].postAtomsFixed = new LinkedList<Atom>();
			for(Atom a : this.data.varsOrder[i].postAtoms) {
				this.vars[i].postAtomsFixed.add(initialSubstitution.createImageOf(a));
			}
		}
		this.profiler = data.profiler;
		this.initialSubstitution = initialSubstitution;
		
		this.level = 0;
		this.goBack = false;
	}

	public BacktrackIterator(InMemoryAtomSet query, Collection<InMemoryAtomSet> negParts, AtomSet data, List<Term> ans,
			Scheduler scheduler, Bootstrapper bootstrapper, ForwardChecking fc, BackJumping bj,
			RulesCompilation compilation, Substitution s, Profiler profiler) throws HomomorphismException {

		this(new BacktrackIteratorData(query, s.getTerms(), negParts, data, ans, scheduler, bootstrapper, fc, bj, compilation, profiler),
				s);
	}

	/**
	 * Look for an homomorphism of h into g.
	 * 
	 * @param h
	 * @param g
	 * @throws HomomorphismException
	 */
	public BacktrackIterator(InMemoryAtomSet h, Collection<InMemoryAtomSet> negParts, AtomSet g, List<Term> ans,
			Scheduler scheduler, Bootstrapper boostrapper, ForwardChecking fc, BackJumping bj,
			RulesCompilation compilation, Substitution s) throws HomomorphismException {
		this(h,negParts, g, ans, scheduler, boostrapper, fc, bj, compilation, s, NoProfiler.instance());
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
		for (int i = 1; i < this.data.varsOrder.length; ++i) {
			if (this.vars[i].domain != null) {
				this.vars[i].domain.close();
			}
		}
		this.data.isOpen = false;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * level < 0 : no more answers <br/>
	 * level 0 : not initialized
	 */
	private Substitution computeNext() throws BacktrackException {
		if (profiler != null) {
			profiler.start("backtrackingTime");
		}

		try {
			if (level == 0) { // first call
				// check if the full instantiated atoms from the query are in data
				if (BacktrackUtils.isHomomorphism(this.data.varsOrder[level].preAtoms, this.data.data, this.initialSubstitution, this.data.index, this.vars, this.data.compilation)) {
					if (this.existNegParts()) {
						this.data.bj.success();
						backtrack(false);
					} else {
						++level;
					}

				} else {
					--level;
				}
			}

			while (level > 0) {
				profiler.incr("#calls", 1);

				if (level > this.data.levelMax) { // Homomorphism found
					Substitution sol = solutionFound(this.data.ans);
					this.data.bj.success();
					backtrack(false);
					if (profiler != null) {
						profiler.stop("backtrackingTime");
					}
					return sol;
				} else {
					if (goBack) {
						if (hasMoreValues(currentVar(), this.data.data)) {
							goBack = false;
							++level;
						} else {
							backtrack(true);
						}
					} else {
						if (getFirstValue(currentVar(), this.data.data)) {
							++level;
						} else {
							backtrack(true);
						}
					}

				}

			}
		} catch (AtomSetException e) {
			throw new BacktrackException("Exception during backtracking", e);
		}
		--level;

		if (profiler != null) {
			profiler.stop("backtrackingTime");
		}

		return null;
	}

	/**
	 * 
	 * @param previousLevel
	 * @param failure
	 *            should be true iff we backtrack due to failure to find a new
	 *            value for the current variable. Should be false if we
	 *            backtrack due to a final success (we search an other
	 *            homomorphism).
	 */
	private void backtrack(boolean failure) {
		int previousLevel = (failure) ? this.data.bj.previousLevel(currentVar().shared, this.vars) : currentVar().shared.previousLevel;

		this.goBack = true;
		for (; level > previousLevel; --level) {
			this.vars[level].image = null;
		}
	}

	private boolean existNegParts() throws BacktrackException {
		Substitution s = currentSubstitution(this.vars);
		s.put(initialSubstitution);
		for (PreparedExistentialHomomorphism negPart : this.currentVar().shared.negatedPartsToCheck) {
			try {
				if (negPart.exist(s)) {
					this.data.bj.success();
					return true;
				}
			} catch (HomomorphismException e) {
				throw new BacktrackException("Error while checking anegated part: ", e);
			}
		}
		return false;
	}

	private Substitution solutionFound(List<Term> ans) {
		Substitution s = new HashMapSubstitution();
		for (Term t : ans) {
			if (t.isVariable()) {
				Integer idx = this.data.index.get((Variable) t);
				if (idx != null) {
					Var v = this.vars[idx];
					s.put(v.shared.value, v.image);
				}
			}
		}

		return s;
	}

	private Substitution currentSubstitution(Var[] vars) {
		Substitution s = new HashMapSubstitution();
		for (int i = 1; i <= this.level; ++i) {
			s.put(vars[i].shared.value, vars[i].image);
		}
		return s;
	}

	private boolean getFirstValue(Var var, AtomSet g) throws BacktrackException {
		if (this.data.fc.isInit(this.level)) {
			var.domain = this.data.fc.getCandidatsIterator(g, var, initialSubstitution, this.data.index, this.vars, this.data.compilation);
		} else {
			var.domain = new HomomorphismIteratorChecker(var, this.data.bootstrapper.exec(var.shared, var.preAtomsFixed, var.postAtomsFixed, g, this.data.compilation),
					var.shared.preAtoms, g, initialSubstitution, this.data.index, this.vars, this.data.compilation);
		}
		return this.hasMoreValues(var, g);
	}

	private boolean hasMoreValues(Var var, AtomSet g) throws BacktrackException {
		try {
			while (var.domain.hasNext()) {
				// TODO explicit var.success
				this.data.bj.level(var.shared.level);
				var.image = var.domain.next();

				// Fix for existential variable in data
				if (!var.image.isConstant()) {
					var.image = DefaultTermFactory.instance().createConstant(var.image.getLabel());
				}

				if (this.data.scheduler.isAllowed(var, var.image) && this.data.fc.checkForward(var, g, initialSubstitution, this.data.index, this.vars, this.data.compilation)
						&& !this.existNegParts()) {
					return true;
				}

			}
		} catch (IteratorException e) {
			throw new BacktrackException("An exception occurs during data iteration", e);
		}
		var.domain.close();
		return false;
	}

	private Var currentVar() {
		return this.vars[this.level];
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
		this.data.bootstrapper.setProfiler(profiler);
		this.data.scheduler.setProfiler(profiler);
		this.data.fc.setProfiler(profiler);
		this.data.bj.setProfiler(profiler);
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
		sb.append("{\n").append("\t{query: ").append(data.query);
		for (InMemoryAtomSet negPart : this.data.negParts) {
			sb.append("\u2227\u00AC").append(negPart);
		}
		sb.append("},\n\t{level: ").append(level).append("},\n\t{\n");
		int i = 0;
		for (Var v : this.vars) {
			sb.append("\t\t");
			sb.append((i == level) ? '*' : ' ');
			String s = v.toString();
			sb.append(s.substring(0, s.length() - 1)).append("->").append(v.image);
			sb.append(v.shared.negatedPartsToCheck.isEmpty() ? "   " : " \u00AC ");
			sb.append("\tFC{");
			this.data.fc.append(sb, i).append("}");
			this.data.bj.append(sb, i).append(" ");
			
			sb.append(this.data.scheduler.getInfos(v));
			sb.append("\n");
			
			++i;
		}
		sb.append("\t}\n}\n");

		return sb.toString();
	}



}
