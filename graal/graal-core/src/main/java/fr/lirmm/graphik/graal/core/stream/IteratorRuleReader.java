package fr.lirmm.graphik.graal.core.stream;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.util.stream.AbstractReader;

public class IteratorRuleReader extends AbstractReader<Rule> {
	
	public IteratorRuleReader(Iterator<Rule>  iterator) {
		this.iterator = iterator;
	}

	@Override
	public void remove() {
		this.iterator.remove();
	}

	@Override
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override
	public Rule next() {
		return this.iterator.next();
	}

	@Override
	public Iterator<Rule> iterator() {
		return this;
	}

	private Iterator<Rule> iterator;

}
