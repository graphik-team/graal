package fr.lirmm.graphik.graal.core.atomset;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.stream.IteratorAtomReader;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 */
public class LinkedListAtomSet extends AbstractAtomSet implements
		InMemoryAtomSet, Collection<Atom> {

	private LinkedList<Atom> linkedList;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public LinkedListAtomSet() {
		this.linkedList = new LinkedList<Atom>();
	}

	public LinkedListAtomSet(LinkedList<Atom> list) {
		this.linkedList = list;
	}

	public LinkedListAtomSet(Atom... atoms) {
		this();
		for (Atom a : atoms)
			this.linkedList.add(a);
	}

	public LinkedListAtomSet(Iterable<Atom> it) {
		this();
		for (Atom a : it)
			this.linkedList.add(a);
	}

	// copy constructor
	public LinkedListAtomSet(AtomSet atomset) {
		this();
		for (Atom atom : atomset) {
			this.add(new DefaultAtom(atom));
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<Predicate> getAllPredicates() {
		Set<Predicate> predicates = new TreeSet<Predicate>();
		for (Atom a : this) {
			predicates.add(a.getPredicate());
		}
		return predicates;
	}

	@Override
	public boolean add(Atom atom) {
		if (this.linkedList.contains(atom))
			return false;

		return this.linkedList.add(atom);
	}

	@Override
	public boolean addAll(Iterable<? extends Atom> atoms) {
		boolean isChanged = false;
		for (Atom a : atoms) {
			isChanged = this.add(a) || isChanged;
		}
		return isChanged;
	}

	@Override
	public boolean addAll(Collection<? extends Atom> c) {
		return this.addAll((Iterable<? extends Atom>) c);
	}

	@Override
	public Set<Term> getTerms() {
		Set<Term> terms = new TreeSet<Term>();
		for (Atom a : this.linkedList) {
			terms.addAll(a.getTerms());
		}
		return terms;
	}

	@Override
	public Set<Term> getTerms(Term.Type type) {
		Set<Term> terms = new TreeSet<Term>();
		for (Atom a : this.linkedList) {
			terms.addAll(a.getTerms(type));
		}
		return terms;
	}

	@Override
	public boolean remove(Atom atom) {
		return this.linkedList.remove(atom);
	}

	@Override
	public boolean removeAll(Iterable<? extends Atom> atoms) {
		boolean isChanged = false;
		for (Atom a : atoms) {
			isChanged = this.linkedList.remove(a) || isChanged;
		}
		return isChanged;
	}

	@Override
	public Iterator<Atom> iterator() {
		return new IteratorAtomReader(this.linkedList.iterator());
	}

	@Override
	public String toString() {
		return this.linkedList.toString();
	}

	@Override
	public boolean isEmpty() {
		return this.linkedList.isEmpty();
	}

	@Override
	public int size() {
		return this.linkedList.size();
	}

	@Override
	public void clear() {
		this.linkedList.clear();
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof Atom)
			this.linkedList.contains((Atom) o);

		return false;
	}

	@Override
	public boolean contains(Atom atom) {
		return this.linkedList.contains(atom);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.linkedList.containsAll(c);
	}

	@Override
	public boolean remove(Object o) {
		return this.linkedList.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.linkedList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.linkedList.retainAll(c);
	}

	@Override
	public Object[] toArray() {
		return this.linkedList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] t) {
		return this.linkedList.toArray(t);
	}

};
