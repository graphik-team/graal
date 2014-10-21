/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.backward_chaining.BackwardChainer;
import fr.lirmm.graphik.graal.backward_chaining.BackwardChainerListener;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class PureRewriter {

	@Parameter(names = { "-h", "--help" }, help = true)
	private boolean help;

	@Parameter(names = { "-f", "--dlp" }, description = "DLP rule file", required = true)
	private String ruleFile = "";

	@Parameter(names = { "-q", "--query" }, description = "The query to rewrite in DLP", required = true)
	private String sQuery = "";

	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
	private boolean verbose = false;

	public static void main(String args[]) throws Exception {
		
		PureRewriter options = new PureRewriter();
		JCommander commander = new JCommander(options, args);
		commander.setProgramName("java -jar PureRewriter");
		DlgpWriter writer = new DlgpWriter();

		if (options.help) {
			commander.usage();
			System.exit(0);
		}
		
		RuleSet rules = new LinkedListRuleSet();
		DlgpParser parser = new DlgpParser(new File(options.ruleFile));
		for(Object o : parser) {
			if(o instanceof Rule) {
				rules.add((Rule)o);
			}
		}
		
		ConjunctiveQuery query = DlgpParser.parseQuery(options.sQuery);
		if(options.verbose) {
			writer.write("Initial query: ");
			writer.write(query);
			writer.write("\n");
			writer.flush();
		}
		BackwardChainer bc = new fr.lirmm.graphik.graal.backward_chaining.PureRewriter(query, rules);
		
		Listener listener = null;
		if (options.verbose) {
			listener = new Listener();
			bc.addListener(listener);
		}

		int i = 0;
		while (bc.hasNext()) {
			writer.write(bc.next());
			++i;
		}

		if (options.verbose) {
			System.out.println();
			System.out.println("  Preprocessing time: "
					+ listener.getPreprocessingTime() + "ms");
			System.out.println("      Rewriting time: "
					+ listener.getRewritingTime() + "ms");
			System.out.println("          Total time: "
					+ listener.getTotalTime() + "ms");
			System.out.println("Number of rewritings: " + i);
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////

	private static class Listener implements BackwardChainerListener {

		private ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		private long preprocessingTime;
		private long rewritingTime;

		public Listener() {
		}

		private long getTime() {
			return bean.getCurrentThreadCpuTime() / 1000000;
		}

		@Override
		public void startPreprocessing() {
			this.preprocessingTime = this.getTime();
		}

		@Override
		public void endPreprocessing() {
			this.preprocessingTime = this.getTime() - this.preprocessingTime;
		}

		@Override
		public void startRewriting() {
			this.rewritingTime = this.getTime();
		}

		@Override
		public void endRewriting() {
			this.rewritingTime = this.getTime() - this.rewritingTime;
		}

		// /////////////////////////////////////////////////////////////////////////
		// GETTERS
		// /////////////////////////////////////////////////////////////////////////

		@Override
		public long getPreprocessingTime() {
			return this.preprocessingTime;
		}

		@Override
		public long getRewritingTime() {
			return this.rewritingTime;
		}

		@Override
		public long getTotalTime() {
			return this.preprocessingTime + rewritingTime;
		}

	}
}
