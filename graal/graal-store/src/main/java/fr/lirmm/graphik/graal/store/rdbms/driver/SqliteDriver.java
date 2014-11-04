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

import fr.lirmm.graphik.graal.store.StoreException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SqliteDriver extends AbstractRdbmsDriver {
	
	private static final Logger logger = LoggerFactory
            .getLogger(SqliteDriver.class);
	
	private static final String INSERT_IGNORE = "INSERT OR IGNORE INTO ";
	
	 /**
	  * 
	  * @param file
	  * @throws StoreException
	  */
	public SqliteDriver(File file)
			throws StoreException {
		super(openConnection(file));
	}
	 
	private static Connection openConnection(File file) throws StoreException {
		Connection connection;
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
			throw new StoreException(e.getMessage(), e);
		}
		
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + file);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			throw new StoreException(e.getMessage(), e);
		}
		
		return connection;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	//	
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String getInsertOrIgnoreStatement() {
		return INSERT_IGNORE;
	}

}
