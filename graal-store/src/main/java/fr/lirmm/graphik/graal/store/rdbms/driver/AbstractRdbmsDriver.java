/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import fr.lirmm.graphik.graal.store.StoreException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractRdbmsDriver implements RdbmsDriver {

	private Connection dbConnection;
	
	public AbstractRdbmsDriver(Connection connection) {
		this.dbConnection = connection;
	}
	
	public Connection getConnection() {
		return this.dbConnection;
	}
	
	public Statement createStatement() throws StoreException {
		try {
			return this.getConnection().createStatement();
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		if (this.dbConnection != null) {
			this.dbConnection.rollback();
			this.dbConnection.close();
		}
	}
}
