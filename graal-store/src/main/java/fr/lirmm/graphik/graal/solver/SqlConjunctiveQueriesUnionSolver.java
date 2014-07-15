/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.ResultSetSubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class SqlConjunctiveQueriesUnionSolver implements ConjunctiveQueriesUnionSolver {

	private static final Logger logger = LoggerFactory
			.getLogger(SqlConjunctiveQueriesUnionSolver.class);
	
	private RdbmsStore store;
	private StringBuilder sqlQuery;
	private ConjunctiveQueriesUnion queries;

	public SqlConjunctiveQueriesUnionSolver(ConjunctiveQueriesUnion queries,
			RdbmsStore store) {
		this.queries = queries;
		this.store = store;
	}

	private void preprocessing() throws SolverException {
		Iterator<ConjunctiveQuery> it = queries.iterator();
		this.sqlQuery = new StringBuilder();
		try {
			if (it.hasNext()) {
				this.sqlQuery.append(this.store.transformToSQL(it.next()));
				this.sqlQuery.setLength(this.sqlQuery.length() - 1);

				while (it.hasNext()) {
					this.sqlQuery.append(" UNION ");
					this.sqlQuery.append(this.store.transformToSQL(it.next()));
					this.sqlQuery.setLength(this.sqlQuery.length() - 1);
				}
			}
			this.sqlQuery.append(';');
		} catch (Exception e) {
			throw new SolverException("Error during query translation to SQL",
					e);
		}
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
			if(logger.isDebugEnabled()) {
				logger.debug(this.sqlQuery.toString());
			}
			return new ResultSetSubstitutionReader(this.store, this.sqlQuery.toString(), queries.isBoolean());
		} catch (Exception e) {
			throw new SolverException(e.getMessage(), e);
		}
	}

}
