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

import parser.ParserListener;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
abstract class AbstractDlgp1Listener implements ParserListener {
    
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractDlgp1Listener.class);
	
	private List<Term> answerVars;
	private LinkedListAtomSet atomSet = null;
	private LinkedListAtomSet atomSet2 = null;
	private DefaultAtom atom;
	private String label;

	private OBJECT_TYPE objectType;

	protected abstract void createAtom(DefaultAtom atom);

	protected abstract void createQuery(DefaultConjunctiveQuery query);
	
	protected abstract void createRule(DefaultRule basicRule);
	
	protected abstract void createNegConstraint(NegativeConstraint negativeConstraint);

	@Override
	public void startsObject(OBJECT_TYPE objectType, String name) {
		this.label = name == null? "" : name;
		
		atomSet = atomSet2 = null;
		this.objectType = objectType;
		
		switch (objectType) {
		case QUERY:
			this.answerVars = new LinkedList<Term>();
			this.atomSet = new LinkedListAtomSet();
			break;
		case RULE:
		case NEG_CONSTRAINT:
			this.atomSet = new LinkedListAtomSet();
			break;
		case FACT:
			break;
		default:
			if(LOGGER.isWarnEnabled()) {
				LOGGER.warn("Unrecognized object type: " + objectType);
			}
			break;
		}
		
	}
	
	

	@Override
	public void createsAtom(String predicate, Object[] terms) {

		List<Term> list = new LinkedList<Term>();
		for (Object t : terms)
			list.add((Term) t);
		
		String predicateWithoutQuotes = removeQuotes(predicate);

		atom = new DefaultAtom(new Predicate(predicateWithoutQuotes, terms.length), list);

		switch (objectType) {
		case FACT:
			this.createAtom(atom);
			break;
		case QUERY:
		case RULE:
		case NEG_CONSTRAINT:
			this.atomSet.add(atom);
			break;
		default:
			break;
		}
	}

	/**
	 * @param predicate
	 */
	private String removeQuotes(String predicate) {
		if(predicate.startsWith("\"") && predicate.endsWith("\"")) {
			return predicate.substring(1, predicate.length() - 1);
		} else {
			return predicate;
		}
	}

	@Override
	public void createsEquality(Object term1, Object term2) {
		atom = new DefaultAtom(Predicate.EQUALITY, (Term) term1, (Term) term2);

		switch (objectType) {
		case FACT:
			this.createAtom(atom);
			break;
		case QUERY:
		case RULE:
		case NEG_CONSTRAINT:
			this.atomSet.add(atom);
			break;
		default:
			break;
		}
	}

	@Override
	public void answerVariableList(Object[] terms) {
		for (Object t : terms)
			this.answerVars.add((Term) t);
	}

	@Override
	public void endsConjunction(OBJECT_TYPE objectType) {
		switch (objectType) {
		case QUERY:
			this.createQuery(new DefaultConjunctiveQuery(this.label, this.atomSet, this.answerVars));
			break;
		case NEG_CONSTRAINT:
			this.createNegConstraint(new NegativeConstraint(this.label, this.atomSet));
			break;
		case RULE:
			if(this.atomSet2 == null) {
    			this.atomSet2 = this.atomSet;
    			this.atomSet = new LinkedListAtomSet();
			} else {
				this.createRule(new DefaultRule(this.label, this.atomSet, this.atomSet2));
			}
			break;
		default:
			break;
		}
	}

}