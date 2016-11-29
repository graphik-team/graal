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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.homomorphism.BacktrackException;
import fr.lirmm.graphik.graal.homomorphism.BacktrackUtils;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismIteratorChecker;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * NFC2 is a ForwardChecking implementation for HyperGraph with immediate local
 * propagation in one step. It maintain a list of possible candidats for each
 * variables but only if the number of candidats in lower than a specified
 * limit.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class NFC2WithLimit extends NFC2 implements ForwardChecking {

	/**
	 * A data extension for variable indexed by level
	 */
	protected VarDataWithLimit[] dataWithLimit;
	private final int            LIMIT;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public NFC2WithLimit(int limit) {
		super(false);
		this.LIMIT = limit;
	}

	/**
	 * If enableCheckMode is true, NFC2 use AtomSet.contains(Atom) instead of
	 * AtomSet.match(Atom) when there is an initialized set of candidates for
	 * each variable.
	 * 
	 * @param enableCheckMode
	 */
	public NFC2WithLimit(int limit, boolean enableCheckMode) {
		super(enableCheckMode);
		this.LIMIT = limit;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void init(Var[] vars, java.util.Map<Variable,Var> map) {
		super.init(vars, map);
		
		this.dataWithLimit = new VarDataWithLimit[vars.length];

		for (int i = 0; i < vars.length; ++i) {
			this.dataWithLimit[vars[i].level] = new VarDataWithLimit();
			this.dataWithLimit[vars[i].level].atomsToCheck = new TreeSet<Atom>();
		}
	}

	@Override
	public CloseableIterator<Term> getCandidatsIterator(AtomSet g, Var var, Map<Variable, Var> map, RulesCompilation rc)
	    throws BacktrackException {
		HomomorphismIteratorChecker tmp;
		if (this.data[var.level].last.init) {
			this.dataWithLimit[var.level].atomsToCheck.addAll(this.data[var.level].toCheckAfterAssignment);
			tmp = new HomomorphismIteratorChecker(
			        var,
			        new CloseableIteratorAdapter<Term>(this.data[var.level].last.candidats.iterator()),
			        this.dataWithLimit[var.level].atomsToCheck, g, map, rc
			    );
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

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
    protected boolean select(Atom atom, Var v, AtomSet g, Map<Variable, Var> map, RulesCompilation rc)
	    throws AtomSetException, IteratorException {
		boolean contains = false;
		int nbAns = 0;
		Iterator<Pair<Atom, Substitution>> rewIt = rc.getRewritingOf(atom).iterator();
		Set<Var> postVarsFromThisAtom = new TreeSet<Var>();

		while (rewIt.hasNext() && nbAns < LIMIT) {
			Atom a = rewIt.next().getLeft();
			
			Var[] postV = this.computePostVariablesPosition(a, v.level, map, postVarsFromThisAtom);
			Atom im = BacktrackUtils.createImageOf(a, map);

			Profiler profiler = this.getProfiler();
			if (profiler != null) {
				profiler.incr("#Select", 1);
				profiler.start("SelectTime");
			}
			
			int cpt = 0;
			CloseableIterator<? extends Atom> it = g.match(im);
			while (it.hasNext() && nbAns < LIMIT) {
				++nbAns;
				++cpt;
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
				profiler.incr("#SelectAns", cpt);
			}
		}

		boolean isThereAnEmptiedList = false;
		if (contains) {
			// set computed candidats for post variables
			for (Var z : postVarsFromThisAtom) {
				if (!isThereAnEmptiedList) {
					if (nbAns >= LIMIT) {
						this.dataWithLimit[z.level].atomsToCheck.add(atom);
					} else {
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
				}
				
				this.data[z.level].tmp.clear();
			}
		} else {
			Var z = postVarsFromThisAtom.iterator().next();
			this.bj.addNeighborhoodToBackjumpSet(z, v);
		}
		
		return contains && !isThereAnEmptiedList;
	}

	@Override
	protected void clear(Var v, Var z) {
		super.clear(v, z);
		this.dataWithLimit[z.level].atomsToCheck.removeAll(v.postAtoms);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////

	protected class VarDataWithLimit {
		Collection<Atom> atomsToCheck;
	}
}
