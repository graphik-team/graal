/**
 * 
 */
package fr.lirmm.graphik.graal.store.test;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.graal.store.gdb.BlueprintsGraphDBStore;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.triplestore.JenaStore;
import fr.lirmm.graphik.graal.store.triplestore.TripleStore;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public final class TestUtil {

	private TestUtil() {
	}

	public static final String SQLITE_TEST = "/tmp/sqlite-test.db";
	public static final String JENA_TEST = "/tmp/jena-test";

	public static DefaultRdbmsStore rdbmsStore = null;
	public static BlueprintsGraphDBStore graphStore = null;
	public static JenaStore jenaStore = null;

	public static Store[] getStores() {
		if (rdbmsStore != null) {
			rdbmsStore.close();
		}

		if (graphStore != null) {
			graphStore.close();
		}
		try {
			File sqlite = new File(SQLITE_TEST);
			if (sqlite.exists()) {
				if (!sqlite.delete()) {
					throw new IOError(new Error("I can't delete the file "
							+ SQLITE_TEST));
				}
			}
			rdbmsStore = new DefaultRdbmsStore(new SqliteDriver(sqlite));

			graphStore = new BlueprintsGraphDBStore(new TinkerGraph());

			return new Store[] { rdbmsStore, graphStore };
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

	public static TripleStore[] getTripleStores() {
		if (jenaStore != null) {
			jenaStore.close();
		}
		File jena = new File(JENA_TEST);
		if (jena.exists()) {
			try {
				FileUtils.deleteDirectory(jena);
			} catch (IOException e) {
				throw new IOError(new Error("I can't delete this directory "
						+ JENA_TEST));
			}
		}
		jenaStore = new JenaStore(JENA_TEST);
		
		return new TripleStore[] { jenaStore };
	}
}
