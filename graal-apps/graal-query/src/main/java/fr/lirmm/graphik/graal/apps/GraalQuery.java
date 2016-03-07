/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.SqlUCQHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.RdbmsDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.util.Apps;
import fr.lirmm.graphik.util.DefaultProfiler;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.GIterator;

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

	private static final Profiler   PROFILER         = new DefaultProfiler(System.out);
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
		DefaultUnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries();
		while (parser.hasNext()) {
			Object o = parser.next();
			if(o instanceof ConjunctiveQuery) {
				ucq.add((ConjunctiveQuery)o);
			}
		}	
		
		if (options.verbose) {
			WRITER.write("# query union");
			WRITER.write(ucq);
		}
			
		SqlUCQHomomorphism solver = SqlUCQHomomorphism.instance();
		if (options.verbose) {
			PROFILER.start("answering time");
		}
		GIterator<Substitution> subr = solver.execute(ucq, store);
		if (options.verbose) {
			PROFILER.stop("answering time");
			WRITER.writeComment("answers");
		}

		int i = 0;
		while (subr.hasNext()) {
			++i;
			WRITER.writeComment(subr.next().toString());
		}
		
		if (options.verbose) {
			PROFILER.put("number of answer", i);
		}

	}
	
};

