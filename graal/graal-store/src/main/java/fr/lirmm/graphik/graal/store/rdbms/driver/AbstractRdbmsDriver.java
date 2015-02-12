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
	
	private Connection dbConnection;
	
	public AbstractRdbmsDriver(Connection connection) {
		this.dbConnection = connection;
	}
	
	public Connection getConnection() {
		return this.dbConnection;
	}
	
	public Statement createStatement() throws DriverException {
		try {
			return this.getConnection().createStatement();
		} catch (SQLException e) {
			throw new DriverException(e.getMessage(), e);
		}		
	}
	
	@Override
	protected void finalize() {
		this.close();
	}
	
	@Override
	public void close() {
		if (this.dbConnection != null) {
			try {
				this.dbConnection.rollback();
				this.dbConnection.close();
			} catch (SQLException e) {
				if(LOGGER.isWarnEnabled()) {
					LOGGER.warn("Error during closing DB connection", e);
				}
			}
			
		}
	}
}
