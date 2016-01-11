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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * NFC2 is a ForwardChecking implementation for HyperGraph with immediate local
 * propagation in one step. It maintain a list of possible candidats for each
 * variables but only if the number of candidats in lower than a specified
 * limit.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class NFC2WithLimit implements ForwardChecking {

	private Map<Var, Set<Term>> candidats = new TreeMap<Var, Set<Term>>();
	private final int           LIMIT;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public NFC2WithLimit(int limit) {
		this.LIMIT = limit;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void init(Var[] vars, Map<Variable, Var> map) {
		for (int i = 0; i < vars.length; ++i) {
			vars[i].forwardNeighbors = new TreeSet<Var>();
			for (Atom a : vars[i].postAtoms) {
				for (Term t : a.getTerms(Type.VARIABLE)) {
					Var v = map.get((Variable) t);
					if (v.level > i) {
						vars[i].forwardNeighbors.add(v);
					}
				}
			}

			// vars[i].possibleImage = new List[vars[i].level + 1];
		}
	}

	@Override
	public boolean checkForward(Var v, AtomSet g, Map<Variable, Var> map, RulesCompilation rc) throws AtomSetException {

		candidats.clear();
		for (Var z : v.forwardNeighbors) {
			candidats.put(z, new TreeSet<Term>());
		}

		boolean contains;
		for (Atom atom : v.postAtoms) {
			contains = false;

			for (Atom a : rc.getRewritingOf(atom)) {

				// Compute post variables positions
				Var postV[] = new Var[a.getPredicate().getArity()];
				int i = -1;
				for (Term t : a) {
					++i;
					Var z = map.get(t);
					if (!t.isConstant() && z.level > v.level) {
						postV[i] = z;
					}
				}

				Atom im = BacktrackUtils.createImageOf(a, map);
				Iterator<? extends Atom> it = g.match(im);
				while (it.hasNext()) {
					i = -1;
					for (Term t : it.next()) {
						++i;
						if (postV[i] != null) {
							candidats.get(postV[i]).add(t);
						}
					}
					contains = true;
				}
			}

			if (!contains) {
				return false;
			}

		}

		// for (Var z : v.forwardNeighbors) {
		// Set<Term> set = candidats.get(z);
		// if (z.possibleImage[v.level - 1] != null) {
		// set.retainAll(z.possibleImage[v.level - 1]);
		// }
		// z.possibleImage[v.level] = new LinkedList<Term>(set);
		// }

		return true;
	}
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterator<Term> getCandidatsIterator(AtomSet g, Var var) throws AtomSetException {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
