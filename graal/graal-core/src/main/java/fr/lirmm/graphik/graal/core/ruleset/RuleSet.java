package fr.lirmm.graphik.graal.core.ruleset;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Rule;

public interface RuleSet extends ImmutableRuleSet {

	public boolean add(Rule rule);
	
	public boolean addAll(Iterator<Rule> ruleIterator);

	public boolean remove(Rule rule);
	
	public boolean removeAll(Iterator<Rule> ruleIterator);

	@Override
	public boolean contains(Rule rule);

	@Override
	public Iterator<Rule> iterator();

};
