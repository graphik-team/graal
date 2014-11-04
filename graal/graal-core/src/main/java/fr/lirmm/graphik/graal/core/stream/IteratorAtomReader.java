package fr.lirmm.graphik.graal.core.stream;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.util.stream.AbstractReader;

public class IteratorAtomReader extends AbstractReader<Atom> {
	
	private Iterator<Atom> iterator;
	
	public IteratorAtomReader(Iterator<Atom>  iterator) {
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
	public Atom next() {
		return this.iterator.next();
	}

	@Override
	public Iterator<Atom> iterator() {
		return this;
	}

}
