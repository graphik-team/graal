/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
/**
* 
*/
package fr.lirmm.graphik.graal.store.rdbms.homomorphism;

import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.homomorphism.AbstractHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsConjunctiveQueryTranslator;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.util.ResultSetCloseableIterator;
import fr.lirmm.graphik.graal.store.rdbms.util.SQLQuery;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.Iterators;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;

/**
 * SQL homomorphism for Union Conjunctive Queries
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class SqlUCQHomomorphism extends AbstractHomomorphism<UnionOfConjunctiveQueries, RdbmsStore>
                                      implements Homomorphism<UnionOfConjunctiveQueries,RdbmsStore> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SqlUCQHomomorphism.class);

	private static SqlUCQHomomorphism instance;

	private SqlUCQHomomorphism() {
	}

	public static synchronized SqlUCQHomomorphism instance() {
		if (instance == null)
			instance = new SqlUCQHomomorphism();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public CloseableIterator<Substitution> execute(UnionOfConjunctiveQueries queries, RdbmsStore store, Substitution s)
	    throws HomomorphismException {
		SQLQuery sqlQuery = preprocessing(queries, store, s);
		if (this.getProfiler().isProfilingEnabled()) {
			this.getProfiler().put("SQLQuery", sqlQuery);
		}
		if (sqlQuery.hasSchemaError()) {
			return Iterators.<Substitution> emptyIterator();
		} else if (sqlQuery.isEmpty()) {
			return Iterators.<Substitution> singletonIterator(Substitutions.emptySubstitution());
		} else {
			try {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(sqlQuery.toString());
				}
				ResultSet results = store.getDriver().createStatement().executeQuery(sqlQuery.toString());
				CloseableIterator<ResultSet> resultsIt = new ResultSetCloseableIterator(results);
				return new ConverterCloseableIterator<ResultSet, Substitution>(resultsIt,
				                                                               new ResultSet2SubstitutionConverter(store.getConjunctiveQueryTranslator(),
				                                                                                                   queries.getAnswerVariables()));

			} catch (Exception e) {
				throw new HomomorphismException(e.getMessage(), e);
			}
		}
	}

	private static SQLQuery preprocessing(UnionOfConjunctiveQueries queries, RdbmsStore store, Substitution s)
	    throws HomomorphismException {
		boolean emptyQuery = false;
		CloseableIterator<ConjunctiveQuery> it = queries.iterator();
		StringBuilder ucq = new StringBuilder();
		RdbmsConjunctiveQueryTranslator translator = store.getConjunctiveQueryTranslator();

		try {
			if (!it.hasNext())
				return SQLQuery.hasSchemaErrorInstance();

			while (it.hasNext()) {
				SQLQuery query = translator.translate(it.next(), s);
				if (!query.hasSchemaError()) {
					if (ucq.length() > 0)
						ucq.append("\nUNION\n");

					ucq.append(query.toString());
				} else if (query.isEmpty()) {
					emptyQuery = true;
				}
			}
		} catch (Exception e) {
			throw new HomomorphismException("Error during query translation to SQL", e);
		}
		
		SQLQuery query = new SQLQuery(ucq.toString());
		if(query.isEmpty() && !emptyQuery) {
			return SQLQuery.hasSchemaErrorInstance();
		}

		return query;
	}

}
