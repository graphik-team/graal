package fr.lirmm.graphik.kb.core;

import java.util.LinkedList;

import fr.lirmm.graphik.kb.stream.IteratorRuleReader;
import fr.lirmm.graphik.util.stream.ObjectReader;

public class LinkedListRuleSet implements RuleSet {

	@Override
	public boolean contains(Rule rule) {
		return _list.contains(rule);
	}

	@Override
	public boolean add(Rule rule) {
		if (_list.contains(rule))
			return false;
		_list.add(rule);
		return true;
	}

	@Override
	public boolean remove(Rule rule) {
		return _list.remove(rule);
	}

	@Override
	public ObjectReader<Rule> iterator() {
		return new IteratorRuleReader(_list.iterator());
	}


	private LinkedList<Rule> _list = new LinkedList<Rule>();

};

