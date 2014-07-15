/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class DefaultSolverFactory implements SolverFactory {

	private static DefaultSolverFactory instance = null;
	
	private DefaultSolverFactory(){}
	
	public static final DefaultSolverFactory getInstance() {
		if(instance == null)
			instance = new DefaultSolverFactory();
		
		return instance;
	}
    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.lirmm.graphik.alaska.solver.SolverFactory#getSolver(fr.lirmm.graphik
     * .kb.IQuery, fr.lirmm.graphik.kb.IAtomSet)
     */
    @Override
    public Solver getSolver(Query query, ReadOnlyAtomSet atomSet)
            throws SolverFactoryException {
        if (query instanceof ConjunctiveQuery) {
                return new RecursiveBacktrackSolver((ConjunctiveQuery) query,
                        atomSet);
            
        } else if (query instanceof ConjunctiveQueriesUnion) {
                return new DefaultConjunctiveQueriesUnionSolver((ConjunctiveQueriesUnion) query, atomSet);
            
        } else {
            throw new SolverFactoryException("No solver for this kind of query");
        }
    }

}
