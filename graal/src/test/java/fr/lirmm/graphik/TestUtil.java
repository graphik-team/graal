/**
 * 
 */
package fr.lirmm.graphik;

import java.io.File;
import java.io.IOException;

import fr.lirmm.graphik.alaska.store.StoreException;
import fr.lirmm.graphik.alaska.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.alaska.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.kb.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.kb.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.kb.core.AtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class TestUtil {
	
	private static final String DB_TEST = "/tmp/test.db";
	
	public static AtomSet[] writeableStore() {
		File file = new File(DB_TEST);
		try {
			if (file.exists()) {
				if (file.delete()) {
					file.createNewFile();
				} else {
					throw new Error("I can't delete the file " + DB_TEST);
				}

			}
			return new AtomSet[] { new MemoryGraphAtomSet(),
					new LinkedListAtomSet(),
					new DefaultRdbmsStore(new SqliteDriver(file)) };
		} catch (IOException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		} catch (StoreException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		}
	}
}
