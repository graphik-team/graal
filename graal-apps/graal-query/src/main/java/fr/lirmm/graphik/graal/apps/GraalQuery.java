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
import fr.lirmm.graphik.graal.io.dlp.DlpParser;
import fr.lirmm.graphik.graal.io.dlp.DlpWriter;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
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
	
	@Parameter(names = { "-d", "--driver"}, description = "mysql|sqlite")
	private String driverName = "mysql";
	
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

	private static final Profiler profiler = new Profiler(System.out);
	private static final DlpWriter writer = new DlpWriter();
	
	public static void main(String[] args) throws AtomSetException, HomomorphismException, IOException {
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
		RdbmsDriver driver;
		switch (options.driver) {
			case DRIVER_SQLITE:
				driver = new SqliteDriver(options.database);
				break;
			case DRIVER_MYSQL:
				driver = new MysqlDriver(options.databaseHost, options.database, options.databaseUser, options.databasePassword);
				break;
			default:
				System.err.println("Unrecognized database driver: " + options.driver);
				System.exit(1);
		}
		RdbmsStore store = new DefaultRdbmsStore(driver);

		DlpParser parser = new DlpParser(new File(options.file));
		UnionConjunctiveQueries ucq = new UnionConjunctiveQueries();
		for(Object o : parser) {
			if(o instanceof ConjunctiveQuery) {
				ucq.add((ConjunctiveQuery)o);
			}
		}
		if (options.verbose) {
			writer.write("# query union");
			writer.write(ucq);
		}

		SqlUCQHomomorphism solver = SqlUCQHomomorphism.getInstance();
		if (options.verbose) profiler.start("answering time");
		SubstitutionReader subr = solver.execute(ucq, store);
		if (options.verbose) profiler.stop("answering time");

		if (options.verbose) writer.writeln("# answers");
		int i = 0;
		for(Substitution sub : subr) {
			++i;
			writer.writeln(sub.toString());
		}
		if (options.verbose) profiler.add("number of answer", i);
	}
	
	private GraalQuery() { }

};

