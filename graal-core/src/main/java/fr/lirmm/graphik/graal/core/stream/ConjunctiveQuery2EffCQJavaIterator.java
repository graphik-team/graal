package fr.lirmm.graphik.graal.core.stream;

import java.util.Iterator;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.EffectiveConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultEffectiveConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitutions;

/**
 * Able to iterate over an Iterator of ConjunctiveQuery just as if it was an iterator of EffectiveConjunctiveQuery.
 * The substitutions will be set to empty substitutions.
 * 
 * @author Olivier Rodriguez
 */
public class ConjunctiveQuery2EffCQJavaIterator implements Iterator<EffectiveConjunctiveQuery> {
	private Iterator<ConjunctiveQuery> iterator;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public ConjunctiveQuery2EffCQJavaIterator(Iterator<ConjunctiveQuery> it) {
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
	public EffectiveConjunctiveQuery next() {
		return new DefaultEffectiveConjunctiveQuery(iterator.next(), Substitutions.emptySubstitution());
	}

	@Override
	public void remove() {
		this.iterator.remove();
	}
}
