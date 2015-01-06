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
import fr.lirmm.graphik.graal.forward_chaining.Chase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.ChaseWithGRDAndUnfiers;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependenciesWithUnifiers;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.grd.GRDParser;
import fr.lirmm.graphik.graal.parser.ParseException;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class ForwardChaining {

	@Parameter(names = { "-d", "--dlp" }, description = "DLP file")
	private String file = "";
	
	@Parameter(names = { "-g", "--grd" }, description = "GRD file")
	private String grd = "";
	
	@Parameter(names = { "--driver"}, description = "mysql|sqlite")
	private String driverName = "mysql";
	
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

	
	
	public static void main(String... args) throws StoreException, FileNotFoundException, ChaseException, ParseException {
		ForwardChaining options = new ForwardChaining();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}
		
		// Driver
		RdbmsDriver driver;
		driver = new MysqlDriver(options.databaseHost, options.database, options.databaseUser, options.databasePassword);
		AtomSet atomSet = new DefaultRdbmsStore(driver);
		
		Chase chase = null;
		GraphOfRuleDependenciesWithUnifiers grd = null;
		if(!options.grd.isEmpty()) {
			grd = GRDParser.getInstance().parse(new File(options.grd));
		} else {
			DlgpParser parser = new DlgpParser(new File(options.file));
			LinkedList<Rule> rules = new LinkedList<Rule>();
			for(Object o : parser) {
				if(o instanceof Rule) {
					rules.add((Rule)o);
				}
			}
			grd = new GraphOfRuleDependenciesWithUnifiers(rules);
		}
			
		chase = new ChaseWithGRDAndUnfiers(grd, atomSet);
		System.out.println("forward chaining");
		long time = System.currentTimeMillis();
		chase.execute();
		long time2 = System.currentTimeMillis();
		System.out.println("Forward chaining time: " + (time2 - time) );
	}
};
