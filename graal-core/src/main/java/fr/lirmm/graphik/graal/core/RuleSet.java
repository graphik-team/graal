package fr.lirmm.graphik.graal.core;

import java.util.Iterator;

public interface RuleSet extends ImmutableRuleSet {

	public boolean add(Rule rule);

	public boolean remove(Rule rule);

	@Override
	public boolean contains(Rule rule);

	@Override
	public Iterator<Rule> iterator();

};
