/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.forward_chaining.Chase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.DefaultChase;
import fr.lirmm.graphik.graal.io.ParseException;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.util.Profiler;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 * Perform one step of a forward chaining algorithm.
 *
 */
public class OneStepForwardChaining {

	@Parameter(names = { "-f", "--dlp" }, description = "DLP file")
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
	
	

	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help;

	private static final Profiler PROFILER = new Profiler(System.out);
	
	public static void main(String[] args) throws AtomSetException, FileNotFoundException, ChaseException, ParseException {
		OneStepForwardChaining options = new OneStepForwardChaining();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}
		
		// Driver
		RdbmsDriver driver;
		driver = new MysqlDriver(options.databaseHost, options.database, options.databaseUser, options.databasePassword);
		AtomSet atomSet = new DefaultRdbmsStore(driver);
		
		DefaultChase chase = null;
		
		DlgpParser parser = new DlgpParser(new File(options.file));
		LinkedList<Rule> rules = new LinkedList<Rule>();
		for(Object o : parser) {
			if(o instanceof Rule) {
				rules.add((Rule)o);
			}
		}
			
		chase = new DefaultChase(rules, atomSet);
		chase.enableVerbose(true);
		PROFILER.start("forward chaining time");
		chase.next();
		PROFILER.stop("forward chaining time");
	}
};
