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
package fr.lirmm.graphik.graal.homomorphism.backjumping;

import java.util.SortedSet;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.homomorphism.Var;
import fr.lirmm.graphik.graal.homomorphism.VarSharedData;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;

/**
 * The GraphBaseBackJumping allows jump (in case of failure) to the last
 * neighbor node from the query graph (in the search order of the variable)
 * <br/>
 * Reference: Dechter, R. (1990). Enhancement Schemes for Constraint Processing:
 * Backjumping, Learning, and Cutset Decomposition. [Artificial Intelligence,
 * 41(3): 273-312]
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class GraphBaseBackJumping extends AbstractProfilable implements BackJumping {

	/**
	 * A data extension for variable indexed by level
	 */
	private VarData[] data;

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void init(VarSharedData[] vars) {
		this.data = new VarData[vars.length];

		for (int i = 0; i < vars.length; ++i) {
			this.data[vars[i].level] = new VarData();
		}
	}
	
	@Override
	public void clear() {
		for(VarData d : data) {
			d.clear();
		}
	}
	
	@Override
	public void success() {
		for (int i = 0; i < this.data.length - 1; ++i) {
			this.data[i].success = true;
		}
	}
	
	@Override
	public void level(int level) {
		this.data[level].success = false;
	}

	@Override
	public int previousLevel(VarSharedData var, Var[] vars) {
		int ret = var.previousLevel;

		VarSharedData v = null, y = null;

		// get the higher neighbor of var
		if (!var.preVars.isEmpty()) {
			v = var.preVars.last();
		}

		// look for a higher var in the backjump set
		if (!this.data[var.level].backjumpSet.isEmpty()) {
			y = this.data[var.level].backjumpSet.last();
			if (v != null && v.compareTo(y) < 0) {
				v = y;
			}
		}

		// update backjump set of the var v
		if (v != null && !data[v.level].success) {
			this.data[v.level].backjumpSet.addAll(var.preVars);
			this.data[v.level].backjumpSet.addAll(this.data[var.level].backjumpSet);
			this.data[v.level].backjumpSet.remove(v);
			if (this.getProfiler().isProfilingEnabled()) {
				this.getProfiler().incr("#backjumps", 1);
				this.getProfiler().incr("#varsBackjumped", var.level - v.level);
			}
			ret = v.level;
		}

		this.data[var.level].backjumpSet.clear();
		return ret;
	}

	@Override
	public void addNeighborhoodToBackjumpSet(VarSharedData from, VarSharedData to) {
		for (VarSharedData v : from.preVars) {
			if (v.level < to.level) {
				this.data[to.level].backjumpSet.add(v);
			}
		}
	}

	@Override
	public StringBuilder append(StringBuilder sb, int level) {
		sb.append("\tBJSet{");
		for (VarSharedData v : this.data[level].backjumpSet) {
			sb.append(v.value);
			sb.append(", ");
		}
		return sb.append("}");
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private class VarData {
		public SortedSet<VarSharedData> backjumpSet = new TreeSet<VarSharedData>();
		public boolean success = false;
		/**
		 * 
		 */
		public void clear() {
			this.backjumpSet.clear();
			this.success = false;
		}
	}

}
