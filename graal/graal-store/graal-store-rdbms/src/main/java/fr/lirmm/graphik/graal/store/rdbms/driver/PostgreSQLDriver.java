/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.util.MethodNotImplementedError;

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
		super(openConnection(host, dbName, user, password), "");
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
	public String getInsertOrIgnoreStatement(String tableName,
			Map<String, Object> data) {
		StringBuilder fields = new StringBuilder("(");
		StringBuilder values = new StringBuilder("");

		boolean first = true;
		for (Map.Entry<String, Object> e : data.entrySet()) {
			if (!first) {
				fields.append(", ");
				values.append(", ");
			}
			fields.append(e.getKey());
			values.append('\'').append(e.getValue()).append('\'');
			first = false;
		}
		fields.append(") ");

		StringBuilder query = new StringBuilder();
		query.append("INSERT INTO ");
		query.append(tableName);
		query.append(fields);
		query.append(" SELECT ");
		query.append(values);
		
		// Where not exist
		query.append(" FROM (SELECT 0) AS t WHERE NOT EXISTS (SELECT 1 FROM ");
		query.append(tableName);
		query.append(" WHERE ");
		
		int i = 0;
		for (Map.Entry<String, Object> e : data.entrySet()) {
			if(i > 0) {
				query.append(" and ");
			}
			query.append(e.getKey()).append(" = ");
			query.append('\'').append(e.getValue()).append('\'');
		}
		query.append("); ");
		
		return query.toString();
	}

	@Override
	public String getInsertOrIgnoreStatement(String tableName,
			String selectQuery) {
		throw new MethodNotImplementedError();
	}

}
