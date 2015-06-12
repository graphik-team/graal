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
 package fr.lirmm.graphik.graal.core.ruleset;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.stream.IteratorRuleReader;

public class LinkedListRuleSet extends AbstractRuleSet implements
		Collection<Rule> {

	private LinkedList<Rule> ruleList;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	public LinkedListRuleSet() {
		this.ruleList = new LinkedList<Rule>();
	}

	/**
	 * @param rules
	 */
	public LinkedListRuleSet(Iterator<Rule> rules) {
		this();
		while(rules.hasNext()) {
			this.ruleList.add(rules.next());
		}
	}

	public LinkedListRuleSet(Iterable<Rule> rules) {
		this(rules.iterator());
	}

	// //////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public boolean contains(Rule rule) {
		return ruleList.contains(rule);
	}

	@Override
	public boolean add(Rule rule) {
		ruleList.add(rule);
		return true;
	}

	@Override
	public boolean remove(Rule rule) {
		return ruleList.remove(rule);
	}

	@Override
	public Iterator<Rule> iterator() {
		return new IteratorRuleReader(ruleList.iterator());
	}

	@Override
	public boolean addAll(Collection<? extends Rule> c) {
		return this.ruleList.addAll(c);
	}

	@Override
	public void clear() {
		this.ruleList.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.ruleList.contains(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.ruleList.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.ruleList.isEmpty();
	}

	@Override
	public boolean remove(Object o) {
		return this.ruleList.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.ruleList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.ruleList.retainAll(c);
	}

	@Override
	public int size() {
		return this.ruleList.size();
	}

	@Override
	public Object[] toArray() {
		return this.ruleList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.ruleList.toArray(a);
	}

};
