/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
/**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.io.FileUtils;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Assert;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.homomorphism.BacktrackHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.backjumping.GraphBaseBackJumping;
import fr.lirmm.graphik.graal.homomorphism.backjumping.NoBackJumping;
import fr.lirmm.graphik.graal.homomorphism.bbc.BCC;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.AllDomainBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.DefaultBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StarBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StatBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC0;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2WithLimit;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NoForwardChecking;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.SimpleFC;
import fr.lirmm.graphik.graal.homomorphism.scheduler.DefaultScheduler;
import fr.lirmm.graphik.graal.store.gdb.Neo4jStore;
import fr.lirmm.graphik.graal.store.rdbms.adhoc.AdHocRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.HSQLDBDriver;
import fr.lirmm.graphik.graal.store.rdbms.natural.NaturalRDBMSStore;
import fr.lirmm.graphik.graal.store.triplestore.rdf4j.RDF4jStore;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@SuppressWarnings("deprecation")
public final class TestUtil {

	private TestUtil() {
	}

	public static final String PLAIN_TABLE_RDBMS_TEST = "plainTable";
	public static final String DEFAULT_RDBMS_TEST = "default";

	public static final String NEO4J_TEST;
	static {
		File jena;
		File neo4j;
		try {
			jena = File.createTempFile("jena-test", "db");
			neo4j = File.createTempFile("neo4j-test", "db");
		} catch (IOException e) {
			throw new Error(e);
		}
		rm(neo4j);
		rm(jena);
		NEO4J_TEST = neo4j.getAbsolutePath();

	}

	public static AdHocRdbmsStore defaultRDBMSStore = null;
	public static NaturalRDBMSStore plainTableRDBMSStore = null;
	public static Neo4jStore neo4jStore = null;
	public static RDF4jStore sailStore = null;

	@SuppressWarnings({ "rawtypes" })
	public static Homomorphism[] getHomomorphisms() {

		BCC bcc0 = new BCC(true);
		BCC bcc1 = new BCC(new GraphBaseBackJumping(), true);
		BCC bcc2 = new BCC(new GraphBaseBackJumping(), true);
		BCC bcc3 = new BCC(new GraphBaseBackJumping(), true);
		BCC bcc4 = new BCC(new GraphBaseBackJumping(), true);
		BCC bcc5 = new BCC(new GraphBaseBackJumping(), true);

		return new Homomorphism[] { SmartHomomorphism.instance(), 
									// Without Optimization
		                            new BacktrackHomomorphism(DefaultScheduler.instance(), StarBootstrapper.instance(), NoForwardChecking.instance(), NoBackJumping.instance()),
		                            // BackJumping
		                            new BacktrackHomomorphism(DefaultScheduler.instance(), StarBootstrapper.instance(), NoForwardChecking.instance(), new GraphBaseBackJumping()),
		                            // BCC
		                            new BacktrackHomomorphism(bcc0.getBCCScheduler(), StarBootstrapper.instance(), NoForwardChecking.instance(), bcc0.getBCCBackJumping()),
		                            new BacktrackHomomorphism(bcc1.getBCCScheduler(), StarBootstrapper.instance(), NoForwardChecking.instance(), bcc1.getBCCBackJumping()),
		                            // Forward Checking
		                            new BacktrackHomomorphism(DefaultScheduler.instance(), StarBootstrapper.instance(),new NFC0(), new GraphBaseBackJumping()), 
		                            new BacktrackHomomorphism(DefaultScheduler.instance(), StarBootstrapper.instance(),new NFC2(), new GraphBaseBackJumping()),
		                            new BacktrackHomomorphism(DefaultScheduler.instance(), StarBootstrapper.instance(),new NFC2(true), new GraphBaseBackJumping()),
		                            new BacktrackHomomorphism(DefaultScheduler.instance(), StarBootstrapper.instance(),new SimpleFC(), new GraphBaseBackJumping()),
		                            new BacktrackHomomorphism(DefaultScheduler.instance(), StarBootstrapper.instance(),new NFC2WithLimit(8), new GraphBaseBackJumping()),
		                            // Bootstrapper
		                            new BacktrackHomomorphism(bcc2.getBCCScheduler(), StarBootstrapper.instance(), new NFC2(), bcc2.getBCCBackJumping()),
		                            new BacktrackHomomorphism(bcc3.getBCCScheduler(), StatBootstrapper.instance(), new NFC2(), bcc3.getBCCBackJumping()),
		                            new BacktrackHomomorphism(bcc4.getBCCScheduler(), DefaultBootstrapper.instance(), new NFC2(), bcc4.getBCCBackJumping()),
		                            new BacktrackHomomorphism(bcc5.getBCCScheduler(), AllDomainBootstrapper.instance(), new NFC2(), bcc5.getBCCBackJumping()) 
		                            };
		

	}

	public static AtomSet[] getAtomSet() {

		if (neo4jStore != null) {
			neo4jStore.close();
		}

		try {
			if (defaultRDBMSStore != null) {
				defaultRDBMSStore.close();
			}

			if (plainTableRDBMSStore != null) {
				plainTableRDBMSStore.close();
			}

			defaultRDBMSStore = new AdHocRdbmsStore(new HSQLDBDriver(DEFAULT_RDBMS_TEST, null));
			plainTableRDBMSStore = new NaturalRDBMSStore(new HSQLDBDriver(PLAIN_TABLE_RDBMS_TEST, null));

			defaultRDBMSStore.clear();
			plainTableRDBMSStore.clear();

			rm(NEO4J_TEST);
			neo4jStore = new Neo4jStore(NEO4J_TEST);

			if (sailStore != null) {
				sailStore.close();
			}

			try {
				sailStore = new RDF4jStore(new SailRepository(new MemoryStore()));
			} catch (AtomSetException e) {
				Assert.assertTrue("Error while creating SailStore", false);
			}

			return new AtomSet[] { new DefaultInMemoryGraphStore(), new LinkedListAtomSet(), defaultRDBMSStore,
			                       plainTableRDBMSStore, neo4jStore, sailStore };
		} catch (SQLException e) {
			throw new Error(e);
		} catch (AtomSetException e) {
			throw new Error(e);
		}
	}

	private static void rm(String path) {
		rm(new File(path));
	}

	private static void rm(File file) {
		if (file.exists()) {
			if (file.isDirectory()) {
				try {
					FileUtils.deleteDirectory(file);
				} catch (IOException e) {
					throw new IOError(new Error("I can't delete the file " + file.getAbsolutePath(), e));
				}
			} else {
				if (!file.delete()) {
					throw new IOError(new Error("I can't delete the file " + file.getAbsolutePath()));
				}
			}
		}
	}

}
