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
 package fr.lirmm.graphik.graal.core.ruleset;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSetException;
import fr.lirmm.graphik.graal.core.stream.filter.RuleFilterIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;

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
	
	public LinkedListRuleSet(CloseableIterator<?> parser) throws RuleSetException {
		this();
		this.addAll(new RuleFilterIterator(parser));
		parser.close();
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
		return ruleList.iterator();
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[\n");
		Iterator<Rule> it = this.iterator();
		while (it.hasNext()) {
			sb.append("\t");
			sb.append(it.next());
			sb.append("\n");
		}
		sb.append("]\n");
		return sb.toString();
	}
};
