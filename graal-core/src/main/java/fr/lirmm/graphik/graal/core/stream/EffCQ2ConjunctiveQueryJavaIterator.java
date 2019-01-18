package fr.lirmm.graphik.graal.core.stream;

import java.util.Iterator;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.EffectiveConjunctiveQuery;

/**
 * @author Olivier Rodriguez
 */
public class EffCQ2ConjunctiveQueryJavaIterator implements Iterator<ConjunctiveQuery> {
	private Iterator<EffectiveConjunctiveQuery> iterator;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public EffCQ2ConjunctiveQueryJavaIterator(Iterator<EffectiveConjunctiveQuery> it) {
		this.iterator = it;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	@Override
	public ConjunctiveQuery next() {
		return this.iterator.next().getQuery();
	}

	@Override
	public void remove() {
		this.iterator.remove();
	}
}
