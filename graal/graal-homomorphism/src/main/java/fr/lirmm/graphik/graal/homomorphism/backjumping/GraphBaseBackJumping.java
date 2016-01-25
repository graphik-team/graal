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
package fr.lirmm.graphik.graal.homomorphism.backjumping;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.homomorphism.Var;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class GraphBaseBackJumping implements Backjumping {

	/**
	 * A data extension for variable indexed by level
	 */
	private VarData[] data;

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
    public void init(Var[] vars, Map<Variable, Var> map) {
		this.data = new VarData[vars.length];

		for (int i = 0; i < vars.length; ++i) {
			vars[i].preVars = new TreeSet<Var>();
			for (Atom a : vars[i].preAtoms) {
				for (Term t : a.getTerms(Type.VARIABLE)) {
					Var v = map.get((Variable) t);
					if (v.level > i) {
						vars[i].preVars.add(v);
					}
				}
			}

			this.data[vars[i].level] = new VarData();
			this.data[vars[i].level].preVars = new TreeSet<Var>();
			this.data[vars[i].level].backjumpSet = new TreeSet<Var>();
		}
	}

	@Override
	public int previousLevel(Var var, Var[] vars) {
		int ret = var.previousLevel;

		Var v = null, y = null;
		if (!this.data[var.level].preVars.isEmpty()) {
			v = this.data[var.level].preVars.last();
		}
		if (!this.data[var.level].backjumpSet.isEmpty()) {
			y = this.data[var.level].backjumpSet.last();
			if (v != null && v.compareTo(y) < 0) {
				v = y;
			}
		}
		if (v != null && !vars[v.level].success) {
			this.data[v.level].backjumpSet.addAll(this.data[var.level].preVars);
			this.data[v.level].backjumpSet.addAll(this.data[var.level].backjumpSet);
			this.data[v.level].backjumpSet.remove(v);
			ret = v.level;
		}
		this.data[var.level].backjumpSet.clear();
		return ret;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private class VarData {
		public SortedSet<Var> preVars;
		public SortedSet<Var> backjumpSet;
	}
}
