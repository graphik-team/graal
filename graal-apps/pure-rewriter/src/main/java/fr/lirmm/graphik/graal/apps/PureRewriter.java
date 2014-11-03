/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

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
 * 
 */
public class PureRewriter {

	public static Profiler timer;

	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help;

	@Parameter(names = { "-o", "--ontology" }, description = "DLP ontology file", required = true)
	private String ontologyFile = "";

	@Parameter(names = { "-q", "--query" }, description = "The query to rewrite in DLP")
	private String sQuery = "";

	@Parameter(names = { "-c", "--compilation" }, description = "The compilation file")
	private String compilationFile = "";

	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
	private boolean verbose = false;

	@Parameter(names = { "-d", "--debug" }, description = "Enable debug mode")
	private boolean debug = false;

	private static PureRewriter options;
	private static DlgpWriter writer;

	public static void main(String args[]) throws Exception {

		options = new PureRewriter();
		JCommander commander = new JCommander(options, args);
		commander.setProgramName("java -jar PureRewriter.jar");
		writer = new DlgpWriter();

		if (options.debug) {
			Thread.sleep(20000);
		}

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		RuleSet rules = new LinkedListRuleSet();
		DlgpParser parser = new DlgpParser(new File(options.ontologyFile));
		for (Object o : parser) {
			if (o instanceof Rule) {
				rules.add((Rule) o);
			}
		}

		RulesCompilation compilation;
		if (options.compilationFile.isEmpty()) {
			compilation = fr.lirmm.graphik.graal.backward_chaining.PureRewriter
					.compile(rules);
		} else {
			compilation = fr.lirmm.graphik.graal.backward_chaining.PureRewriter
					.loadCompilation(rules, new FilterReader<Rule, Object>(
							new DlgpParser(new File(options.compilationFile)),
							new RulesFilter()));
		}

		if (options.sQuery.isEmpty()) {
			compilation.save(new DlgpWriter());
		} else {
			processQuery(rules, compilation);
		}

		if (options.verbose) {
			System.out.println();
			for (String key : timer.keySet()) {
				System.out.println("info - " + key + ": " + timer.get(key)
						+ "ms");
			}

		}
	}

	private static void processQuery(RuleSet rules, RulesCompilation compilation)
			throws IOException {
		ConjunctiveQuery query = DlgpParser.parseQuery(options.sQuery);
		if (options.verbose) {
			writer.write("info - initial query: ");
			writer.write(query);
			writer.write("\n");
			writer.flush();
		}
		fr.lirmm.graphik.graal.backward_chaining.PureRewriter bc = new fr.lirmm.graphik.graal.backward_chaining.PureRewriter(
				query, rules, compilation);

		if (options.verbose) {
			timer = new Profiler();
			bc.setProfiler(timer);
			bc.enableVerbose(true);
		}

		int i = 0;
		while (bc.hasNext()) {
			writer.write(bc.next());
			++i;
		}

		if (options.verbose) {
			System.out.println("info - Number of rewritings: " + i);
		}
	}

	private static class RulesFilter implements Filter {
		@Override
		public boolean filter(Object o) {
			return true;
		}
	}

}
