/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileNotFoundException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.forward_chaining.Chase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.parser.ParseException;
import fr.lirmm.graphik.graal.solver.SqlUnionConjunctiveQueriesSolver;
import fr.lirmm.graphik.graal.store.StoreException;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 * Allow to perform a conjunctive query over a MySQL Atomset.
 *
 */
public class GRAALQuery {

	@Parameter(names = { "-d", "--dlp" }, description = "DLP file")
	private String file = "";
	
	@Parameter(names = { "--driver"}, description = "mysql|sqlite")
	private String driver_name = "mysql";
	
	@Parameter(names = { "--db"}, description = "database name")
	private String database = "";
	
	@Parameter(names = { "--host"}, description = "database host")
	private String database_host = "localhost";
	
	@Parameter(names = { "--user"}, description = "database user")
	private String database_user = "root";
	
	@Parameter(names = { "--password"}, description = "database password")
	private String database_password = "root";
	
	

	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help;

	
	
	public static void main(String[] args) throws StoreException, FileNotFoundException, ChaseException, ParseException, HomomorphismException {
		GRAALQuery options = new GRAALQuery();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}
		
		// Driver
		RdbmsDriver driver;
		driver = new MysqlDriver(options.database_host, options.database, options.database_user, options.database_password);
		AtomSet atomSet = new DefaultRdbmsStore(driver);
		
		Chase chase = null;
		GraphOfRuleDependencies grd = null;

		DlgpParser parser = new DlgpParser(new File(options.file));
		UnionConjunctiveQueries ucq = new UnionConjunctiveQueries();
		for(Object o : parser) {
			if(o instanceof ConjunctiveQuery) {
				ucq.add((ConjunctiveQuery)o);
			}
		}		
			
		Homomorphism solver = SqlUnionConjunctiveQueriesSolver.getInstance();
		System.out.println("querying");
		long time = System.currentTimeMillis();
		SubstitutionReader subr = solver.execute(ucq, atomSet);
		long time2 = System.currentTimeMillis();
		System.out.println("answering time: " + (time2 - time) );
		
		int i = 0;
		for(Substitution sub : subr) {
			++i;
			System.out.println(sub);
		}
		
		System.out.println("answering time: " + (time2 - time) );
		System.out.println(i + "answers");
	}
};
