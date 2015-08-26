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
 package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.core.term.Term.Type;

/**
 * rule with only one atom in its head
 * 
 * @author Mélanie KÖNIG
 * 
 */
class AtomicHeadRule implements Rule {

	private Rule rule;

	/**
	 * Construct an AtomicHeadRule
	 * 
	 * @param b
	 *            a fact
	 * @param h
	 *            must be an AtomicFact
	 * @throws Exception
	 */
	public AtomicHeadRule(AtomSet b, Atom h) {
		this.rule = RuleFactory.instance().create(b, new AtomicAtomSet(h));
	}

	@Override
	public AtomicAtomSet getHead() {
		return (AtomicAtomSet) this.rule.getHead();
	}

	@Override
	public int compareTo(Rule arg0) {
		return this.rule.compareTo(arg0);
	}

	@Override
	public String getLabel() {
		return this.rule.getLabel();
	}

	@Override
	public void setLabel(String label) {
		this.rule.setLabel(label);
	}

	@Override
	public InMemoryAtomSet getBody() {
		return this.rule.getBody();
	}

	@Override
	public Set<Term> getFrontier() {
		return this.rule.getFrontier();
	}

	@Override
	public Set<Term> getExistentials() {
		return this.rule.getExistentials();
	}

	@Override
	public Set<Term> getTerms(Type type) {
		return this.rule.getTerms(type);
	}

	@Override
	public Set<Term> getTerms() {
		return this.rule.getTerms();
	}

	@Override
	public void appendTo(StringBuilder sb) {
		this.rule.appendTo(sb);
	}

}
