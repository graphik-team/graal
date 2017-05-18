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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.PreparedExistentialHomomorphism;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class VarSharedData implements Comparable<VarSharedData> {

	public int level;
	public Variable value;

	/*
	 * Each atoms from the request graph in which this variable have the highest
	 * level.
	 */
	public Collection<Atom> preAtoms;
	/*
	 * Each atoms from the request graph that is not in preAtoms and in which
	 * this variable appears.
	 */
	public Collection<Atom> postAtoms;

	// Forward Checking
	public NavigableSet<VarSharedData> preVars;
	public Set<VarSharedData> postVars;

	// BackJumping
	public int nextLevel;
	public int previousLevel;

	public List<PreparedExistentialHomomorphism> negatedPartsToCheck;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public VarSharedData() {
		negatedPartsToCheck = new LinkedList<PreparedExistentialHomomorphism>();
	}

	public VarSharedData(int level) {
		this();
		this.level = level;
		this.previousLevel = level - 1;
		this.nextLevel = level + 1;
	}

	/**
	 * copy constructor
	 * 
	 * @param v
	 */
	public VarSharedData(VarSharedData v) {
		this();

		this.value = v.value;
		this.level = v.level;
		this.previousLevel = v.previousLevel;
		this.nextLevel = v.nextLevel;

	}
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public int hashCode() {
		return this.level;
	}

	/**
	 * Use for debugging
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[').append(value).append("(").append(previousLevel).append("<-").append(level).append("->")
				.append(nextLevel).append(")");
		sb.append("]\n");
		return sb.toString();
	}

	@Override
	public int compareTo(VarSharedData o) {
		return this.level - o.level;
	}
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////


}
