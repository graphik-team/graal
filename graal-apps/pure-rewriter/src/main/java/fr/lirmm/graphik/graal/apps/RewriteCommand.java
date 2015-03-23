/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.lirmm.graphik.graal.backward_chaining.pure.AggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.AggregSingleRuleOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.BasicAggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.RewritingOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.NoCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Parser;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Writer;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.stream.FilterIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@Parameters(commandDescription = "Rewrite queries")
class RewriteCommand {
	
	public static final String NAME = "rewrite";
	
	private Dlgp1Writer writer;
	private Profiler profiler;
	private boolean isVerbose;

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(names = { "-o", "--ontology" }, description = "DLP ontology file", required = true)
	private String ontologyFile = "";

	@Parameter(names = { "-q", "--query" }, description = "The queries to rewrite in DLP", required = true)
	private String query = null;

	@Parameter(names = { "-c", "--compilationFile" }, description = "The compilation file")
	private String compilationFile = "";

	@Parameter(names = { "-t", "--compilationType" }, description = "Compilation type H, ID, NONE", required = false)
	private String compilationType = "ID";

	@Parameter(names = { "-p", "--operator" }, description = "Rewriting operator SRA, ARA, ARAM", required = false)
	private String operator = "SRA";
	
	@Parameter(names = { "-u", "--unfolding" }, description = "Enable unfolding", required = false)
	private boolean isUnfoldingEnable = false;

	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	public RewriteCommand(Profiler profiler, Dlgp1Writer writer, boolean isVerbose) {
		this.profiler = profiler;
		this.writer = writer;
		this.isVerbose = isVerbose;
	}
	
	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param commander
	 * @throws FileNotFoundException
	 */
	public void run(JCommander commander) throws FileNotFoundException {
		if (this.help) {
			commander.usage(NAME);
			System.exit(0);
		}

		RuleSet rules = Util.parseOntology(this.ontologyFile);
		RulesCompilation compilation = selectCompilationType();
		RewritingOperator operator = selectOperator();

		compilation.setProfiler(profiler);
		operator.setProfiler(profiler);
		
		if (this.compilationFile.isEmpty()) {
			compilation.compile(rules.iterator());
		} else {
			compilation.load(rules.iterator(), new FilterIterator<Object, Rule>(new Dlgp1Parser(
					new File(this.compilationFile)), new RulesFilter()));
		}
	
		this.processQuery(rules, compilation, operator);
	}
	
	private RulesCompilation selectCompilationType() {
		RulesCompilation compilation = null;
		if ("H".equals(this.compilationType)) {
			compilation = new HierarchicalCompilation();
		} else if ("ID".equals(this.compilationType)) {
			compilation = new IDCompilation();
		} else {
			compilation = new NoCompilation();
		}
		return compilation;
	}

	private RewritingOperator selectOperator() {
		RewritingOperator operator = null;	
		if("SRA".equals(this.operator)) {
			operator = new AggregSingleRuleOperator();
		} else if ("ARAM".equals(this.operator)) {
			operator = new AggregAllRulesOperator();
		} else {
			operator = new BasicAggregAllRulesOperator();
		}
		return operator;
	}
	
	private void processQuery(RuleSet rules, RulesCompilation compilation, RewritingOperator operator)
			throws FileNotFoundException {
		List<ConjunctiveQuery> queries = new LinkedList<ConjunctiveQuery>();
		File file = new File(this.query);
		if (file.exists()) {
			Iterator<ConjunctiveQuery> it = new FilterIterator<Object, ConjunctiveQuery>(
					new Dlgp1Parser(file), new ConjunctiveQueryFilter());
			while (it.hasNext()) {
				queries.add(it.next());
			}
		} else {
			queries.add(Dlgp1Parser.parseQuery(this.query));
		}

		for (ConjunctiveQuery query : queries) {
			if (isVerbose) {
				profiler.clear();
				profiler.add("Initial query", query);
			}
			fr.lirmm.graphik.graal.backward_chaining.PureRewriter bc = new fr.lirmm.graphik.graal.backward_chaining.PureRewriter(
					query, rules, compilation, operator);

			if (isVerbose) {
				bc.setProfiler(profiler);
				bc.enableVerbose(true);
			}
			
			bc.enableUnfolding(this.isUnfoldingEnable);

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

			if (isVerbose) {
				profiler.add("Number of rewritings", i);
			}
		}
	}
}
