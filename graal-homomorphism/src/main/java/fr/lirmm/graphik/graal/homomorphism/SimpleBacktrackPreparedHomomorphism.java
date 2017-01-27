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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.PreparedExistentialHomomorphism;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.PatternBootstrapper;
import fr.lirmm.graphik.util.stream.CloseableIterableWithoutException;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * This Backtrack is inspired by the Baget Jean-François Thesis (Chapter 5)
 *
 * see also "Backtracking Through Biconnected Components of a Constraint Graph"
 * (Jean-François Baget, Yannic S. Tognetti IJCAI 2001)
 *
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class SimpleBacktrackPreparedHomomorphism<U1 extends InMemoryAtomSet, U2 extends AtomSet> implements PreparedExistentialHomomorphism<U1, U2> {

	private AtomSet data;
	private InMemoryAtomSet query;
	private int firstRealLevel;
	private Var[] vars;
	private TreeMap<Variable, Var> index;
	
	public SimpleBacktrackPreparedHomomorphism(InMemoryAtomSet q, Set<Variable> preAffectedVariable, AtomSet data) {
		this.query = q;
		this.data = data;
		vars = PatternScheduler.instance().execute(q, preAffectedVariable, data, NoCompilation.instance());
		firstRealLevel = vars[0].nextLevel;

		index = new TreeMap<Variable, Var>();
		for (Var v : vars) {
			if (v.value != null)
				index.put(v.value, v);
		}
		
		computeAtomOrder(q, vars, index);
	}

	// /////////////////////////////////////////////////////////////////////////
	// HOMOMORPHISM METHODS
	// /////////////////////////////////////////////////////////////////////////

	public boolean exist(Substitution s) throws HomomorphismException {
		for(int i = 1; i <= firstRealLevel; ++i) {
			vars[i].image = s.createImageOf(vars[i].value);
		}
		boolean res =  backtrack(query, vars, index, data);
		for (int i = 1; i < vars.length - 1; ++i) {
			vars[i].image = null;
		}
		return res;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	
	private boolean backtrack(InMemoryAtomSet h, Var vars[], Map<Variable, Var> index, AtomSet g) throws BacktrackException {

		int level = firstRealLevel;
		int levelMax = vars.length - 2;
		
		Var currentVar;
		boolean goBack = false;
	
		// check if the full instantiated atoms from the query are
		// in the data
		try {
			if (BacktrackUtils.isHomomorphism(vars[level].preAtoms, g, index, NoCompilation.instance())) {
				++level;
			} else {
				return false;
			}
				
			while (level > firstRealLevel) {
				currentVar = vars[level];
				// Homomorphism found
				if (level > levelMax) {
					return true;
				} 
				
				if (goBack) {
					if (hasMoreValues(currentVar, g)) {
						goBack = false;
						++level;
					} else {
						int nextLevel = currentVar.previousLevel;
						for (; level > nextLevel; --level) {
							vars[level].image = null;
						}
					}
				} else {
					if (getFirstValue(h, currentVar, g, index)) {
						++level;
					} else {
						goBack = true;
						int nextLevel = currentVar.previousLevel;
						for (; level > nextLevel; --level) {
							vars[level].image = null;
						}
					}
				}
			}
	
		} catch (AtomSetException e) {
			throw new BacktrackException(e);
		}

		return false;
	}
	
	private static boolean getFirstValue(InMemoryAtomSet h, Var var, AtomSet g, Map<Variable, Var> index) throws BacktrackException {
		var.domain = new HomomorphismIteratorChecker(var, PatternBootstrapper.instance().exec(var, h, g, NoCompilation.instance(), index),
			                                             var.preAtoms, g, index, NoCompilation.instance());
		
		return hasMoreValues(var, g);
	}

	private static boolean hasMoreValues(Var var, AtomSet g) throws BacktrackException {
		try {
			while (var.domain.hasNext()) {
				// TODO explicit var.success
				var.success = false;
				var.image = var.domain.next();

				// Fix for existential variable in data
				if (!var.image.isConstant()) {
					var.image = DefaultTermFactory.instance().createConstant(var.image.getLabel());
				}
				
				return true;
			}
		} catch (IteratorException e) {
			throw new BacktrackException("An exception occurs during data iteration", e);
		}
		var.domain.close();
		return false;
	}
	
	/**
	 * The index 0 contains the fully instantiated atoms.
	 * 
	 * @param atomset
	 * @param index 
	 * @param varsOrdered
	 * @return
	 */
	private void computeAtomOrder(CloseableIterableWithoutException<Atom> atomset, Var[] vars, Map<Variable, Var> index) {
		int tmp, rank;

		
		// initialisation preAtoms and postAtoms Collections
		for (int i = firstRealLevel; i < vars.length; ++i) {
			vars[i].preAtoms = new TreeSet<Atom>();
			vars[i].postAtoms = new TreeSet<Atom>();
		}

		//
		CloseableIteratorWithoutException<Atom> it = atomset.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			rank = 0;
			for (Term t : a.getTerms(Type.VARIABLE)) {
				tmp = index.get((Variable) t).level;
				if(tmp < firstRealLevel) {
					tmp = firstRealLevel;
				}
				vars[tmp].postAtoms.add(a);

				if (rank < tmp)
					rank = tmp;
			}
			vars[rank].postAtoms.remove(a);
			vars[rank].preAtoms.add(a);
		}

	}

}
