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
package fr.lirmm.graphik.graal.store.rdbms.homomorphism;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.ResultSetSubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class SqlHomomorphism implements Homomorphism<ConjunctiveQuery, RdbmsStore> {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SqlHomomorphism.class);
    
    private static SqlHomomorphism instance;

	private SqlHomomorphism() {
	}

	public static synchronized SqlHomomorphism instance() {
		if (instance == null)
			instance = new SqlHomomorphism();

		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
    /*
     * (non-Javadoc)
     * 
     * @see fr.lirmm.graphik.alaska.solver.ISolver#execute()
     */
    @Override
    public SubstitutionReader execute(ConjunctiveQuery query, RdbmsStore store) throws HomomorphismException {
        String sqlQuery = preprocessing(query, store);
        try {
            return new ResultSetSubstitutionReader(store, sqlQuery, query.isBoolean());
        } catch (Exception e) {
            throw new HomomorphismException(e.getMessage(), e);
        }
    }
    
    // /////////////////////////////////////////////////////////////////////////
    //	PRIVATE METHODS
    // /////////////////////////////////////////////////////////////////////////

    private static String preprocessing(ConjunctiveQuery query, RdbmsStore store) throws HomomorphismException {
    	String sqlQuery = null;
        try {
            sqlQuery = store.transformToSQL(query);
            if(LOGGER.isDebugEnabled())
            	LOGGER.debug("GENERATED SQL QUERY: \n" + query + "\n" + sqlQuery);
        } catch (Exception e) {
            throw new HomomorphismException("Error during query translation to SQL",
                    e);
        }
        return sqlQuery;
    }

}
