package fr.lirmm.graphik.graal.store.rdbms;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.SymbolGenerator;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;

/**
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public interface RdbmsStore extends Store {

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
	String transformToSQL(ConjunctiveQuery cquery) throws AtomSetException;

	/**
	 * @param label
	 * @return
	 * @throws StoreException
	 */
	Term getTerm(String label) throws AtomSetException;
	
	public SymbolGenerator getFreeVarGen();
    
}
