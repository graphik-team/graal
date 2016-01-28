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
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.homomorphism.BacktrackUtils;
import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;

/**
 * NFC2 is a ForwardChecking implementation for HyperGraph with immediate local
 * propagation in one step. It maintain a list of possible candidats for each
 * variables.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class NFC2 implements ForwardChecking {

	/**
	 * A data extension for variable indexed by level
	 */
	protected VarData[] data;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void init(Var[] vars, Map<Variable, Var> map) {
		this.data = new VarData[vars.length];

		for (int i = 0; i < vars.length; ++i) {
			vars[i].forwardNeighbors = new TreeSet<Var>();
			this.data[vars[i].level] = new VarData();
			this.data[vars[i].level].candidats = new Set[vars[i].level];
			this.data[vars[i].level].tmp = new TreeSet<Term>();
			this.data[vars[i].level].toCheckAfterAssignment = new LinkedList<Atom>();

			for (Atom a : vars[i].postAtoms) {
				for (Term t : a.getTerms(Type.VARIABLE)) {
					Var v = map.get((Variable) t);
					if (v.level > i) {
						vars[i].forwardNeighbors.add(v);
					}
				}
			}

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
		}

	}

	@Override
	public boolean checkForward(Var v, AtomSet g, Map<Variable, Var> map, RulesCompilation rc) throws AtomSetException {

		for (Var z : v.forwardNeighbors) {
			this.data[z.level].tmp.clear();

			if (this.data[z.level].candidats[v.level - 1] != null) {
				this.data[z.level].candidats[v.level] = new TreeSet<Term>(this.data[z.level].candidats[v.level - 1]);
			} else {
				this.data[z.level].candidats[v.level] = null;
			}
		}

		boolean contains;
		for (Atom atom : v.postAtoms) {
			contains = false;
			Set<Var> forwardNeighborsInThisAtom = new TreeSet<Var>();

			for (Atom a : rc.getRewritingOf(atom)) {

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
				while (it.hasNext()) {
					i = -1;
					for (Term t : it.next()) {
						++i;
						if (postV[i] != null) {
							this.data[postV[i].level].tmp.add(t);
						}
					}
					contains = true;
				}
			}

			if (contains) {
				for (Var z : forwardNeighborsInThisAtom) {
					if (this.data[z.level].candidats[v.level] == null) {
						this.data[z.level].candidats[v.level] = new TreeSet<Term>(this.data[z.level].tmp);
					} else {
						this.data[z.level].candidats[v.level].retainAll(this.data[z.level].tmp);
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
			return new HomomorphismIteratorChecker(
			                                       var,
			                                       new CloseableIteratorAdapter<Term>(
			                                                                          this.data[var.level].candidats[var.level - 1].iterator()),
			                                       this.data[var.level].toCheckAfterAssignment, g, map, rc);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected class VarData {
		Set<Term>[]      candidats;
		Set<Term>        tmp;
		Collection<Atom> toCheckAfterAssignment;
	}
}
