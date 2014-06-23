package fr.lirmm.graphik.kb.core;

import fr.lirmm.graphik.util.stream.ObjectReader;

public interface RuleSet extends Iterable<Rule> {

    public boolean contains(Rule rule);
	public boolean add(Rule rule);
	public boolean remove(Rule rule);

	@Override
	public ObjectReader<Rule> iterator();

};

