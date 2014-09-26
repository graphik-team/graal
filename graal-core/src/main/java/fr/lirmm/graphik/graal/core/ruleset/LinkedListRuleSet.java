package fr.lirmm.graphik.graal.core.ruleset;

import java.util.Collection;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.stream.IteratorRuleReader;
import fr.lirmm.graphik.util.stream.ObjectReader;

public class LinkedListRuleSet implements RuleSet, Collection<Rule> {

	private LinkedList<Rule> ruleList;
	
	public LinkedListRuleSet() {
		this.ruleList = new LinkedList<Rule>();
	}
	
	/**
	 * @param rules
	 */
	public LinkedListRuleSet(Iterable<Rule> rules) {
		this();
		for(Rule r : rules) {
			this.ruleList.add(r);
		}
	}

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
	public ObjectReader<Rule> iterator() {
		return new IteratorRuleReader(ruleList.iterator());
	}


	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends Rule> c) {
		return this.ruleList.addAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear() {
		this.ruleList.clear();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
		return this.ruleList.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		return this.ruleList.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.ruleList.isEmpty();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		return this.ruleList.remove(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		return this.ruleList.removeAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		return this.ruleList.retainAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size() {
		return this.ruleList.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.ruleList.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
		return this.ruleList.toArray(a);
	}

};

