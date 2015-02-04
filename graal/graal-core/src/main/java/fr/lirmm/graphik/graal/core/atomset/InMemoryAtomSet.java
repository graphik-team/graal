package fr.lirmm.graphik.graal.core.atomset;

import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;

public interface InMemoryAtomSet extends AtomSet {
	
	@Override
	boolean contains(Atom atom);
	
	@Override
	Iterable<Predicate> getAllPredicates();
	
	@Override
	Set<Term> getTerms();
	
	@Override
	boolean addAll(Iterable<? extends Atom> atoms);
	
	@Override
	boolean removeAll(Iterable<? extends Atom> atoms);

}
