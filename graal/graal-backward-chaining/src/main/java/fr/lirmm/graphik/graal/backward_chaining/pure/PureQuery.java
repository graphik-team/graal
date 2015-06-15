/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Literal;
import fr.lirmm.graphik.graal.core.term.Term;

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
	 * @param f
	 * @param answerVariable
	 */
	public PureQuery(AtomSet atomSet, Collection<Term> answerVariable) {
		super(atomSet, answerVariable);
	}

	/**
	 * @param f
	 * @param answerVariable
	 */
	public PureQuery(ArrayList<Atom> atoms, ArrayList<Term> answerVariable) {
		super(atoms, answerVariable);
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
		Term[] ans = new Term[query.getAnswerVariables().size()];
		Iterator<Atom> ita = query.getAtomSet().iterator();
		while (ita.hasNext()) {
			Atom a = ita.next();
			if (a.getPredicate().equals(ansPredicate)) {
				ans[(Integer) ((Literal) a.getTerm(0)).getValue()] = a
						.getTerm(1);
				ita.remove();
			}
		}
		query.setAnswerVariables(Arrays.asList(ans));
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
