package fr.lirmm.graphik.graal;

import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.DefaultConjunctiveQueriesUnionSolver;
import fr.lirmm.graphik.graal.solver.RecursiveBacktrackSolver;
import fr.lirmm.graphik.graal.solver.Solver;
import fr.lirmm.graphik.graal.solver.SolverFactory;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;
import fr.lirmm.graphik.graal.solver.SqlConjunctiveQueriesUnionSolver;
import fr.lirmm.graphik.graal.solver.SqlSolver;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.transformation.ReadOnlyTransformStore;
import fr.lirmm.graphik.graal.transformation.TransformatorSolver;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class GraalSolverFactory implements SolverFactory {

	private static GraalSolverFactory instance = null;
	
	private GraalSolverFactory(){}
	
	public static final GraalSolverFactory getInstance() {
		if(instance == null)
			instance = new GraalSolverFactory();
		
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
            if (atomSet instanceof RdbmsStore)
                return new SqlSolver((ConjunctiveQuery) query,
                        (RdbmsStore) atomSet);
            if (atomSet instanceof ReadOnlyTransformStore)
            	return new TransformatorSolver((ConjunctiveQuery) query, atomSet);
            else
                return new RecursiveBacktrackSolver((ConjunctiveQuery) query,
                        atomSet);
            
        } else if (query instanceof ConjunctiveQueriesUnion) {
            if (atomSet instanceof RdbmsStore)
                return new SqlConjunctiveQueriesUnionSolver((ConjunctiveQueriesUnion) query, (RdbmsStore) atomSet);
            if (atomSet instanceof ReadOnlyTransformStore)
            	return null;
            else
                return new DefaultConjunctiveQueriesUnionSolver((ConjunctiveQueriesUnion) query, atomSet);
            
        } else {
            throw new SolverFactoryException("No solver for this kind of query");
        }
    }

}
