package fr.lirmm.graphik.kb.stream;

import java.util.Iterator;

import fr.lirmm.graphik.kb.core.Predicate;

public interface PredicateReader extends Iterable<Predicate>, Iterator<Predicate> {
	
	boolean hasNext();
	Predicate next();
	Iterator<Predicate> iterator();
}
