package fr.lirmm.graphik.alaska.store.rdbms;

import fr.lirmm.graphik.alaska.store.Store;
import fr.lirmm.graphik.alaska.store.StoreException;
import fr.lirmm.graphik.alaska.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.kb.core.ConjunctiveQuery;
import fr.lirmm.graphik.kb.core.Term;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public interface IRdbmsStore extends Store {

    /**
     * @return
     */
    RdbmsDriver getDriver();

	/**
	 * @param cquery
	 * @return
	 * @throws StoreException 
	 * @throws Exception
	 */
	String transformToSQL(ConjunctiveQuery cquery) throws StoreException;

	/**
	 * @param label
	 * @return
	 * @throws StoreException
	 */
	Term getTerm(String label) throws StoreException;
    
}
