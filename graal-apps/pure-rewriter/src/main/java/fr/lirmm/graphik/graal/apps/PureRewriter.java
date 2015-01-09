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

import fr.lirmm.graphik.graal.backward_chaining.pure.AggregSingleRuleOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.util.Apps;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.Filter;
import fr.lirmm.graphik.util.stream.FilterReader;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * @author Mélanie Konïg (LIRMM)
 * 
 */
public class PureRewriter {

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

	public static void main(String args[]) throws Exception {

		options = new PureRewriter();
		JCommander commander = new JCommander(options);
		commander.setProgramName("java -jar PureRewriter.jar");

		CommandCompile cmdCompile = new CommandCompile();
		CommandRewrite cmdRewrite = new CommandRewrite();

		commander.addCommand(CommandCompile.NAME, cmdCompile);
		commander.addCommand(CommandRewrite.NAME, cmdRewrite);

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
			Apps.printVersion("pure-rewiter");
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
			String command = commander.getParsedCommand();
			if (CommandCompile.NAME.equals(command)) {
				cmdCompile.run(commander);
			} else if (CommandRewrite.NAME.equals(command)) {
				cmdRewrite.run(commander);
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

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

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////

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

	@Parameters(separators = "=", commandDescription = "Compile an ontology")
	private static class CommandCompile {
		public static final String NAME = "compile";

		@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
		private boolean help;

		@Parameter(description = "<DLP ontology file>", required = true)
		private List<String> ontologyFile = new LinkedList<String>();

		@Parameter(names = { "-o", "--output" }, description = "Output file for this compilation")
		private String outputFile = null;

		@Parameter(names = { "-t", "--type" }, description = "Compilation type H or ID", required = false)
		private String compilationType = "ID";

		/**
		 * @param commander
		 * @throws FileNotFoundException
		 */
		public void run(JCommander commander) throws FileNotFoundException {
			if (this.help) {
				commander.usage(NAME);
				System.exit(0);
			}

			RuleSet rules = parseOntology(this.ontologyFile.get(0));
			RulesCompilation compilation;
			if ("H".equals(this.compilationType)) {
				compilation = new HierarchicalCompilation();
			} else {
				compilation = new IDCompilation();
			}

			compilation.setProfiler(profiler);
			compilation.compile(rules);

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
		public static final String NAME = "rewrite";

		@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
		private boolean help;

		@Parameter(names = { "-o", "--ontology" }, description = "DLP ontology file", required = true)
		private String ontologyFile = "";

		@Parameter(names = { "-q", "--query" }, description = "The queries to rewrite in DLP", required = true)
		private String query = null;

		@Parameter(names = { "-c", "--compilation" }, description = "The compilation file")
		private String compilationFile = "";

		@Parameter(names = { "-t", "--type" }, description = "Compilation type H, ID, NONE", required = false)
		private String compilationType = "NONE";

		/**
		 * @param commander
		 * @throws FileNotFoundException
		 */
		public void run(JCommander commander) throws FileNotFoundException {
			if (this.help) {
				commander.usage(NAME);
				System.exit(0);
			}

			RuleSet rules = parseOntology(this.ontologyFile);
			RulesCompilation compilation;

			if ("H".equals(this.compilationType)) {
				compilation = new HierarchicalCompilation();
			} else if ("ID".equals(this.compilationType)) {
				compilation = new IDCompilation();
			} else {
				compilation = new NoCompilation();
			}

			compilation.setProfiler(profiler);

			if (this.compilationFile.isEmpty()) {
				compilation.compile(rules);
			} else {
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
						query, rules, compilation, new AggregSingleRuleOperator());

				if (options.verbose) {
					bc.setProfiler(profiler);
					bc.enableVerbose(true);
				}

				int i = 0;

				try {
					writer.write("\n");
					writer.write("% rewrite of: ");
					writer.write(query);
					while (bc.hasNext()) {
						writer.write(bc.next());
						++i;
					}
				} catch (IOException e) {
				}

				if (options.verbose) {
					profiler.add("Number of rewritings", i);
				}
			}
		}
	}

}
