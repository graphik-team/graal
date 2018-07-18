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
package fr.lirmm.graphik.graal.core;

import java.util.HashMap;
import java.util.Map;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;

public class AtomType {
	public static final int VARIABLE = -1;
	public static final int CONSTANT_OR_FROZEN_VAR = -2;
	
	boolean isThereConstant;
	boolean isThereConstraint;
	int type[];
	int size;
	
	
	public AtomType(Atom atom, Substitution s) {
		isThereConstant = false;
		size = atom.getPredicate().getArity();
		type = new int[size];
		Map<Term, Integer> firstPositionMap = new HashMap<Term,Integer>();
		int i = -1;
		for(Term t : atom) {
			++i;
			if(t.isConstant() || s.getTerms().contains(t)) {
				type[i] = CONSTANT_OR_FROZEN_VAR;
				isThereConstant = true;
				isThereConstraint = true;
			} else {
				Integer firstPos = firstPositionMap.get(t);
				if(firstPos == null) {
					firstPositionMap.put(t, i);
					firstPos = VARIABLE;
				} else {
					isThereConstraint = true;
				}
				type[i] = firstPos;
			}
		}
	}
	
	public AtomType(Atom atom) {
		this(atom, Substitutions.emptySubstitution());
	}

	public boolean isThereConstant() {
		return this.isThereConstant;
	}
	
	public boolean isThereConstraint() {
		return this.isThereConstraint;
	}


}