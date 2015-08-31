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
/**
 * 
 */
package fr.lirmm.graphik.graal.core.factory;

import java.util.List;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.impl.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class ConjunctiveQueryFactory {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private static ConjunctiveQueryFactory instance = new ConjunctiveQueryFactory();

	protected ConjunctiveQueryFactory() {
		super();
	}

	public static synchronized ConjunctiveQueryFactory instance() {
		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Create an empty query
	 * 
	 * @return
	 */
	public ConjunctiveQuery create() {
		return new DefaultConjunctiveQuery();
	}

	/**
	 * Copy
	 * 
	 * @param query
	 * @return
	 */
	public ConjunctiveQuery create(ConjunctiveQuery query) {
		return new DefaultConjunctiveQuery(query);
	}

	/**
	 * Create a query from the specified atom set. All variables appearing in
	 * the atom set will be considered as answer variables.
	 * 
	 * @param atomSet
	 * @return
	 */
	public ConjunctiveQuery create(InMemoryAtomSet atomSet) {
		return new DefaultConjunctiveQuery(atomSet);
	}

	/**
	 * Create a query from the specified atom set and the specified answer
	 * variables.
	 * 
	 * @param atomSet
	 * @param ans
	 * @return
	 */
	public ConjunctiveQuery create(InMemoryAtomSet atomSet, List<Term> ans) {
		return new DefaultConjunctiveQuery(atomSet, ans);
	}

	/**
	 * Create a query from the specified atom set and the specified answer
	 * variables.
	 * 
	 * @param atomSet
	 * @param answerVariables
	 * @return
	 */
	public ConjunctiveQuery create(Iterable<Atom> atomSet, Iterable<Term> answerVariables) {
		return new DefaultConjunctiveQuery(atomSet, answerVariables);
	}

	/**
	 * Create a query from the specified atom set, answer variables and label.
	 * 
	 * @param label
	 * @param atomSet
	 * @param ans
	 * @return
	 */
	public ConjunctiveQuery create(String label, InMemoryAtomSet atomSet, List<Term> ans) {
		return new DefaultConjunctiveQuery(label, atomSet, ans);
	}

}
