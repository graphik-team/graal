/**
 * 
 */
package fr.lirmm.graphik.graal.core.ruleset;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class IndexedByBodyPredicatesRuleSet extends LinkedListRuleSet {

	TreeMap<Predicate, RuleSet> map;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public IndexedByBodyPredicatesRuleSet() {
		super();
		this.map = new TreeMap<Predicate, RuleSet>();
	}

	public IndexedByBodyPredicatesRuleSet(Iterable<Rule> rules) {
		super();
		this.map = new TreeMap<Predicate, RuleSet>();
		for (Rule r : rules) {
			this.add(r);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// SPECIFIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public Iterable<Rule> getRulesByBodyPredicate(Predicate predicate) {
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
		for (Atom a : rule.getBody()) {
			add(a.getPredicate(), rule);
		}
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends Rule> c) {
		boolean res = super.addAll(c);
		for (Rule rule : c) {
			for (Atom a : rule.getBody()) {
				add(a.getPredicate(), rule);
			}
		}
		return res;
	}

	@Override
	public boolean remove(Rule rule) {
		boolean res = super.remove(rule);
		for (Atom a : rule.getBody()) {
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
