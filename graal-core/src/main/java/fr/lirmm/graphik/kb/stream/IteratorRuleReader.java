package fr.lirmm.graphik.kb.stream;

import java.util.Iterator;

import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.util.stream.AbstractReader;

public class IteratorRuleReader extends AbstractReader<Rule> {
	
	public IteratorRuleReader(Iterator<Rule>  iterator) {
		_iterator = iterator;
	}

	@Override
	public void remove() {
		_iterator.remove();
	}

	@Override
	public boolean hasNext() {
		return _iterator.hasNext();
	}

	@Override
	public Rule next() {
		return _iterator.next();
	}

	@Override
	public Iterator<Rule> iterator() {
		return this;
	}

	private Iterator<Rule> _iterator;

}
