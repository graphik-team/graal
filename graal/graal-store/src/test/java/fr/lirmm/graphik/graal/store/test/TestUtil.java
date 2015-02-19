/**
 * 
 */
package fr.lirmm.graphik.graal.store.test;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.graal.store.gdb.BlueprintsGraphDBStore;
import fr.lirmm.graphik.graal.store.gdb.Neo4jStore;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.triplestore.JenaStore;
import fr.lirmm.graphik.graal.store.triplestore.SailStore;
import fr.lirmm.graphik.graal.store.triplestore.TripleStore;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public final class TestUtil {

	private TestUtil() {
	}

	public static final File SQLITE_TEST = new File("/tmp/sqlite-test.db");
	public static final File JENA_TEST = new File("/tmp/jena-test");
	public static final File NEO4J_TEST = new File("/tmp/neo4j-test");

	public static DefaultRdbmsStore rdbmsStore = null;
	public static BlueprintsGraphDBStore graphStore = null;
	public static JenaStore jenaStore = null;
	public static Neo4jStore neo4jStore = null;
	public static SailStore sailStore = null;

	public static Store[] getStores() {
		if (rdbmsStore != null) {
			rdbmsStore.close();
		}

		if (graphStore != null) {
			graphStore.close();
		}
		
		if (neo4jStore != null) {
			neo4jStore.close();
		}
		
		if (sailStore != null) {
			sailStore.close();
		}
		
		try {
			rm(SQLITE_TEST);
			rdbmsStore = new DefaultRdbmsStore(new SqliteDriver(SQLITE_TEST));
			graphStore = new BlueprintsGraphDBStore(new TinkerGraph());
			rm(NEO4J_TEST);
			neo4jStore = new Neo4jStore(NEO4J_TEST);

			return new Store[] { rdbmsStore, graphStore, neo4jStore };
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
			jenaStore.clear();
			jenaStore.close();
		}
		
		if (sailStore != null) {
			sailStore.close();
		}
		
		rm(JENA_TEST);
		jenaStore = new JenaStore(JENA_TEST.getAbsolutePath());
		
		try {
			sailStore = new SailStore();
		} catch (AtomSetException e) {
			Assert.assertTrue("Error while creating SailStore", false);
		}
		
		return new TripleStore[] { jenaStore, sailStore };
	}
	
	private static void rm(File file) {
		if (file.exists()) {
			if(file.isDirectory()) {
				try {
					FileUtils.deleteDirectory(file);
				} catch (IOException e) {
					throw new IOError(new Error("I can't delete the file "
							+ file.getAbsolutePath(), e));
				}
			} else {
				if (!file.delete()) {
					throw new IOError(new Error("I can't delete the file "
							+ file.getAbsolutePath()));
				}
			}
		}
	}
}
