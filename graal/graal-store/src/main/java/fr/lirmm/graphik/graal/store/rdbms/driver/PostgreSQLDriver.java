/**
 * 
 */
package fr.lirmm.graphik.graal.store.rdbms.driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;

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
	 * @throws StoreException
	 */
	public PostgreSQLDriver(String host, String dbName, String user,
			String password)
			throws StoreException {
		super(openConnection(host, dbName, user, password));
	}

	private static Connection openConnection(String host, String dbName, String user,
			String password) throws StoreException {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://" + host
					+ "/" + dbName + "?user=" + user + "&password=" + password);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
			throw new StoreException(e.getMessage(), e);
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
	
	public static void main(String args[]) throws StoreException, SQLException {
		RdbmsDriver driver = new PostgreSQLDriver("localhost", "test", "root", "root");
		/*driver.getConnection().setAutoCommit(false);
		driver.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS lulu (titi varchar(128));");
		driver.getConnection().commit();*/
		AtomSet atomset = new DefaultRdbmsStore(driver);
		atomset.add(DlgpParser.parseAtom("p(i,j)."));
		for(Atom a : atomset) {
			System.out.println("## -- " + a);
		}
	}

}
