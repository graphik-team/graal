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

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.homomorphism.BacktrackException;
import fr.lirmm.graphik.graal.homomorphism.BacktrackUtils;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismIteratorChecker;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.graal.homomorphism.backjumping.BackJumping;
import fr.lirmm.graphik.util.AbstractProfilable;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractNFC extends AbstractProfilable implements ForwardChecking {

	protected VarData[] data;
	protected BackJumping bj;
	
	@Override
	public void setBackJumping(BackJumping bj) {
		this.bj = bj;
	}

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
	    throws BacktrackException {
		HomomorphismIteratorChecker tmp;
		if (this.data[var.level].last.init) {
			tmp = new HomomorphismIteratorChecker(
			                                      var,
			                                      new CloseableIteratorAdapter<Term>(
			                                                                         this.data[var.level].last.candidats.iterator()),
			                                      this.data[var.level].toCheckAfterAssignment, g, map, rc);
		} else {
			try {
				tmp = new HomomorphismIteratorChecker(var, g.termsIterator(), var.preAtoms, g, map, rc);
			} catch (AtomSetException e) {
				throw new BacktrackException(e);
			}
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
		Set<Term> candidats = this.data[varToCompute.level].candidats.get(currentVar).candidats;
		
		// FIXME bug with p(X,Y,Z) -> q(X,Y) in the compilation
		for (Pair<Atom, Substitution> rew : rc.getRewritingOf(im)) {
			Atom a = rew.getLeft();
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
		}
		
		candidats.retainAll(this.data[varToCompute.level].tmp);
		this.data[varToCompute.level].tmp.clear();
		if(candidats.isEmpty()) {
			this.bj.addNeighborhoodToBackjumpSet(varToCompute, currentVar);
			return false;
		} else {
			return true;
		}
	}

	protected boolean select(Atom atom, Var v, AtomSet g, Map<Variable, Var> map, RulesCompilation rc)
	    throws AtomSetException, IteratorException {
		boolean contains = false;
		Set<Var> postVarsFromThisAtom = new TreeSet<Var>();

		for (Pair<Atom, Substitution> rew : rc.getRewritingOf(atom)) {
			Atom a = rew.getLeft();
			Var postV[] = this.computePostVariablesPosition(a, v.level, map, postVarsFromThisAtom);
			Atom im = BacktrackUtils.createImageOf(a, map);

			Profiler profiler = this.getProfiler();
			if (profiler != null) {
				profiler.incr("#Select", 1);
				profiler.start("SelectTime");
			}
			int nbAns = 0;
			CloseableIterator<? extends Atom> it = g.match(im);
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
						if(ac.candidats.isEmpty()) {
							this.bj.addNeighborhoodToBackjumpSet(z, v);
						}
					} else {
						ac.candidats.addAll(this.data[z.level].tmp);
						ac.init = true;
					}
				}
				this.data[z.level].tmp.clear();
			}
		} else {
			Var z = postVarsFromThisAtom.iterator().next();
			this.bj.addNeighborhoodToBackjumpSet(z, v);
		}

		return contains && !isThereAnEmptiedList;
	}

	/**
	 * Return an array containing the corresponding instance of Var class for
	 * each position of a variable in the specified atom with a higher level
	 * than the specified level. Constant, literal and lower or equals level
	 * variable postions contain null value.
	 * 
	 * @param atom
	 * @param level
	 * @param map
	 *            Correspondence between Variable instance and Var instance.
	 * @param postVars
	 *            output parameter that is a Set in which must be added higher
	 *            level variables from this atom.
	 * @return
	 */
	protected Var[] computePostVariablesPosition(Atom atom, int level, Map<Variable, Var> map,
	    Set<Var> postVars) {
		Var postV[] = new Var[atom.getPredicate().getArity()];
		int i = -1;
		for (Term t : atom) {
			++i;
			Var z = map.get(t);
			if (!t.isConstant() && z.level > level) {
				postV[i] = z;
				postVars.add(z);
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
		
		public String toString() {
			if(init) {
				return candidats.toString();
			} else {
				return "";
			}
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int level=0; level < this.data.length; ++level) {
			sb.append(level+": ");
			this.append(sb, level);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	@Override
	public StringBuilder append(StringBuilder sb, int level) {
		for(Map.Entry<Var, AcceptableCandidats> e : this.data[level].candidats.entrySet()) {
			sb.append(e.getKey().value +"=" + e.getValue().toString()+", ");
		}
		return sb;
	}
	

}
