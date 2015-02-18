/**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;

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
