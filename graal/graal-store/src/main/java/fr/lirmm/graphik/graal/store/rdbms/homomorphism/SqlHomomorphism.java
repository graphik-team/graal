/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.homomorphism;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
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

	public static synchronized SqlHomomorphism getInstance() {
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
