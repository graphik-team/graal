/**
 * 
 */
package fr.lirmm.graphik.graal.rdbms.store.test;

import java.sql.SQLException;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.HSQLDBDriver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class TestUtil {

	private TestUtil() {
	}

	private static final String HSQLDB_TEST = "test";
	private static DefaultRdbmsStore rdbmsStore = null;

	public static Store getStore() {
		if (rdbmsStore != null) {
			try {
				rdbmsStore.getDriver().getConnection().createStatement()
						.executeQuery("DROP SCHEMA PUBLIC CASCADE");
			} catch (SQLException e) {
				// TODO treat this exception
				e.printStackTrace();
				throw new Error("Untreated exception");
			}
			rdbmsStore.close();
		}
		try {
			rdbmsStore = new DefaultRdbmsStore(new HSQLDBDriver(HSQLDB_TEST,
					null));
		} catch (AtomSetException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		}
		return rdbmsStore;
	}

}
