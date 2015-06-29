/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
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

import org.apache.commons.lang3.tuple.Pair;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.lirmm.graphik.graal.backward_chaining.pure.AggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.AggregSingleRuleOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.BasicAggregAllRulesOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.RewritingOperator;
import fr.lirmm.graphik.graal.backward_chaining.pure.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.filter.FilterIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@Parameters(commandDescription = "Rewrite given queries")
class RewriteCommand extends PureCommand {
	
	public static final String NAME = "rewrite";
	
	private DlgpWriter writer;

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(description = "<DLGP ontology file>", required = true)
	private List<String> ontologyFile = new LinkedList<String>();

	@Parameter(names = { "-q", "--queries" }, description = "The queries to rewrite in DLGP", required = true)
	private String query = null;

	@Parameter(names = { "-c", "--compilation" }, description = "The compilation file or the compilation type H, ID, NONE")
	private String compilationString = "ID";

	@Parameter(names = { "-o", "--operator" }, description = "Rewriting operator SRA, ARA, ARAM")
	private String operator = "SRA";
	
	@Parameter(names = { "-u", "--unfold" }, description = "Enable unfolding")
	private boolean isUnfoldingEnable = false;

	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	public RewriteCommand(DlgpWriter writer) {
		this.writer = writer;
	}
	
	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param commander
	 * @throws PureException
	 * @throws IOException
	 */
	public void run(JCommander commander) throws PureException, IOException {
		if (this.help) {
			commander.usage(NAME);
			System.exit(0);
		}

		Pair<LinkedList<Prefix>, RuleSet> onto = Util
				.parseOntology(this.ontologyFile.get(0));

		RewritingOperator operator = selectOperator();
		operator.setProfiler(this.getProfiler());

		RulesCompilation compilation = null;
		File compilationFile = new File(this.compilationString);
		if (compilationFile.exists()) {
			compilation = Util.loadCompilation(compilationFile,
 onto.getRight()
.iterator()).getRight();
			compilation.setProfiler(this.getProfiler());
		} else {
			compilation = Util.selectCompilationType(this.compilationString);
			compilation.setProfiler(this.getProfiler());
			compilation.compile(onto.getRight().iterator());
		}

		writer.write(onto.getLeft());
		this.processQuery(onto.getRight(), compilation, operator);
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
					new DlgpParser(file), new ConjunctiveQueryFilter());
			while (it.hasNext()) {
				queries.add(it.next());
			}
		} else {
			queries.add(DlgpParser.parseQuery(this.query));
		}

		for (ConjunctiveQuery query : queries) {
			if (this.isVerbose()) {
				this.getProfiler().clear();
				this.getProfiler().add("Initial query", query);
			}
			fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter bc = new fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter(
					query, rules, compilation, operator);

			if (this.isVerbose()) {
				bc.setProfiler(this.getProfiler());
				bc.enableVerbose(true);
			}
			
			bc.enableUnfolding(this.isUnfoldingEnable);

			try {
				writer.writeComment("rewrite of: "
						+ DlgpWriter.writeToString(query).replace("\n", ""));
				while (bc.hasNext()) {
					writer.write(bc.next());
				}
			} catch (IOException e) {
			}

		}
		try {
			writer.close();
		} catch (IOException e) {
		}
	}


}
