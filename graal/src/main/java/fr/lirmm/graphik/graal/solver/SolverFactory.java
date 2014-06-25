/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class SolverFactory {

    public static SolverFactory getFactory() {
        return new DefaultSolverFactory();
    }
    
    public abstract Solver getSolver(Query query, ReadOnlyAtomSet atomSet) throws SolverFactoryException;

}
