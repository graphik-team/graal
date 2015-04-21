/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractRdbmsDriver implements RdbmsDriver {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractRdbmsDriver.class);
	
	private final Connection DB_CONNECTION;
	protected final String INSERT_IGNORE;
	
	public AbstractRdbmsDriver(Connection connection, String insertORignore) {
		this.INSERT_IGNORE = insertORignore;
		this.DB_CONNECTION = connection;
	}
	
	@Override
	public Connection getConnection() {
		return this.DB_CONNECTION;
	}
	
	@Override
	public Statement createStatement() throws DriverException {
		try {
			return this.getConnection().createStatement();
		} catch (SQLException e) {
			throw new DriverException(e.getMessage(), e);
		}		
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}
	
	@Override
	public void close() {
		if (this.DB_CONNECTION != null) {
			try {
				this.DB_CONNECTION.rollback();
				this.DB_CONNECTION.close();
			} catch (SQLException e) {
				if(LOGGER.isWarnEnabled()) {
					LOGGER.warn("Error during closing DB connection", e);
				}
			}
			
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String getInsertOrIgnoreStatement(String tableName,
			Iterable<?> values) {
		StringBuilder sb = new StringBuilder();
		sb.append(INSERT_IGNORE).append(" ").append(tableName)
				.append(" VALUES (");
		boolean first = true;
		for (Object o : values) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append('\'').append(o.toString()).append('\'');
		}
		sb.append(");");
		return sb.toString();
	}

	@Override
	public String getInsertOrIgnoreStatement(String tableName,
			String selectQuery) {
		StringBuilder sb = new StringBuilder();
		sb.append(INSERT_IGNORE).append(' ').append(tableName).append(' ')
				.append(selectQuery);
		return sb.toString();
	}
}
