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
public class SqlConjunctiveQueriesUnionSolver implements ConjunctiveQueriesUnionSolver<RdbmsStore> {

	private static final Logger logger = LoggerFactory
			.getLogger(SqlConjunctiveQueriesUnionSolver.class);
	
	private static SqlConjunctiveQueriesUnionSolver instance;

	private SqlConjunctiveQueriesUnionSolver() {
	}
	
	public static SqlConjunctiveQueriesUnionSolver getInstance() {
		if(instance == null)
			instance = new SqlConjunctiveQueriesUnionSolver();
		
		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public SubstitutionReader execute(ConjunctiveQueriesUnion queries,
			RdbmsStore store) throws SolverException {
		String sqlQuery = preprocessing(queries, store);
		try {
			if(logger.isDebugEnabled()) {
				logger.debug(sqlQuery.toString());
			}
			return new ResultSetSubstitutionReader(store, sqlQuery.toString(), queries.isBoolean());
		} catch (Exception e) {
			throw new SolverException(e.getMessage(), e);
		}
	}

	private static String preprocessing(ConjunctiveQueriesUnion queries, RdbmsStore store) throws SolverException {
		Iterator<ConjunctiveQuery> it = queries.iterator();
		StringBuilder sqlQuery = new StringBuilder();
		try {
			if (it.hasNext()) {
				sqlQuery.append(store.transformToSQL(it.next()));
				sqlQuery.setLength(sqlQuery.length() - 1);

				while (it.hasNext()) {
					sqlQuery.append(" UNION ");
					sqlQuery.append(store.transformToSQL(it.next()));
					sqlQuery.setLength(sqlQuery.length() - 1);
				}
			}
			sqlQuery.append(';');
		} catch (Exception e) {
			throw new SolverException("Error during query translation to SQL",
					e);
		}
		return sqlQuery.toString();
	}
}
