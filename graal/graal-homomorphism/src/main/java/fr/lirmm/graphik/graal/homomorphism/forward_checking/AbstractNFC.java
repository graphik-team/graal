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
package fr.lirmm.graphik.graal.homomorphism.forward_checking;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.homomorphism.BacktrackUtils;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismIteratorChecker;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.util.AbstractProfilable;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractNFC extends AbstractProfilable implements ForwardChecking {

	protected VarData[] data;

	@Override
	public void init(Var[] vars, Map<Variable, Var> map) {
		this.data = new VarData[vars.length];

		for (int i = 0; i < vars.length; ++i) {
			this.data[vars[i].level] = new VarData();
			this.data[vars[i].level].candidats = new TreeMap<Var, AcceptableCandidats>();
			this.data[vars[i].level].tmp = new TreeSet<Term>();
			this.data[vars[i].level].toCheckAfterAssignment = new LinkedList<Atom>();

			for (Atom a : vars[i].preAtoms) {
				int cpt = 0;
				boolean toAdd = true;
				for (Term t : a.getTerms()) {
					if (!t.isConstant()) {
						if (t.equals(vars[i].value))
							++cpt;
						else
							toAdd = false;
					}
				}
				if (cpt > 1)
					toAdd = true;

				if (toAdd) {
					this.data[vars[i].level].toCheckAfterAssignment.add(a);
				}
			}

			AcceptableCandidats previous = new AcceptableCandidats();
			for (Var z : vars[i].preVars) {
				AcceptableCandidats ac = new AcceptableCandidats();
				ac.candidats = new TreeSet<Term>();
				ac.previous = previous;
				previous = ac;

				this.data[vars[i].level].candidats.put(z, ac);
			}
			this.data[vars[i].level].last = previous;
		}
	}

	@Override
	public boolean isInit(Var v) {
		return this.data[v.level].last.init;
	}

	@Override
	public CloseableIterator<Term> getCandidatsIterator(AtomSet g, Var var, Map<Variable, Var> map, RulesCompilation rc)
	    throws AtomSetException {
		HomomorphismIteratorChecker tmp;
		if (this.data[var.level].last.init) {
			tmp = new HomomorphismIteratorChecker(
			                                      var,
			                                      new CloseableIteratorAdapter<Term>(
			                                                                         this.data[var.level].last.candidats.iterator()),
			                                      this.data[var.level].toCheckAfterAssignment, g, map, rc);
		} else {
			tmp = new HomomorphismIteratorChecker(var, new CloseableIteratorAdapter<Term>(g.termsIterator()),
			                                      var.preAtoms, g, map, rc);
		}
		tmp.setProfiler(this.getProfiler());
		return tmp;
	}

	protected boolean check(Atom atom, Var currentVar, Var varToCompute, AtomSet g, Map<Variable, Var> map,
	    RulesCompilation rc)
	    throws AtomSetException {
		Substitution s = BacktrackUtils.createSubstitution(map.values().iterator());
		Atom im = s.createImageOf(atom);
		this.data[varToCompute.level].tmp.clear();
		
		// FIXME bug with p(X,Y,Z) -> q(X,Y) in the compilation
		boolean contains = false;
		for (Atom a : rc.getRewritingOf(im)) {
			Set<Term> candidats = this.data[varToCompute.level].candidats.get(currentVar).candidats;
			Iterator<Term> it = candidats.iterator();
			while (it.hasNext()) {
				Term t = it.next();
				Atom fullInstantiatedAtom = Substitutions.createImageOf(a, varToCompute.value, t);
				Profiler profiler = this.getProfiler();
				if (profiler != null) {
					profiler.incr("#check", 1);
					profiler.start("checkTime");
				}
				if (g.contains(fullInstantiatedAtom)) {
					this.data[varToCompute.level].tmp.add(t);
				}
				if (profiler != null) {
					profiler.stop("checkTime");
				}
			}
			if (!candidats.isEmpty()) {
				contains = true;
			}
		}
		
		this.data[varToCompute.level].candidats.get(currentVar).candidats.retainAll(this.data[varToCompute.level].tmp);
		this.data[varToCompute.level].tmp.clear();
		
		return contains;
	}

	protected boolean select(Atom atom, Var v, AtomSet g, Map<Variable, Var> map, RulesCompilation rc)
	    throws AtomSetException {
		boolean contains = false;
		Set<Var> postVarsFromThisAtom = new TreeSet<Var>();

		for (Atom a : rc.getRewritingOf(atom)) {

			Var postV[] = this.computePostVariablesPosition(a, v, map, postVarsFromThisAtom);
			Atom im = BacktrackUtils.createImageOf(a, map);

			Profiler profiler = this.getProfiler();
			if (profiler != null) {
				profiler.incr("#Select", 1);
				profiler.start("SelectTime");
			}
			int nbAns = 0;
			Iterator<? extends Atom> it = g.match(im);
			while (it.hasNext()) {
				++nbAns;
				int i = -1;
				for (Term t : it.next()) {
					++i;
					if (postV[i] != null) {
						this.data[postV[i].level].tmp.add(t);
					}
				}
				contains = true;
			}
			if (profiler != null) {
				profiler.stop("SelectTime");
				profiler.incr("#SelectAns", nbAns);
			}
		}

		boolean isThereAnEmptiedList = false;
		if (contains) {
			// set computed candidats for post variables
			for (Var z : postVarsFromThisAtom) {
				if (!isThereAnEmptiedList) {
					AcceptableCandidats ac = this.data[z.level].candidats.get(v);
					if (ac.init) {
						ac.candidats.retainAll(this.data[z.level].tmp);
						isThereAnEmptiedList |= ac.candidats.isEmpty();
					} else {
						ac.candidats.addAll(this.data[z.level].tmp);
						ac.init = true;
					}
				}
				this.data[z.level].tmp.clear();
			}
		}

		return contains && !isThereAnEmptiedList;
	}

	protected Var[] computePostVariablesPosition(Atom a, Var v, Map<Variable, Var> map, Set<Var> postVarsFromThisAtom) {
		Var postV[] = new Var[a.getPredicate().getArity()];
		int i = -1;
		for (Term t : a) {
			++i;
			Var z = map.get(t);
			if (!t.isConstant() && z.level > v.level) {
				postV[i] = z;
				postVarsFromThisAtom.add(z);
			}
		}
		return postV;
	}

	protected void clear(Var v, Var z) {
		AcceptableCandidats ac = this.data[z.level].candidats.get(v);
		ac.candidats.clear();
		ac.init = false;
		if (ac.previous.init) {
			ac.candidats.addAll(ac.previous.candidats);
			ac.init = true;
		}
	}

	protected class VarData {
		Map<Var, AcceptableCandidats> candidats;
		AcceptableCandidats           last;
		Set<Term>                     tmp;
		Collection<Atom>              toCheckAfterAssignment;
	}

	protected class AcceptableCandidats {
		Set<Term>           candidats;
		AcceptableCandidats previous;
		Boolean             init = false;
	}

}
