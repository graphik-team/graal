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
package fr.lirmm.graphik.graal.io.dlp;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.dlgp2.parser.ParserListener;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.Constant;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
abstract class AbstractDlgpListener implements ParserListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractDlgpListener.class);

	private List<Term> answerVars;
	private LinkedListAtomSet atomSet = null;
	private LinkedListAtomSet atomSet2 = null;
	private DefaultAtom atom;
	private String label;

	protected abstract void createAtomSet(InMemoryAtomSet atom);

	protected abstract void createQuery(DefaultConjunctiveQuery query);

	protected abstract void createRule(DefaultRule basicRule);

	protected abstract void createNegConstraint(
			NegativeConstraint negativeConstraint);

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
			this.answerVars.add((Term) t);
		}
	}

	@Override
	public void endsConjunction(OBJECT_TYPE objectType) {
		switch (objectType) {
		case QUERY:
			this.createQuery(new DefaultConjunctiveQuery(this.label,
					this.atomSet, this.answerVars));
			break;
		case NEG_CONSTRAINT:
			this.createNegConstraint(new NegativeConstraint(this.label,
					this.atomSet));
			break;
		case RULE:
			if (this.atomSet2 == null) {
				this.atomSet2 = this.atomSet;
				this.atomSet = new LinkedListAtomSet();
			} else {
				this.createRule(new DefaultRule(this.label, this.atomSet,
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

	private Predicate createPredicate(Object uri, int arity) {
		return new Predicate(uri, arity);
	}

	private Constant createConstant(Object uri) {
		return DefaultTermFactory.instance().createConstant(uri);
	}

	private Term createTerm(Object t) {
		if (t instanceof Term) {
			return (Term) t;
		} else {
			return createConstant(t);
		}
	}

}