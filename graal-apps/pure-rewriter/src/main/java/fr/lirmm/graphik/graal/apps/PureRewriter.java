/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.Filter;
import fr.lirmm.graphik.util.stream.FilterReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Mélanie Konïg (LIRMM)
 * 
 */
public class PureRewriter {

	private static final String VERSION = "0.6.1-SNAPSHOT";
	private static Profiler profiler;
	private static PureRewriter options;
	private static DlgpWriter writer = new DlgpWriter();

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
	private boolean verbose = false;

	@Parameter(names = { "--version" }, description = "Print version information")
	private boolean version = false;

	@Parameter(names = { "-d", "--debug" }, description = "Enable debug mode", hidden = true)
	private boolean debug = false;

	@Parameters(separators = "=", commandDescription = "Compile an ontology")
	private static class CommandCompile {
		public static final String name = "compile";

		@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
		private boolean help;

		@Parameter(description = "<DLP ontology file>", required = true)
		private List<String> ontologyFile = new LinkedList<String>();

		@Parameter(names = { "-o", "--output" }, description = "Output file for this compilation")
		private String outputFile = null;

		/*
		 * @Parameter(names = { "-t", "--type" }, description =
		 * "Compilation type", required = false) private String compilationType
		 * = "";
		 */

		/**
		 * @param commander
		 * @throws FileNotFoundException
		 */
		public void run(JCommander commander) throws FileNotFoundException {
			if (this.help) {
				commander.usage(name);
				System.exit(0);
			}

			RuleSet rules = parseOntology(this.ontologyFile.get(0));
			RulesCompilation compilation = new IDCompilation(rules);
			compilation.setProfiler(profiler);
			compilation.compile();

			try {
				DlgpWriter w = writer;
				if (this.outputFile != null && !outputFile.isEmpty()) {
					w = new DlgpWriter(new File(this.outputFile));
				}
				w.write(compilation.getSaturation());
			} catch (IOException e) {
			}
		}
	}

	@Parameters(commandDescription = "Add file contents to the index")
	public static class CommandRewrite {
		public static final String name = "rewrite";

		@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
		private boolean help;

		@Parameter(names = { "-o", "--ontology" }, description = "DLP ontology file", required = true)
		private String ontologyFile = "";

		@Parameter(names = { "-q", "--query" }, description = "The queries to rewrite in DLP", required = true)
		private String query = null;

		@Parameter(names = { "-c", "--compilation" }, description = "The compilation file")
		private String compilationFile = "";

		/**
		 * @param commander
		 * @throws FileNotFoundException
		 */
		public void run(JCommander commander) throws FileNotFoundException {
			if (this.help) {
				commander.usage(name);
				System.exit(0);
			}

			RuleSet rules = parseOntology(this.ontologyFile);
			IDCompilation compilation;

			if (this.compilationFile.isEmpty()) {
				compilation = new IDCompilation(rules);
				compilation.setProfiler(profiler);
				compilation.compile();
			} else {
				compilation = new IDCompilation(rules);
				compilation.load(new FilterReader<Rule, Object>(new DlgpParser(
						new File(this.compilationFile)), new RulesFilter()));
			}

			this.processQuery(rules, compilation);
		}

		private void processQuery(RuleSet rules, RulesCompilation compilation)
				throws FileNotFoundException {
			List<ConjunctiveQuery> queries = new LinkedList<ConjunctiveQuery>();
			File file = new File(this.query);
			if (file.exists()) {
				for (ConjunctiveQuery q : new FilterReader<ConjunctiveQuery, Object>(
						new DlgpParser(file), new ConjunctiveQueryFilter())) {
					queries.add(q);
				}
			} else {
				queries.add(DlgpParser.parseQuery(this.query));
			}

			for (ConjunctiveQuery query : queries) {
				if (options.verbose) {
					profiler.clear();
					profiler.add("Initial query", query);
				}
				fr.lirmm.graphik.graal.backward_chaining.PureRewriter bc = new fr.lirmm.graphik.graal.backward_chaining.PureRewriter(
						query, rules, compilation);

				if (options.verbose) {
					bc.setProfiler(profiler);
					bc.enableVerbose(true);
				}

				int i = 0;
				while (bc.hasNext()) {
					try {
						writer.write(bc.next());
					} catch (IOException e) {
					}
					++i;
				}

				if (options.verbose) {
					profiler.add("Number of rewritings", i);
				}
			}
		}
	}

	public static void main(String args[]) throws Exception {

		options = new PureRewriter();
		JCommander commander = new JCommander(options);
		commander.setProgramName("java -jar PureRewriter.jar");

		CommandCompile cmdCompile = new CommandCompile();
		CommandRewrite cmdRewrite = new CommandRewrite();

		commander.addCommand(CommandCompile.name, cmdCompile);
		commander.addCommand(CommandRewrite.name, cmdRewrite);

		try {
			commander.parse(args);
		} catch (ParameterException e) {
			System.err.println("\nError: " + e.getMessage() + "\n");
			commander.usage();
			System.exit(1);
		}

		if (options.debug) {
			Thread.sleep(20000);
		}

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		if (options.version) {
			printVersion();
			System.exit(0);
		}

		if (options.verbose) {
			profiler = new Profiler(System.err);
		}

		if (commander.getParsedCommand() == null) {
			System.err.println("\nError: Expected a command.\n");
			commander.usage();
			System.exit(1);
		}

		// Main part
		try {
			switch (commander.getParsedCommand()) {
			case CommandCompile.name:
				cmdCompile.run(commander);
				break;

			case CommandRewrite.name:
				cmdRewrite.run(commander);
				break;
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * 
	 */
	private static void printVersion() {
		try {
			writer.write("Pure version " + VERSION);
			writer.flush();
		} catch (IOException e) {
		}
	}

	private static RuleSet parseOntology(String ontologyFile)
			throws FileNotFoundException {
		RuleSet rules = new LinkedListRuleSet();
		DlgpParser parser = new DlgpParser(new File(ontologyFile));
		for (Object o : parser) {
			if (o instanceof Rule) {
				rules.add((Rule) o);
			}
		}
		return rules;
	}

	private static class RulesFilter implements Filter {
		@Override
		public boolean filter(Object o) {
			return o instanceof Rule;
		}
	}

	private static class ConjunctiveQueryFilter implements Filter {
		@Override
		public boolean filter(Object o) {
			return o instanceof ConjunctiveQuery;
		}
	}

}
