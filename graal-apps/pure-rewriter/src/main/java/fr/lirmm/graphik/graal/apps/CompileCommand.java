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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.HierarchicalCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.io.RuleWriter;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Writer;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@Parameters(commandDescription = "Compile an ontology")
class CompileCommand extends PureCommand {

	public static final String NAME = "compile";

	public static final String ID_COMPILATION_NAME = "ID";
	public static final String HIERACHICAL_COMPILATION_NAME = "H";
	public static final String NO_COMPILATION_NAME = "NONE";

	private RuleWriter writer;

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(description = "<DLGP ontology file>", required = true)
	private List<String> ontologyFile = new LinkedList<String>();

	@Parameter(names = { "-f", "--file" }, description = "Output file for this compilation")
	private String outputFile = null;

	@Parameter(names = { "-t", "--type" }, description = "Compilation type "
			+ ID_COMPILATION_NAME + " or " + HIERACHICAL_COMPILATION_NAME, required = false)
	private String compilationType = "ID";

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	public CompileCommand(RuleWriter writer) {
		this.writer = writer;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * @param commander
	 * @throws IOException
	 */
	public void run(JCommander commander) throws IOException {
		if (this.help) {
			commander.usage(NAME);
			System.exit(0);
		}

		RuleSet rules = Util.parseOntology(this.ontologyFile.get(0));
		RulesCompilation compilation;
		if ("H".equals(this.compilationType)) {
			compilation = new HierarchicalCompilation();
		} else {
			compilation = new IDCompilation();
		}

		compilation.setProfiler(this.getProfiler());
		compilation.compile(rules.iterator());

		RuleWriter w = writer;
		if (this.outputFile != null && !outputFile.isEmpty()) {
			w = new Dlgp1Writer(new File(this.outputFile));
		}
		Util.writeCompilation(compilation, w);

	}
}
