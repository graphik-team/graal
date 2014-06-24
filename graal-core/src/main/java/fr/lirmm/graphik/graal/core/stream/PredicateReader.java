package fr.lirmm.graphik.graal.core.stream;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Predicate;

public interface PredicateReader extends Iterable<Predicate>, Iterator<Predicate> {
	
	boolean hasNext();
	Predicate next();
	Iterator<Predicate> iterator();
}
