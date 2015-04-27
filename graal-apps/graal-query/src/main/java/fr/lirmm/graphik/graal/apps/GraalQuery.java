/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlUCQHomomorphism;
import fr.lirmm.graphik.util.Apps;
import fr.lirmm.graphik.util.Profiler;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 * Allow to perform a conjunctive query over a MySQL Atomset.
 *
 */
public class GraalQuery {

	@Parameter(names = { "-q", "--query" }, description = "DLP filepath to queries")
	private String file = "";
	
	@Parameter(names = { "-d", "--driver"}, description = DRIVER_MYSQL + "|" + DRIVER_SQLITE)
	private String driver = "mysql";
	
	@Parameter(names = { "--db"}, description = "database name (mysql), database file path (sqlite)")
	private String database = "";
	
	@Parameter(names = { "--host"}, description = "database host")
	private String databaseHost = "localhost";
	
	@Parameter(names = { "--user"}, description = "database user")
	private String databaseUser = "root";
	
	@Parameter(names = { "--password"}, description = "database password")
	private String databasePassword = "root";
	
	@Parameter(names = { "-V", "--version" }, description = "Print version information")
	private boolean version = false;

	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode (profiler and such)")
	private boolean verbose = false;
	
	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help = false;

	private static final Profiler PROFILER = new Profiler(System.out);
	private static final DlgpWriter WRITER = new DlgpWriter();

	private static final String DRIVER_SQLITE = "sqlite";

	private static final String DRIVER_MYSQL = "mysql";

	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	private GraalQuery() { }

	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	public static void main(String[] args) throws AtomSetException, HomomorphismException, IOException, DriverException {
		GraalQuery options = new GraalQuery();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		
		}

		if (options.version) {
			Apps.printVersion("graal-query");
			System.exit(0);
		}

		// Connection to the store
		RdbmsDriver driver = null;
		switch (options.driver) {
			case DRIVER_SQLITE:
				driver = new SqliteDriver(new File(options.database));
				break;
			case DRIVER_MYSQL:
				driver = new MysqlDriver(options.databaseHost, options.database, options.databaseUser, options.databasePassword);
				break;
			default:
				System.err.println("Unrecognized database driver: " + options.driver);
				System.exit(1);
		}
		RdbmsStore store = new DefaultRdbmsStore(driver);

		DlgpParser parser = new DlgpParser(new File(options.file));
		UnionConjunctiveQueries ucq = new UnionConjunctiveQueries();
		for(Object o : parser) {
			if(o instanceof ConjunctiveQuery) {
				ucq.add((ConjunctiveQuery)o);
			}
		}	
		
		if (options.verbose) {
			WRITER.write("# query union");
			WRITER.write(ucq);
		}
			
		SqlUCQHomomorphism solver = SqlUCQHomomorphism.getInstance();
		if (options.verbose) {
			PROFILER.start("answering time");
		}
		SubstitutionReader subr = solver.execute(ucq, store);
		if (options.verbose) {
			PROFILER.stop("answering time");
			WRITER.writeComment("answers");
		}

		int i = 0;
		for(Substitution sub : subr) {
			++i;
			WRITER.writeComment(sub.toString());
		}
		
		if (options.verbose) {
			PROFILER.add("number of answer", i);
		}

	}
	
};

