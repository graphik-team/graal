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
package fr.lirmm.graphik.graal.core.factory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultConjunctiveQueryFactory implements ConjunctiveQueryFactory {
	
	public final ConjunctiveQuery BOOLEAN_BOTTOM_QUERY; 

	// /////////////////////////////////////////////////////////////////////////
	// SINGLETON
	// /////////////////////////////////////////////////////////////////////////

	private static DefaultConjunctiveQueryFactory instance;

	protected DefaultConjunctiveQueryFactory() {
		super();
		BOOLEAN_BOTTOM_QUERY = this.create(
				DefaultAtomFactory.instance().create(Predicate.BOTTOM, DefaultTermFactory.instance().createVariable("X")),
			        Collections.<Term>emptyList());
	}

	public static synchronized DefaultConjunctiveQueryFactory instance() {
		if (instance == null)
			instance = new DefaultConjunctiveQueryFactory();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public ConjunctiveQuery create() {
		return new DefaultConjunctiveQuery();
	}

	@Override
	public ConjunctiveQuery create(InMemoryAtomSet atomSet) {
		return new DefaultConjunctiveQuery(atomSet);
	}

	@Override
	public ConjunctiveQuery create(Atom atom) {
		LinkedList<Atom> list = new LinkedList<Atom>();
		list.add(atom);
		return new DefaultConjunctiveQuery(new LinkedListAtomSet(list));
	}

	@Override
	public ConjunctiveQuery create(Atom atom, List<Term> ans) {
		LinkedList<Atom> list = new LinkedList<Atom>();
		list.add(atom);
		return new DefaultConjunctiveQuery(new LinkedListAtomSet(list), ans);
	}

	@Override
	public ConjunctiveQuery create(InMemoryAtomSet atomSet, List<Term> ans) {
		return new DefaultConjunctiveQuery(atomSet, ans);
	}

	@Override
	public ConjunctiveQuery create(CloseableIteratorWithoutException<Atom> atomSet,
	    CloseableIteratorWithoutException<Term> answerVariables) {
		return new DefaultConjunctiveQuery(atomSet, answerVariables);
	}

	@Override
	public ConjunctiveQuery create(String label, InMemoryAtomSet atomSet, List<Term> ans) {
		return new DefaultConjunctiveQuery(label, atomSet, ans);
	}

	@Override
	public ConjunctiveQuery create(ConjunctiveQuery query) {
		return new DefaultConjunctiveQuery(query);
	}


}
