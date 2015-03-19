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
public class GRAALQuery {

	@Parameter(names = { "-d", "--dlp" }, description = "DLP file")
	private String file = "";
	
//	@Parameter(names = { "--driver"}, description = "mysql|sqlite")
//	private String driverName = "mysql";
	
	@Parameter(names = { "--db"}, description = "database name")
	private String database = "";
	
	@Parameter(names = { "--host"}, description = "database host")
	private String databaseHost = "localhost";
	
	@Parameter(names = { "--user"}, description = "database user")
	private String databaseUser = "root";
	
	@Parameter(names = { "--password"}, description = "database password")
	private String databasePassword = "root";
	
	@Parameter(names = { "--version" }, description = "Print version information")
	private boolean version = false;
	

	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help;

	private static final Profiler profiler = new Profiler(System.out);
	private static final DlgpWriter writer = new DlgpWriter();
	
	public static void main(String[] args) throws AtomSetException, HomomorphismException, IOException {
		GRAALQuery options = new GRAALQuery();
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
		RdbmsDriver driver = new MysqlDriver(options.databaseHost, options.database, options.databaseUser, options.databasePassword);
		RdbmsStore store = new DefaultRdbmsStore(driver);

		DlgpParser parser = new DlgpParser(new File(options.file));
		UnionConjunctiveQueries ucq = new UnionConjunctiveQueries();
		for(Object o : parser) {
			if(o instanceof ConjunctiveQuery) {
				ucq.add((ConjunctiveQuery)o);
			}
		}		
		writer.write("# query union");
		writer.write(ucq);
			
		SqlUCQHomomorphism solver = SqlUCQHomomorphism.getInstance();
		profiler.start("answering time");
		SubstitutionReader subr = solver.execute(ucq, store);
		profiler.stop("answering time");
		
		writer.writeln("# answers");
		int i = 0;
		for(Substitution sub : subr) {
			++i;
			writer.writeln(sub.toString());
		}
		profiler.add("number of answer", i);

		
	}
};
