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
