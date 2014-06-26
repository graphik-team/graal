/**
 * 
 */
package fr.lirmm.graphik.graal;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.Solver;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class SolverFactory {
	
	public static SolverFactory DEFAULT_FACTORY = new SolverFactory() {
		
		@Override
		public Solver getSolver(Query query, ReadOnlyAtomSet atomSet)
				throws SolverFactoryException {
			// TODO implement this method
			throw new Error("This method isn't implemented");
		}
	};
    public static SolverFactory getFactory() {
        return new DefaultSolverFactory();
    }
    
    public abstract Solver getSolver(Query query, ReadOnlyAtomSet atomSet) throws SolverFactoryException;

}
