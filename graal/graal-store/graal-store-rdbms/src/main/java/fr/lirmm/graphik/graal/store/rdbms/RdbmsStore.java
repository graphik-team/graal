package fr.lirmm.graphik.graal.store.rdbms;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
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
	 * Transform the conjunctive query into a SQL query
	 * 
	 * @param cquery
	 * @return a string representing the generated SQL query
	 * @throws StoreException
	 * @throws Exception
	 */
	String transformToSQL(ConjunctiveQuery cquery) throws AtomSetException;

	/**
	 * Transform a rule into an "INSERT ... SELECT ..." SQL statement.
	 * 
	 * @param rangeRestrictedRule
	 *            a range restricted rule (i.e. all variables that appear in the
	 *            head also occur in the body).
	 * @return a string representing the generated SQL statement. If the rule
	 *         does not fulfill the range restricted condition the behavior is
	 *         undefined.
	 * @throws AtomSetException
	 */
	Iterator<String> transformToSQL(Rule rangeRestrictedRule)
			throws AtomSetException;

	/**
	 * @param label
	 * @return
	 * @throws StoreException
	 */
	Term getTerm(String label) throws AtomSetException;
	
	SymbolGenerator getFreeVarGen();
    
}
