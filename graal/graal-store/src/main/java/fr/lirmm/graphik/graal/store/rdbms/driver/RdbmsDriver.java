/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.Statement;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface RdbmsDriver {

	Connection getConnection();
	Statement createStatement() throws DriverException;
	void close();
	
	String getInsertOrIgnoreStatement(String tableName, Iterable<?> values);
}
