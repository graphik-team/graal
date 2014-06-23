/**
 * 
 */
package fr.lirmm.graphik.alaska.store.rdbms.driver;

import java.sql.Connection;
import java.sql.Statement;

import fr.lirmm.graphik.alaska.store.StoreException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface RdbmsDriver {

	Connection getConnection();
	Statement getStatement() throws StoreException;
	
	String getInsertOrIgnoreStatement();
}
