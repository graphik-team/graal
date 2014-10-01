/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.UnionConjunctiveQueriesHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.ResultSetSubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class SqlUnionConjunctiveQueriesSolver implements UnionConjunctiveQueriesHomomorphism<RdbmsStore> {

	private static final Logger logger = LoggerFactory
			.getLogger(SqlUnionConjunctiveQueriesSolver.class);
	
	private static SqlUnionConjunctiveQueriesSolver instance;

	private SqlUnionConjunctiveQueriesSolver() {
	}
	
	public static synchronized SqlUnionConjunctiveQueriesSolver getInstance() {
		if(instance == null)
			instance = new SqlUnionConjunctiveQueriesSolver();
		
		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public SubstitutionReader execute(UnionConjunctiveQueries queries,
			RdbmsStore store) throws HomomorphismException {
		String sqlQuery = preprocessing(queries, store);
		try {
			if(logger.isDebugEnabled()) {
				logger.debug(sqlQuery);
			}
			return new ResultSetSubstitutionReader(store, sqlQuery.toString(), queries.isBoolean());
		} catch (Exception e) {
			throw new HomomorphismException(e.getMessage(), e);
		}
	}

	private static String preprocessing(UnionConjunctiveQueries queries, RdbmsStore store) throws HomomorphismException {
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
			throw new HomomorphismException("Error during query translation to SQL",
					e);
		}
		return sqlQuery.toString();
	}
}
