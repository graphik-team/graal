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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.lirmm.graphik.graal.backward_chaining.pure.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@Parameters(commandDescription = "Unfold a pivotal UCQ")
public class UnfoldCommand extends PureCommand {

	public static final String NAME = "unfold";

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(names = { "-c", "--compilation" }, description = "The compilation file")
	private String compilationFile = "";

	@Parameter(names = { "-q", "--queries" }, description = "The queries to rewrite in DLGP", required = true)
	private String queriesString = null;

	private DlgpWriter writer;

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	public UnfoldCommand(DlgpWriter writer) {
		this.writer = writer;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	public void run(JCommander commander) throws Exception {

		if (this.help) {
			commander.usage(NAME);
			System.exit(0);
		}

		Pair<LinkedList<Prefix>, RulesCompilation> onto = Util.loadCompilation(
				new File(
				this.compilationFile), Collections.<Rule> emptyList()
				.iterator());

		if (this.isVerbose()) {
			onto.getRight().setProfiler(this.getProfiler());
		}

		List<ConjunctiveQuery> queries = Util.parseQueries(this.queriesString);

		Iterable<ConjunctiveQuery> unfold = onto.getRight().unfold(queries);

		// display
		writer.write(onto.getLeft());
		for (ConjunctiveQuery q : unfold) {
			writer.write(q);
		}
		writer.close();

	}

}
