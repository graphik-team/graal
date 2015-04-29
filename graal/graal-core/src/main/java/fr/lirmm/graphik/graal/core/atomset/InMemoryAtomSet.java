package fr.lirmm.graphik.graal.core.atomset;

import java.util.Iterator;
import java.util.Set;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.Term;

public interface InMemoryAtomSet extends AtomSet {
	
	@Override
	boolean contains(Atom atom);

	@Override
	Set<Predicate> getPredicates();

	@Override
	Iterator<Predicate> predicatesIterator();

	@Override
	Set<Term> getTerms();

	@Override
	Iterator<Term> termsIterator();
	
	@Override
	Set<Term> getTerms(Term.Type type);

	@Override
	Iterator<Term> termsIterator(Term.Type type);

	@Override
	@Deprecated
	boolean isSubSetOf(AtomSet atomset);

	@Override
	boolean isEmpty();

	@Override
	boolean add(Atom atom);

	@Override
	boolean addAll(Iterable<? extends Atom> atoms);

	@Override
	boolean remove(Atom atom);

	@Override
	boolean removeAll(Iterable<? extends Atom> atoms);

	@Override
	void clear();

}
