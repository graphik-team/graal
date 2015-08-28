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
package fr.lirmm.graphik.graal.io.oxford;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
class OxfordQueryParserListener {
	
	private enum State {
		HEAD, BODY
	}
	
	private ConjunctiveQuery cquery = null;
	private State state;
	private LinkedList<Term> awsweredVariables = new LinkedList<Term>();
	private LinkedListAtomSet body = new LinkedListAtomSet();

	private LinkedList<Term> termsOfCurrentAtom = null;
	private String predicateLabelOfCurrentAtom = null;

	public ConjunctiveQuery getQuery() {
		return this.cquery;
	}
	
	public void startQuery() {
		this.state = State.HEAD;
	}
	

	public void endOfQuery() {
		this.cquery = ConjunctiveQueryFactory.instance().create(this.body, this.awsweredVariables);
	}
	

	public void startBody() {
		this.state = State.BODY;
	}
	

	public void startAtom() {
		this.termsOfCurrentAtom = new LinkedList<Term>();
	}
	

	public void endOfAtom() {
		Predicate predicate = new Predicate(this.predicateLabelOfCurrentAtom, this.termsOfCurrentAtom.size());
		Atom atom = new DefaultAtom(predicate, this.termsOfCurrentAtom);
		this.body.add(atom);
	}
	

	public void predicate(String label) {
		this.predicateLabelOfCurrentAtom = label;
	}


	public void constant(String label) {
		Term term = DefaultTermFactory.instance().createConstant(label);
		switch(state) {
		case HEAD:
			this.awsweredVariables.add(term);
			break;
		case BODY:
			this.termsOfCurrentAtom.add(term);
		}
	}


	public void variable(String label) {
		Term term = DefaultTermFactory.instance().createVariable(label);
		switch(state) {
		case HEAD:
			this.awsweredVariables.add(term);
			break;
		
		case BODY:
			this.termsOfCurrentAtom.add(term);
			break;
		}
	}



}
