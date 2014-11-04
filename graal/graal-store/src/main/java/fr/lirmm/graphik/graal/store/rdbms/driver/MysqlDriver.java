/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.store.StoreException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class MysqlDriver extends AbstractRdbmsDriver {

	private static final Logger logger = LoggerFactory
            .getLogger(MysqlDriver.class);
	
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO ";
	
	/**
	 * 
	 * @param host
	 * @param dbName
	 * @param user
	 * @param password
	 * @throws StoreException
	 */
	public MysqlDriver(String host, String dbName, String user,
			String password)
			throws StoreException {
		super(openConnection(host, dbName, user, password));
	}

	private static Connection openConnection(String host, String dbName, String user,
			String password) throws StoreException {
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			logger.error(e.getMessage(), e);
			throw new StoreException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(), e);
			throw new StoreException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new StoreException(e.getMessage(), e);
		}

		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + host
					+ "/" + dbName + "?user=" + user + "&password=" + password);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new StoreException(e.getMessage(), e);
		}
		return connection;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	//	
	// /////////////////////////////////////////////////////////////////////////

	public String getInsertOrIgnoreStatement() {
		return INSERT_IGNORE;
	}
}
