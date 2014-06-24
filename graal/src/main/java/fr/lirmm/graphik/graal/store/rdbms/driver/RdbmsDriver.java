/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.Statement;

import fr.lirmm.graphik.graal.store.StoreException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface RdbmsDriver {

	Connection getConnection();
	Statement getStatement() throws StoreException;
	
	String getInsertOrIgnoreStatement();
}
