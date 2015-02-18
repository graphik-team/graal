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
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class PostgreSQLDriver extends AbstractRdbmsDriver {

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(PostgreSQLDriver.class);
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param host
	 * @param dbName
	 * @param user
	 * @param password
	 * @throws AtomSetException
	 */
	public PostgreSQLDriver(String host, String dbName, String user,
			String password)
			throws AtomSetException {
		super(openConnection(host, dbName, user, password));
	}

	private static Connection openConnection(String host, String dbName, String user,
			String password) throws AtomSetException {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://" + host
					+ "/" + dbName + "?user=" + user + "&password=" + password);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new AtomSetException(e.getMessage(), e);
		}
		return connection;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String getInsertOrIgnoreStatement(String tableName, Iterable<?> values) {
		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO ");
		query.append(tableName);
		query.append(" SELECT ");

		boolean first = true;
		for(Object value : values) {
			if(!first) {
				query.append(", ");
			}
			query.append('\'').append(value).append('\'');
			first = false;
		}
		query.append(" ");
		
		// Where not exist
		query.append("FROM (SELECT 0) AS t WHERE NOT EXISTS (SELECT 1 FROM ");
		query.append(tableName);
		query.append(" WHERE ");
		
		int i = 0;
		for(Object value : values) {
			if(i > 0) {
				query.append(" and ");
			}
			query.append("term").append(i++).append(" = ");
			query.append('\'').append(value).append('\'');
		}
		query.append("); ");
		
		return query.toString();
	}

}
