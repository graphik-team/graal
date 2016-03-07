/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
package fr.lirmm.graphik.graal.store.rdbms;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.UCQHomomorphism;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * SQL homomorphism for Union Conjunctive Queries
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class SqlUCQHomomorphism implements UCQHomomorphism<RdbmsStore> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SqlUCQHomomorphism.class);
	
	private static SqlUCQHomomorphism instance;

	private Profiler                  profiler;

	private SqlUCQHomomorphism() {
	}
	
	public static synchronized SqlUCQHomomorphism instance() {
		if(instance == null)
			instance = new SqlUCQHomomorphism();
		
		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public CloseableIterator<Substitution> execute(UnionOfConjunctiveQueries queries,
			RdbmsStore store) throws HomomorphismException {
		String sqlQuery = preprocessing(queries, store);
		try {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug(sqlQuery);
			}
			return new ResultSetSubstitutionIterator(store, sqlQuery.toString(), queries.isBoolean());
		} catch (Exception e) {
			throw new HomomorphismException(e.getMessage(), e);
		}
	}

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}

	private static String preprocessing(UnionOfConjunctiveQueries queries, RdbmsStore store)
	    throws HomomorphismException {
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
