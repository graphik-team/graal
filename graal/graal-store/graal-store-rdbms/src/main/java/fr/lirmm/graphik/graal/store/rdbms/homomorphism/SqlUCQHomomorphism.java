/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.homomorphism;

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
 * SQL homomorphism for Union Conjunctive Queries
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class SqlUCQHomomorphism implements UnionConjunctiveQueriesHomomorphism<RdbmsStore> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SqlUCQHomomorphism.class);
	
	private static SqlUCQHomomorphism instance;

	private SqlUCQHomomorphism() {
	}
	
	public static synchronized SqlUCQHomomorphism getInstance() {
		if(instance == null)
			instance = new SqlUCQHomomorphism();
		
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
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(sqlQuery);
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
