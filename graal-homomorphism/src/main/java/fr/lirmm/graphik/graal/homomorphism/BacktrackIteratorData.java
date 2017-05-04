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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections4.SetUtils;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.homomorphism.backjumping.BackJumping;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.Bootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.ForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.scheduler.PatternScheduler;
import fr.lirmm.graphik.graal.homomorphism.scheduler.Scheduler;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterableWithoutException;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

class BacktrackIteratorData {
	Scheduler scheduler;
	Bootstrapper bootstrapper;
	ForwardChecking fc;
	BackJumping bj;

	InMemoryAtomSet query;
	AtomSet data;

	RulesCompilation compilation;
	Var[] varsOrder;
	Map<Variable, Var> index;

	int levelMax;
	List<Term> ans;
	Collection<InMemoryAtomSet> negParts;
	 Profiler profiler;

	public BacktrackIteratorData(InMemoryAtomSet query, Set<Variable> variablesToParameterize,
			Collection<InMemoryAtomSet> negParts, AtomSet data, List<Term> ans, PatternScheduler scheduler,
			Bootstrapper bootstrapper, ForwardChecking fc, BackJumping bj, RulesCompilation compilation,
			Profiler profiler) throws HomomorphismException {

		this.query = query;
		this.negParts = negParts;
		this.data = data;
		this.ans = ans;
		this.scheduler = scheduler;
		this.bootstrapper = bootstrapper;
		this.fc = fc;
		this.bj = bj;
		this.fc.setBackJumping(bj);
		this.compilation = compilation;

		this.profiler = profiler;
		this.bootstrapper.setProfiler(profiler);
		this.scheduler.setProfiler(profiler);
		this.fc.setProfiler(profiler);
		this.bj.setProfiler(profiler);

		this.preprocessing(variablesToParameterize, profiler);
	}

	public BacktrackIteratorData(InMemoryAtomSet query, Collection<InMemoryAtomSet> negParts, AtomSet data,
			List<Term> ans, Scheduler scheduler, Bootstrapper bootstrapper, ForwardChecking fc, BackJumping bj,
			RulesCompilation compilation, Profiler profiler) throws HomomorphismException {

		this.query = query;
		this.negParts = negParts;
		this.data = data;
		this.ans = ans;
		this.scheduler = scheduler;
		this.bootstrapper = bootstrapper;
		this.fc = fc;
		this.bj = bj;
		this.fc.setBackJumping(bj);
		this.compilation = compilation;

		this.profiler = profiler;
		this.bootstrapper.setProfiler(profiler);
		this.scheduler.setProfiler(profiler);
		this.fc.setProfiler(profiler);
		this.bj.setProfiler(profiler);

		this.preprocessing(null, profiler);
	}

	/**
	 * copy constructor
	 * 
	 * @param data
	 */
	public BacktrackIteratorData(BacktrackIteratorData data) {

		this.query = data.query;
		this.negParts = data.negParts;
		this.data = data.data;
		this.ans = data.ans;
		this.scheduler = data.scheduler;
		this.bootstrapper = data.bootstrapper;
		this.fc = data.fc;
		this.bj = data.bj;
		this.compilation = data.compilation;

		this.levelMax = data.levelMax;
		this.index = data.index;

		this.varsOrder = copy(data.varsOrder);
		
		this.profiler = data.profiler;
	}

	private Var[] copy(Var[] toCopy) {
		Var[] varsOrder = new Var[toCopy.length];
		for (int i = 0; i < toCopy.length; ++i) {
			varsOrder[i] = new Var(toCopy[i]);
		}
		return varsOrder;
	}

	private void preprocessing(Set<Variable> variablesToParameterize, Profiler profiler) throws HomomorphismException {
		profiler.start("preprocessingTime");
		// Compute order on query variables and atoms
		if (variablesToParameterize != null && !variablesToParameterize.isEmpty()) {
			assert this.scheduler instanceof PatternScheduler;
			PatternScheduler sched = (PatternScheduler) this.scheduler;
			this.varsOrder = sched.execute(this.query, variablesToParameterize, ans, this.data, this.compilation);
		} else {
			this.varsOrder = this.scheduler.execute(this.query, ans, this.data, this.compilation);
		}
		this.levelMax = varsOrder.length - 2;

		// PROFILING
		if (profiler.isProfilingEnabled()) {
			this.profilingVarOrder(this.varsOrder, profiler);
		}

		// Index Var structures by original variable object
		this.index = new TreeMap<Variable, Var>();
		for (Var v : this.varsOrder) {
			if (v.value != null) { //
				this.index.put(v.value, v);
			}
		}

		if (this.ans.isEmpty()) {
			this.varsOrder[this.levelMax + 1].previousLevel = -1;
		}

		computeAtomOrder(this.query, this.varsOrder, this.index);
		this.fc.init(this.varsOrder, this.index);
		this.bj.init(this.varsOrder, this.index);

		Set<Variable> allVarsFromH = query.getVariables();
		for (InMemoryAtomSet negPart : this.negParts) {
			Set<Variable> frontier = SetUtils.intersection(allVarsFromH, negPart.getVariables());
			this.varsOrder[maxLevel(frontier)].negatedPartsToCheck.add(BacktrackHomomorphismPattern.instance()
					.prepareHomomorphism(new DefaultConjunctiveQuery(negPart, Collections.<Term>emptyList()), frontier,
							this.data, this.compilation));
		}
		profiler.stop("preprocessingTime");

	}

	/**
	 * The index 0 contains the fully instantiated atoms.
	 * 
	 * @param atomset
	 * @param vars
	 */
	private static void computeAtomOrder(CloseableIterableWithoutException<Atom> atomset, Var[] vars,
			Map<Variable, Var> index) {
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
			for (Variable t : a.getVariables()) {
				Var v = index.get(t);
				if (v != null) {
					tmp = v.level;
					vars[tmp].postAtoms.add(a);

					if (rank < tmp)
						rank = tmp;
				}
			}
			vars[rank].postAtoms.remove(a);
			vars[rank].preAtoms.add(a);
		}

		for (int i = 0; i < vars.length; ++i) {
			for (Atom a : vars[i].postAtoms) {
				for (Variable t : a.getVariables()) {
					Var v = index.get(t);
					if (v != null && v.level > i) {
						vars[i].postVars.add(v);
						v.preVars.add(vars[i]);
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param frontier
	 * @return the biggest level of specified variables
	 */
	private int maxLevel(Set<Variable> frontier) {
		int max = 0;
		for (Variable v : frontier) {
			Var var = this.index.get(v);
			if (var.level > max) {
				max = var.level;
			}
		}
		return max;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROFLING METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void profilingVarOrder(Var[] varsOrder, Profiler profiler) {
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < varsOrder.length - 1; ++i) {
			sb.append(varsOrder[i].value.toString());
			sb.append(' ');
		}
		profiler.incr("__#cqs", 1);
		profiler.put("SchedulingSubQuery" + profiler.get("__#cqs"), sb.toString());
	}

}