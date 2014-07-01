/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.ResultSetSubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class SqlSolver implements Solver {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SqlSolver.class);

    private ConjunctiveQuery query;
    private RdbmsStore store;
    private String sqlQuery;

    /**
     * @param query
     * @param store
     */
    public SqlSolver(ConjunctiveQuery query, RdbmsStore store) {
        this.query = query;
        this.store = store;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.alaska.solver.ISolver#execute()
     */
    @Override
    public SubstitutionReader execute() throws SolverException {
        this.preprocessing();
        try {
            return new ResultSetSubstitutionReader(this.store, this.sqlQuery, this.query.isBoolean());
        } catch (Exception e) {
            throw new SolverException(e.getMessage(), e);
        }
    }
    
    // /////////////////////////////////////////////////////////////////////////
    //	PRIVATE METHODS
    // /////////////////////////////////////////////////////////////////////////

    private void preprocessing() throws SolverException {
        try {
            this.sqlQuery = this.store.transformToSQL(query);
            if(logger.isDebugEnabled())
            	logger.debug("GENERATED SQL QUERY: \n" + this.query + "\n" + this.sqlQuery);
        } catch (Exception e) {
            throw new SolverException("Error during query translation to SQL",
                    e);
        }
    }

}
