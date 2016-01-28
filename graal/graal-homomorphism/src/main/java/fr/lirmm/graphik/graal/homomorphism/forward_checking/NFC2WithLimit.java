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
package fr.lirmm.graphik.graal.homomorphism.forward_checking;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.homomorphism.BacktrackUtils;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;

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
	private final int           LIMIT;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public NFC2WithLimit(int limit) {
		super();
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
	public boolean checkForward(Var v, AtomSet g, Map<Variable, Var> map, RulesCompilation rc) throws AtomSetException {

		for (Var z : v.forwardNeighbors) {
			super.data[z.level].tmp.clear();
			this.dataWithLimit[z.level].atomsToCheck.removeAll(v.postAtoms);

			if (super.data[z.level].candidats[v.level - 1] != null) {
				super.data[z.level].candidats[v.level] = new TreeSet<Term>(this.data[z.level].candidats[v.level - 1]);
			} else {
				super.data[z.level].candidats[v.level] = null;
			}
		}

		boolean contains;
		for (Atom atom : v.postAtoms) {
			contains = false;
			int cpt = 0;

			Iterator<Atom> rewIt = rc.getRewritingOf(atom).iterator();
			Set<Var> forwardNeighborsInThisAtom = new TreeSet<Var>();

			while (rewIt.hasNext() && cpt < LIMIT) {
				Atom a = rewIt.next();

				// Compute post variables positions
				Var postV[] = new Var[a.getPredicate().getArity()];
				int i = -1;
				for (Term t : a) {
					++i;
					Var z = map.get(t);
					if (!t.isConstant() && z.level > v.level) {
						postV[i] = z;
						forwardNeighborsInThisAtom.add(z);
					}
				}

				Atom im = BacktrackUtils.createImageOf(a, map);
				Iterator<? extends Atom> it = g.match(im);
				while (it.hasNext() && cpt < LIMIT) {
					i = -1;
					++cpt;
					for (Term t : it.next()) {
						++i;
						if (postV[i] != null) {
							super.data[postV[i].level].tmp.add(t);
						}
					}
					contains = true;
				}
			}

			if (contains) {
				for (Var z : forwardNeighborsInThisAtom) {
					if (cpt >= LIMIT) {
						if (z.preAtoms.contains(atom)) {
							this.dataWithLimit[z.level].atomsToCheck.add(atom);
						}
					} else {
						if (super.data[z.level].candidats[v.level] == null) {
							super.data[z.level].candidats[v.level] = new TreeSet<Term>(this.data[z.level].tmp);
						} else {
							super.data[z.level].candidats[v.level].retainAll(this.data[z.level].tmp);
						}
					}
				}
			} else {
				return false;
			}

		}

		return true;
	}

	@Override
	public CloseableIterator<Term> getCandidatsIterator(AtomSet g, Var var, Map<Variable, Var> map, RulesCompilation rc)
	    throws AtomSetException {
		if (this.data[var.level].candidats == null || this.data[var.level].candidats[var.level - 1] == null) {
			return new HomomorphismIteratorChecker(var, new CloseableIteratorAdapter<Term>(g.termsIterator()),
			                                       var.preAtoms, g, map, rc);
		} else {
			this.dataWithLimit[var.level].atomsToCheck.addAll(this.data[var.level].toCheckAfterAssignment);
			return new HomomorphismIteratorChecker(
			                                       var,
			                                       new CloseableIteratorAdapter<Term>(
			                                                                          this.data[var.level].candidats[var.level - 1].iterator()),
			                                       this.dataWithLimit[var.level].atomsToCheck, g, map, rc);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////

	protected class VarDataWithLimit {
		Collection<Atom> atomsToCheck;
	}

}
