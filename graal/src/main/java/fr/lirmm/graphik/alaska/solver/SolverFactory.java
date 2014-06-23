/**
 * 
 */
package fr.lirmm.graphik.alaska.solver;

import fr.lirmm.graphik.kb.core.Query;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;

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
