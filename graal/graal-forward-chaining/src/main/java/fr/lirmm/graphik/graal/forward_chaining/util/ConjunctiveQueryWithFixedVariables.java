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
package fr.lirmm.graphik.graal.forward_chaining.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class ConjunctiveQueryWithFixedVariables implements ConjunctiveQuery {

	private InMemoryAtomSet atomSet;
	private List<Term> answerVariables;

	public ConjunctiveQueryWithFixedVariables(AtomSet atomSet, Iterable<Term> fixedTerms) {
		this.atomSet = computeFixedQuery(atomSet, fixedTerms);
        this.answerVariables = new LinkedList(this.atomSet.getTerms(Term.Type.VARIABLE));
    }

	public ConjunctiveQueryWithFixedVariables(/*ReadOnly*/AtomSet atomSet,
			List<Term> responseVariables, Iterable<Term> fixedTerms) {

		this.atomSet = computeFixedQuery(atomSet, fixedTerms);
		this.answerVariables = responseVariables;
		if (this.answerVariables == null) {
			this.answerVariables = new LinkedList<Term>();
		}
	}

	@Override
	public Iterator<Atom> iterator() { return getAtomSet().iterator(); }
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean isBoolean() {
		return this.answerVariables.isEmpty();
	}

	@Override
	public InMemoryAtomSet getAtomSet() {
		return this.atomSet;
	}

	@Override
	public List<Term> getAnswerVariables() {
		return this.answerVariables;
	}
	
	@Override
	public void setAnswerVariables(List<Term> ans) {
		this.answerVariables = ans;
	}
	

	@Override
	public String getLabel() {
		return "";
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	private static InMemoryAtomSet computeFixedQuery(/*ReadOnly*/AtomSet atomSet,
			Iterable<Term> fixedTerms) {
		// create a Substitution for fixed query
		InMemoryAtomSet fixedQuery = AtomSetFactory.getInstance().createAtomSet();
		Substitution fixSub = SubstitutionFactory.getInstance().createSubstitution();
		for (Term t : fixedTerms) {
			if (!t.isConstant())
				fixSub.put(
						t,
						DefaultTermFactory.instance().createConstant(
								t.getLabel()));
		}

		// apply substitution
		for (Atom a : atomSet) {
			fixedQuery.add(fixSub.createImageOf(a));
		}
		
		return fixedQuery;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("FIXED(");
		for (Term t : this.atomSet.getTerms(Term.Type.CONSTANT))
			s.append(t).append(',');

		s.append("), ANS(");
		for (Term t : this.answerVariables)
			s.append(t).append(',');

		s.append(") :- ");
		s.append(this.atomSet);
		return s.toString();
	}
	
}
