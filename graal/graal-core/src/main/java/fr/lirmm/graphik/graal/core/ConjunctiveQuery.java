/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;

/**
 * Class representing a conjunctive query.
 * A conjunctive query is composed of a fact and a set of answer variables.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ConjunctiveQuery extends Query, Iterable<Atom> {

	/**
	 * Get the atom conjunction representing the query.
	 * @return an atom set representing the atom conjunction of the query.
	 */
	InMemoryAtomSet getAtomSet();

	/**
	 * Get the answer variables
	 * @return an Collection of Term representing the answer variables.
	 */
	Collection<Term> getAnswerVariables();

	/**
	 * Iterator of the atom query conjunction.
	 */
	@Override
	Iterator<Atom> iterator();
	
}
