/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;

/**
 * Class representing a conjunctive query.
 * A conjunctive query is composed of a fact and a set of answer variables.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ConjunctiveQuery extends Query, Iterable<Atom> {

	/*ReadOnly*/AtomSet getAtomSet();

	Collection<Term> getResponseVariables();

	@Override
	Iterator<Atom> iterator();
	
}
