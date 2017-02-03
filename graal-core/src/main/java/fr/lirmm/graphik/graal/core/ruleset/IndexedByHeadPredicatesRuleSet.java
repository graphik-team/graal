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
package fr.lirmm.graphik.graal.core.ruleset;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class IndexedByHeadPredicatesRuleSet extends LinkedListRuleSet {

	TreeMap<Predicate, RuleSet> map;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public IndexedByHeadPredicatesRuleSet() {
		super();
		this.map = new TreeMap<Predicate, RuleSet>();
	}

	public IndexedByHeadPredicatesRuleSet(Iterable<Rule> rules) {
		super();
		this.map = new TreeMap<Predicate, RuleSet>();
		for (Rule r : rules) {
			this.add(r);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// SPECIFIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public Iterable<Rule> getRulesByHeadPredicate(Predicate predicate) {
		Iterable<Rule> res = this.map.get(predicate);
		if (res == null) {
			res = Collections.<Rule> emptyList();
		}
		return res;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean add(Rule rule) {
		super.add(rule);
		CloseableIteratorWithoutException<Atom> it = rule.getHead().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			add(a.getPredicate(), rule);
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Rule> c) {
		boolean res = super.addAll(c);
		for (Rule rule : c) {
			CloseableIteratorWithoutException<Atom> it = rule.getHead().iterator();
			while (it.hasNext()) {
				Atom a = it.next();
				add(a.getPredicate(), rule);
			}
		}
		return res;
	}

	@Override
	public boolean remove(Rule rule) {
		boolean res = super.remove(rule);
		CloseableIteratorWithoutException<Atom> it = rule.getHead().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			remove(a.getPredicate(), rule);
		}
		return res;
	}

	@Override
	public void clear() {
		super.clear();
		this.map.clear();
	}

	@Override
	public boolean remove(Object o) {
		if (o instanceof Rule) {
			return this.remove((Rule) o);
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean res = false;
		for (Object o : c) {
			res = this.remove(o) || res;
		}
		return res;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean res = super.retainAll(c);
		this.map.clear();
		this.addAll(this);
		return res;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void add(Predicate p, Rule r) {
		RuleSet rules = this.map.get(p);
		if (rules == null) {
			rules = new LinkedListRuleSet();
			this.map.put(p, rules);
		}
		rules.add(r);
	}

	private void remove(Predicate p, Rule r) {
		RuleSet rules = this.map.get(p);
		if (rules != null) {
			rules.remove(r);
		}
	}
};
