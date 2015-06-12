/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractInMemoryAtomSet extends AbstractAtomSet implements InMemoryAtomSet {

	@Override
	public boolean addAll(Iterator<? extends Atom> atoms) {
		boolean isChanged = false;
		while(atoms.hasNext()) {
			isChanged = this.add(atoms.next()) || isChanged;
		}
		return isChanged;
	}

	@Override
	public boolean addAll(Iterable<? extends Atom> atoms) {
		return this.addAll(atoms.iterator());
	}
	
	@Override
	public boolean removeAll(Iterator<? extends Atom> atoms) {
		boolean isChanged = false;
		while(atoms.hasNext()) {
			isChanged = this.remove(atoms.next()) || isChanged;
		}
		return isChanged;
	}
	
	@Override
	public boolean removeAll(Iterable<? extends Atom> atoms) {
		return this.removeAll(atoms.iterator());
	}
	
	@Override
	public Iterator<Predicate> predicatesIterator() {
		return this.getPredicates().iterator();
	}
	
	@Override
	public Iterator<Term> termsIterator() {
		return this.getTerms().iterator();
	}
	
	@Override
	public Iterator<Term> termsIterator(Term.Type type) {
		return this.getTerms(type).iterator();
	}
}
