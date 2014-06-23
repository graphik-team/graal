/**
 * 
 */
package fr.lirmm.graphik.alaska.store.rdbms.driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import fr.lirmm.graphik.alaska.store.StoreException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractRdbmsDriver implements RdbmsDriver {

	private Statement statement;
	private Connection dbConnection;
	
	public AbstractRdbmsDriver(Connection connection) {
		this.dbConnection = connection;
	}
	
	public Connection getConnection() {
		return this.dbConnection;
	}
	
	public Statement getStatement() throws StoreException {
		try {
			if(this.statement == null || this.statement.isClosed())
				this.statement = this.getConnection().createStatement();
		} catch (SQLException e) {
			throw new StoreException(e.getMessage(), e);
		}
		
		return this.statement;
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
