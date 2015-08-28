/**
 * 
 */
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface GraphAtomSet extends AtomSet {

	Iterator<AtomEdge> getAtoms(Predicate p);

	Iterator<AtomEdge> getAtoms(Term t);

}
