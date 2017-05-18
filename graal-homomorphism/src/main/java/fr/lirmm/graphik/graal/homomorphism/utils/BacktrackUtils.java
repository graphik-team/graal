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
package fr.lirmm.graphik.graal.homomorphism.utils;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.homomorphism.Var;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class BacktrackUtils {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private BacktrackUtils() {
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param atomsFrom
	 * @param atomsTo
	 * @param index
	 * @param rc
	 * @return true if there is a homomorphism, false otherwise.
	 * @throws AtomSetException
	 */
	public static boolean isHomomorphism(Iterable<Atom> atomsFrom, AtomSet atomsTo, Substitution initialSubstitution, Map<Variable, Integer> index,
			Var[] varData, RulesCompilation rc) throws AtomSetException {
		for (Atom atom : atomsFrom) {
			Atom image = BacktrackUtils.createImageOf(atom, initialSubstitution, index, varData);
			boolean contains = false;

			for (Pair<Atom, Substitution> p : rc.getRewritingOf(image)) {
				if (atomsTo.contains(p.getLeft())) {
					contains = true;
					break;
				}
			}

			if (!contains)
				return false;
		}
		return true;
	}

	/**
	 * 
	 * @param atom
	 * @param map
	 * @return an image of specified atom obtained by replacement variables contained in the map with the associated Var.image. 
	 */
	public static Atom createImageOf(Atom atom, Substitution initialSubstitution, Map<Variable, Integer> map, Var[] varData) {
		Term[] termsSubstitut = new Term[atom.getPredicate().getArity()];
		
		int i = -1;
		for (Term term : atom) {
			if (term.isVariable()) {
				Term t = initialSubstitution.createImageOf(term);
				termsSubstitut[++i] = t.isVariable()? imageOf((Variable) t, map, varData) : t;
			} else {
				termsSubstitut[++i] = term;
			}
		}

		return new DefaultAtom(atom.getPredicate(), termsSubstitut);
	}

	/**
	 * Return the image of the specified variable (extracted from map).
	 * 
	 * @param var
	 * @return the variable image
	 */
	public static Term imageOf(Variable var, Map<Variable, Integer> map, Var[] varData) {
		Integer i = map.get(var);
		if( i != null) {
			Term t = varData[i].image;
			if (t != null) {
				return t;
			}
		}
		return var;
	}

	/**
	 * Extract image of variables from Var class in a Substitution.
	 * 
	 * @param vars
	 * @return a Substitution obtained by association of Var.value with Var.image.
	 */
	public static Substitution createSubstitution(Var[] vars) {
		Substitution s = new TreeMapSubstitution();
		for (Var v : vars) {
			if (v.image != null) {
				s.put(v.shared.value, v.image);
			}
		}
		return s;
	}

}
