/**
 * 
 */
package fr.lirmm.graphik.alaska.solver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.alaska.store.rdbms.IRdbmsStore;
import fr.lirmm.graphik.alaska.store.rdbms.ResultSetSubstitutionReader;
import fr.lirmm.graphik.kb.core.ConjunctiveQuery;
import fr.lirmm.graphik.kb.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class SqlSolver implements Solver {
	
	private static final Logger logger = LoggerFactory
			.getLogger(SqlSolver.class);

    private ConjunctiveQuery query;
    private IRdbmsStore store;
    private String sqlQuery;

    /**
     * @param query
     * @param store
     */
    public SqlSolver(ConjunctiveQuery query, IRdbmsStore store) {
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
