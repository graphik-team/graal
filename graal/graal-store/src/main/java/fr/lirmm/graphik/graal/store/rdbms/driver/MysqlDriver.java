/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class MysqlDriver extends AbstractRdbmsDriver {

	private static final Logger LOGGER = LoggerFactory
            .getLogger(MysqlDriver.class);
	
	private static final String INSERT_IGNORE = "INSERT IGNORE INTO ";
	
	/**
	 * 
	 * @param host
	 * @param dbName
	 * @param user
	 * @param password
	 * @throws AtomSetException
	 */
	public MysqlDriver(String host, String dbName, String user,
			String password)
			throws AtomSetException {
		super(openConnection(host, dbName, user, password));
	}
	
	public MysqlDriver(String uri) throws AtomSetException {
		super(openConnection(uri));
	}

	private static Connection openConnection(String host, String dbName, String user,
			String password) throws AtomSetException {
		return openConnection("jdbc:mysql://" + host
					+ "/" + dbName + "?user=" + user + "&password=" + password);
	}
	
	private static Connection openConnection(String uri) throws AtomSetException {
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AtomSetException(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AtomSetException(e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AtomSetException(e.getMessage(), e);
		}
		
		try {
			connection = DriverManager.getConnection(uri);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AtomSetException(e.getMessage(), e);
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
