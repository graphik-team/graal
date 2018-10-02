package fr.lirmm.graphik.graal.core;

import java.util.HashMap;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.BuiltInPredicate;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Olivier Rodriguez
 */
public class BuiltInAtomSetBuilder {
	private AtomSet source;
	private AtomSet atomsBuilded;
	private AtomSet btAtomsBuilded;

	/**
	 * Store references to the considered built-in predicates.
	 */
	private HashMap<Integer, BuiltInPredicate> btPredicates = new HashMap<>();

	public BuiltInAtomSetBuilder() {
	}

	public BuiltInAtomSetBuilder(AtomSet source, Iterable<BuiltInPredicate> predicates) {
		setSource(source);
		setBuiltInPredicates(predicates);
	}

	public void setSource(AtomSet source) {
		this.source = source;
	}

	public void setBuiltInPredicates(Iterable<BuiltInPredicate> predicates) {
		btPredicates.clear();

		for (BuiltInPredicate p : predicates)
			btPredicates.put(p.hashCode(), p);
	}

	/**
	 * Build the new AtomSets (atomsBuilded and btAtomsBuilded).
	 * 
	 * The process takes Atom's reference to create the new sets of atoms, he does
	 * not make a copy.
	 * 
	 * @throws IteratorException
	 * @throws AtomSetException
	 */
	public void build() throws IteratorException, AtomSetException {
		final CloseableIterator<Atom> iterator = source.iterator();
		final DefaultAtomSetFactory factory = DefaultAtomSetFactory.instance();

		atomsBuilded = factory.create();
		btAtomsBuilded = factory.create();

		while (iterator.hasNext()) {
			Atom current = iterator.next();
			int key = current.getPredicate().hashCode();

			/*
			 * Do a partition of the Built-in predicates and the others.
			 */
			if (btPredicates.containsKey(key)) {
				/*
				 * Here we are replacing simple Predicate by the BuiltInPredicate associated
				 */
				current.setPredicate(btPredicates.get(key));
				btAtomsBuilded.add(current);
			} else
				atomsBuilded.add(current);
		}
	}

	public AtomSet getSimpleAtoms() {
		return atomsBuilded;
	}

	public AtomSet getBuiltInAtoms() {
		return btAtomsBuilded;
	}
}