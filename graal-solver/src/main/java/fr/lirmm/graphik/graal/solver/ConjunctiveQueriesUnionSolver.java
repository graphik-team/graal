/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface ConjunctiveQueriesUnionSolver<A extends ReadOnlyAtomSet> extends Solver<ConjunctiveQueriesUnion, A> {

}
