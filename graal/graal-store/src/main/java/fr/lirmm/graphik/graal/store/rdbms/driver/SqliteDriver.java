/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SqliteDriver extends AbstractRdbmsDriver {
	
	private static final Logger LOGGER = LoggerFactory
            .getLogger(SqliteDriver.class);
	
	private static final String INSERT_IGNORE = "INSERT OR IGNORE INTO ";
	
	 /**
	  * 
	  * @param file
	  * @throws StoreException
	  */
	public SqliteDriver(File file)
			throws DriverException {
		super(openConnection(file));
	}
	 
	private static Connection openConnection(File file) throws DriverException {
		Connection connection;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			throw new DriverException(e.getMessage(), e);
		}
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + file);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new DriverException(e.getMessage(), e);
		}
		
		return connection;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	//	
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String getInsertOrIgnoreStatement(String tableName, Iterable<?> values) {
		StringBuilder sb = new StringBuilder();
		sb.append(INSERT_IGNORE);
		sb.append(" ").append(tableName);
		sb.append(" VALUES (");
		boolean first = true;
		for(Object o : values) {
			if(!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append('\'');
			sb.append(o.toString());
			sb.append('\'');
		}
		sb.append(");");
		return sb.toString();
	}

}
