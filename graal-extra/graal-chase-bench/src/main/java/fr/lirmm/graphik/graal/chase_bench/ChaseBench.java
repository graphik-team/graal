package fr.lirmm.graphik.graal.chase_bench;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.GraalWriter;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.chase_bench.io.ChaseBenchDataParser;
import fr.lirmm.graphik.graal.chase_bench.io.ChaseBenchQueryParser;
import fr.lirmm.graphik.graal.chase_bench.io.ChaseBenchRuleParser;
import fr.lirmm.graphik.graal.chase_bench.io.ChaseBenchWriter;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.ChaseWithGRD;
import fr.lirmm.graphik.graal.forward_chaining.ConfigurableChase;
import fr.lirmm.graphik.graal.forward_chaining.DefaultChase;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.adhoc.AdHocRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.PostgreSQLDriver;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.RealTimeProfiler;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorAdapter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class ChaseBench {

	@Parameter(names = { "-m", "--mode" }, description = "MEM|SQL InMemory or PostgreSQL")
	String mode = "MEM";

	@Parameter(names = { "--db" }, description = "database name")
	String databaseName = "";

	@Parameter(names = { "--host" }, description = "database host")
	String databaseHost = "localhost";

	@Parameter(names = { "--user" }, description = "database user")
	String databaseUser = "root";

	@Parameter(names = { "--password" }, description = "database password")
	String databasePassword = "root";

	@Parameter(names = { "-h", "--help" }, help = true)
	boolean help;

	@Parameter(names = { "--data" }, description = "Data directory path", required = true)
	String inputDataFilePath;

	@Parameter(names = { "--st-tgds" }, description = "Source to Target TGDS file path", required = true)
	String inputStTgdsFilePath;

	@Parameter(names = { "--t-tgds" }, description = "Target TGDS file path", required = true)
	String inputTargetTgdsFilePath;

	@Parameter(names = { "--queries" }, description = "queries directory path")
	String inputQueryDirPath = "";

	static String outputFilePath = "./output.txt";

	public static void main(String args[])
	    throws ChaseException, AtomSetException, IOException, HomomorphismException, SQLException {
		ChaseBench options = new ChaseBench();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		RuleSet stTgdsSet = new LinkedListRuleSet();
		RuleSet targetTgdsSet = new LinkedListRuleSet();

		Profiler prof = new RealTimeProfiler();
		prof.setOutputStream(System.out);

		// Database connection //
		// the database must exist and should be empty before running this
		// program.

		AtomSet atomSet;
		if ("SQL".equals(options.mode)) {
			// atomSet = new DefaultRdbmsStore(new PostgreSQLDriver("localhost",
			// "papotti", "clement", "clement"));
			atomSet = new AdHocRdbmsStore(new PostgreSQLDriver(options.databaseHost, options.databaseName,
			                                                     options.databaseUser, options.databasePassword));
		} else {
			// Alternatively, you can use an in memory graph based AtomSet
			atomSet = new DefaultInMemoryGraphAtomSet();
		}

		// Parsing data //
		prof.start("parsing data");
		Parser<Atom> dataParser = new ChaseBenchDataParser(new File(options.inputDataFilePath));
		prof.stop("parsing data");

		// Loading data //
		prof.start("loading data");
		atomSet.addAll(dataParser);
		prof.stop("loading data");

		// Loading rules //
		prof.start("parsing/loading st-tgds");
		Parser<Rule> ruleParser = new ChaseBenchRuleParser(new File(options.inputStTgdsFilePath));
		stTgdsSet.addAll(new IteratorAdapter<Rule>(ruleParser));
		prof.stop("parsing/loading st-tgds");

		prof.start("parsing/loading t-tgds");
		ruleParser = new ChaseBenchRuleParser(new File(options.inputTargetTgdsFilePath));
		targetTgdsSet.addAll(new IteratorAdapter<Rule>(ruleParser));
		prof.stop("parsing/loading t-tgds");

		// Applying chase //
		// The SimpleChase is a quickly optimized chase that works with the
		// attached snapshot version
		// condition.
		prof.start("st-tgds chase");
		Chase chase = new ConfigurableChase(stTgdsSet, atomSet);
		chase.next();
		prof.stop("st-tgds chase");

		prof.start("Check linearity");
		boolean isLinear = true;
		for (Rule r : targetTgdsSet) {
			if (!isLinear(r)) {
				isLinear = false;
				break;
			}
		}
		prof.stop("Check linearity");
		prof.trace("tgds set linearity: " + isLinear);

		chase = null;
		if (isLinear) {
			chase = new DefaultChase(targetTgdsSet, atomSet);
		} else {
			prof.start("Graph of Rule Dependencies computing time");
			GraphOfRuleDependencies grd = new GraphOfRuleDependencies(targetTgdsSet);
			prof.stop("Graph of Rule Dependencies computing time");
			chase = new ChaseWithGRD(grd, atomSet);
		}

		prof.start("t-tgds chase");
		chase.execute();
		prof.stop("t-tgds chase");

		// Write data //
		// Now, we can write our saturated data in a new file.
		GraalWriter writer = new ChaseBenchWriter(outputFilePath);
		writer.write(atomSet);
		writer.close();

		if (!options.inputQueryDirPath.isEmpty()) {
			prof.start("load queries");
			List<ConjunctiveQuery> queries = new LinkedList<ConjunctiveQuery>();

			File dir = new File(options.inputQueryDirPath);
			File[] files = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".txt");
				}
			});

			for (File f : files) {
				Parser<ConjunctiveQuery> queryParser = new ChaseBenchQueryParser(f);
				while (queryParser.hasNext()) {
					ConjunctiveQuery q = queryParser.next();
					q = new DefaultConjunctiveQuery(f.getName(), q.getAtomSet(), q.getAnswerVariables());
					queries.add(q);
				}
			}
			prof.stop("load queries");

			int i = 0;

			for (ConjunctiveQuery q : queries) {
				GraalWriter w = new ChaseBenchWriter(new File(q.getLabel()));
				prof.start("query " + i);
				CloseableIterator<Substitution> execute = StaticHomomorphism.instance().execute(q, atomSet);
				while (execute.hasNext()) {
					Substitution s = execute.next();
					for (Term t : q.getAnswerVariables()) {
						writeTerm(w, s.createImageOf(t));
						w.write(", ");
					}
					w.write("\n");
				}
				prof.stop("query " + i);
				w.close();
				++i;
			}
		}
	}

	protected static void writeTerm(GraalWriter w, Term t) throws IOException {
		if (Type.VARIABLE.equals(t.getType())) {
			w.write("?" + t.getIdentifier());
		} else if (Type.CONSTANT.equals(t.getType())) {
			w.write(t.getIdentifier());
		} else { // LITERAL
			writeLiteral(w, (Literal) t);
		}
	}

	protected static void writeLiteral(GraalWriter w, Literal l) throws IOException {
		if (l.getValue() instanceof String) {
			w.write('"');
			w.write(l.getValue());
			w.write('"');
		} else {
			w.write(l.getValue().toString());
		}
	}

	protected static boolean isLinear(Rule r) {
		CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
		if (it.hasNext()) {
			it.next();
			return !it.hasNext();
		} else {
			return true;
		}

	}

}
