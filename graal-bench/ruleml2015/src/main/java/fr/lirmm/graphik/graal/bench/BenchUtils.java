/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
package fr.lirmm.graphik.graal.bench;

import java.io.File;

import org.apache.commons.io.FileUtils;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class BenchUtils {
	
	private BenchUtils() {}
	
	private static final File SQLITE_UNSAT = new File("/tmp/lubm-ex-20-unsat");
	private static final File SQLITE_SEMISAT = new File("/tmp/lubm-ex-20-semisat");
	
	private static final String MYSQL_UNSAT = "jdbc:mysql://localhost/%s-unsat?user=root&password=root";
	private static final String MYSQL_SEMISAT = "jdbc:mysql://localhost/%s-semisat?user=root&password=root";
	

	
	public static Store getStoreUnsat(String system, String basename) throws AtomSetException, DriverException {
		if("SQLITE".equals(system)) {
			if(SQLITE_UNSAT.exists())
				FileUtils.deleteQuietly(SQLITE_UNSAT);
			return new DefaultRdbmsStore(new SqliteDriver(SQLITE_UNSAT));
		} else {
			return new DefaultRdbmsStore(new MysqlDriver(String.format(MYSQL_UNSAT, basename)));
		}
	}
	
	public static long sizeOfStoreUnsat() {
		return FileUtils.sizeOf(SQLITE_UNSAT);
	}
	
	public static Store getStoreSat(String system, String basename) throws AtomSetException, DriverException {
		if("SQLITE".equals(system)) {
			if(SQLITE_SEMISAT.exists())
				FileUtils.deleteQuietly(SQLITE_SEMISAT);
			return new DefaultRdbmsStore(new SqliteDriver(SQLITE_SEMISAT));
		} else {
			return new DefaultRdbmsStore(new MysqlDriver(String.format(MYSQL_SEMISAT, basename)));
		}
	}
	
	public static long sizeOfStoreSemiSat() {
		return FileUtils.sizeOf(SQLITE_SEMISAT);
	}
		
}
