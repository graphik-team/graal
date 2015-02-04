/**
 * 
 */
package fr.lirmm.graphik.graal.store.test;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public final class TestUtil {
	
	private TestUtil(){}
	
	public static final String DB_TEST = "/tmp/test.db";
	
	public static AtomSet[] writeableStore() {
		File file = new File(DB_TEST);
		try {
			if (file.exists()) {
				if (file.delete()) {
					file.createNewFile();
				} else {
					throw new IOError(new Error("I can't delete the file " + DB_TEST));
				}

			}
			return new AtomSet[] { new DefaultRdbmsStore(new SqliteDriver(file)) };
		} catch (IOException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new IOError(e);
		} catch (DriverException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception", e);
		} catch (AtomSetException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception", e);
		}
	}
}
