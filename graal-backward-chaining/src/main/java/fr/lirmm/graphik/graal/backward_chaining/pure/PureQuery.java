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
 /**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
class PureQuery extends DefaultConjunctiveQuery {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public PureQuery() {
		super();
	}

	/**
	 * Create a query which has the same atom and id as the given fact and which
	 * has the given term as answerVariable
	 * 
	 * @param atomSet
	 * @param answerVariable
	 */
	public PureQuery(InMemoryAtomSet atomSet, List<Term> answerVariable) {
		super(atomSet, answerVariable);
	}

	/**
	 * Copy constructor
	 * 
	 * @param q
	 */
	public PureQuery(ConjunctiveQuery q) {
		super(q);
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	private static Predicate ansPredicate = new Predicate("__ans", 2);
	public void removeAnswerPredicate() {
		removeAnswerPredicate(this);
	}
	
	public static void removeAnswerPredicate(ConjunctiveQuery query) {
		Term[] ans = query.getAnswerVariables().toArray(new Term[query.getAnswerVariables().size()]) ;
		CloseableIteratorWithoutException<Atom> ita = query.getAtomSet().iterator();
		InMemoryAtomSet toRemove = new LinkedListAtomSet();
		InMemoryAtomSet toAdd = new LinkedListAtomSet();

		while (ita.hasNext()) {
			Atom a = ita.next();
			if (a.getPredicate().equals(ansPredicate)) {
				Term ansTerm = ans[(Integer) ((Literal) a.getTerm(0)).getValue()];
				if(!ansTerm.equals(a.getTerm(1))) {
					toAdd.add(DefaultAtomFactory.instance().create(Predicate.EQUALITY, ansTerm, a.getTerm(1)));
				}
				toRemove.add(a);
			}
		}
		query.getAtomSet().removeAll(toRemove);
		query.getAtomSet().addAll(toAdd);
	}

	public void addAnswerPredicate() {
		int i = -1;
		for(Term t: getAnswerVariables()) {
			this.getAtomSet().add(
					new DefaultAtom(ansPredicate, DefaultTermFactory.instance()
							.createLiteral(++i), t));
		}
	}
}
