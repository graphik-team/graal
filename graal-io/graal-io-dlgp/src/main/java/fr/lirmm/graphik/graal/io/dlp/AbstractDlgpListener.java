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
package fr.lirmm.graphik.graal.io.dlp;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import fr.lirmm.graphik.dlgp2.parser.ParserListener;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.io.ParseError;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultNegativeConstraint;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
abstract class AbstractDlgpListener implements ParserListener {

	private List<Term> answerVars;
	private LinkedListAtomSet atomSet = null;
	private LinkedListAtomSet atomSet2 = null;
	private DefaultAtom atom;
	private String label;

	protected abstract void createAtomSet(InMemoryAtomSet atom);

	protected abstract void createQuery(ConjunctiveQuery query);

	protected abstract void createRule(Rule basicRule);

	protected abstract void createNegConstraint(
			DefaultNegativeConstraint negativeConstraint);

	@Override
	public void startsObject(OBJECT_TYPE objectType, String name) {
		this.label = (name == null) ? "" : name;

		atomSet = new LinkedListAtomSet();
		atomSet2 = null;

		if (OBJECT_TYPE.QUERY.equals(objectType)) {
			this.answerVars = new LinkedList<Term>();
		}


	}

	@Override
	public void createsAtom(Object predicate, Object[] terms) {
		List<Term> list = new LinkedList<Term>();
		for (Object t : terms) {
			list.add(createTerm(t));
		}

		atom = new DefaultAtom(createPredicate(predicate, terms.length),
				list);
		this.atomSet.add(atom);

	}

	@Override
	public void createsEquality(Object term1, Object term2) {
		atom = new DefaultAtom(Predicate.EQUALITY, createTerm(term1),
				createTerm(term2));
		this.atomSet.add(atom);

	}

	@Override
	public void answerTermList(Object[] terms) {
		for (Object t : terms) {
			this.answerVars.add(createTerm(t));
		}
	}

	@Override
	public void endsConjunction(OBJECT_TYPE objectType) {
		switch (objectType) {
		case QUERY:
			Set<Variable> bodyVars = this.atomSet.getVariables();
			for(Term t : this.answerVars) {
				if(t.isVariable() && !bodyVars.contains(t)) {
					throw new ParseError("The variable ["+ t +"] of the answer list does not appear in the query body.");
				}
			}
			this.createQuery(
			    DefaultConjunctiveQueryFactory.instance().create(this.label,
				this.atomSet, this.answerVars));
			break;
		case NEG_CONSTRAINT:
			this.createNegConstraint(new DefaultNegativeConstraint(this.label,
					this.atomSet));
			break;
		case RULE:
			if (this.atomSet2 == null) {
				this.atomSet2 = this.atomSet;
				this.atomSet = new LinkedListAtomSet();
			} else {
				this.createRule(DefaultRuleFactory.instance().create(this.label, this.atomSet,
						this.atomSet2));
			}
			break;
		case FACT:
			this.createAtomSet(this.atomSet);
			break;
		default:
			break;
		}
	}


	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static Predicate createPredicate(Object uri, int arity) {
		return new Predicate(uri, arity);
	}

	private static Constant createConstant(Object uri) {
		return DefaultTermFactory.instance().createConstant(uri);
	}

	private static Term createTerm(Object t) {
		if (t instanceof Term) {
			return (Term) t;
		} else {
			return createConstant(t);
		}
	}

}