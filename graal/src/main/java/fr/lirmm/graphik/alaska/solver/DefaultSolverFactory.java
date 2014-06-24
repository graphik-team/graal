/**
 * 
 */
package fr.lirmm.graphik.alaska.solver;

import fr.lirmm.graphik.alaska.store.rdbms.IRdbmsStore;
import fr.lirmm.graphik.alaska.transformation.ReadOnlyTransformStore;
import fr.lirmm.graphik.alaska.transformation.TransformatorSolver;
import fr.lirmm.graphik.kb.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.kb.core.ConjunctiveQuery;
import fr.lirmm.graphik.kb.core.Query;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class DefaultSolverFactory extends SolverFactory {

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
            if (atomSet instanceof IRdbmsStore)
                return new SqlSolver((ConjunctiveQuery) query,
                        (IRdbmsStore) atomSet);
            if (atomSet instanceof ReadOnlyTransformStore)
            	return new TransformatorSolver((ConjunctiveQuery) query, atomSet);
            else
                return new RecursiveBacktrackSolver((ConjunctiveQuery) query,
                        atomSet);
            
        } else if (query instanceof ConjunctiveQueriesUnion) {
            if (atomSet instanceof IRdbmsStore)
                return new SqlConjunctiveQueriesUnionSolver((ConjunctiveQueriesUnion) query, (IRdbmsStore) atomSet);
            if (atomSet instanceof ReadOnlyTransformStore)
            	return null;
            else
                return new ConjunctiveQueriesUnionSolver((ConjunctiveQueriesUnion) query, atomSet);
            
        } else {
            throw new SolverFactoryException("No solver for this kind of query");
        }
    }

}
