/**
 * 
 */
package fr.lirmm.graphik.graal.store.test;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.graal.store.TripleStore;
import fr.lirmm.graphik.graal.store.gdb.BlueprintsGraphDBStore;
import fr.lirmm.graphik.graal.store.gdb.Neo4jStore;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.HSQLDBDriver;
import fr.lirmm.graphik.graal.store.triplestore.JenaStore;
import fr.lirmm.graphik.graal.store.triplestore.SailStore;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public final class TestUtil {

	private TestUtil() {
	}

	public static final String HSQLDB_TEST = "test";

	public static final File JENA_TEST;
	public static final File NEO4J_TEST;
	static {
		File jena;
		File neo4j;
		try {
			jena = File.createTempFile("jena-test", "db");
			neo4j = File.createTempFile("neo4j-test", "db");
		} catch (IOException e) {
			jena = new File("/tmp/jena-test.db");
			neo4j = new File("/tmp/neo4j-test.db");
		}
		JENA_TEST = jena;
		NEO4J_TEST = neo4j;
	}

	public static DefaultRdbmsStore rdbmsStore = null;
	public static BlueprintsGraphDBStore graphStore = null;
	public static JenaStore jenaStore = null;
	public static Neo4jStore neo4jStore = null;
	public static SailStore sailStore = null;

	public static Store[] getStores() {
		if (rdbmsStore != null) {
			try {
				rdbmsStore.getDriver().getConnection().createStatement()
						.executeQuery("DROP SCHEMA PUBLIC CASCADE");
			} catch (SQLException e) {
				// TODO treat this exception e.printStackTrace(); throw new
				throw new Error("Untreated exception");
			}
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
			rdbmsStore = new DefaultRdbmsStore(new HSQLDBDriver(HSQLDB_TEST,
					null));
			graphStore = new BlueprintsGraphDBStore(new TinkerGraph());
			rm(NEO4J_TEST);
			neo4jStore = new Neo4jStore(NEO4J_TEST);

			return new Store[] { rdbmsStore, graphStore, neo4jStore };
		} catch (AtomSetException e) { // TODO treat this exception
										// e.printStackTrace();
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
			if (file.isDirectory()) {
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
