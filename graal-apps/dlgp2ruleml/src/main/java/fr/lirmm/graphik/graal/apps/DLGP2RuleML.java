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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.io.dlp.Dlgp1Parser;
import fr.lirmm.graphik.graal.io.ruleml.RuleMLWriter;
import fr.lirmm.graphik.util.Apps;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DLGP2RuleML {

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

//	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
//	private boolean verbose = false;

	@Parameter(names = { "--version" }, description = "Print version information")
	private boolean version = false;

	@Parameter(names = { "-f", "--file" }, description = "DLGP input file")
	private String inputFile = "";

	@Parameter(names = { "-o", "--output" }, description = "The output file")
	private String outputFile = "";

	public static void main(String args[]) throws IOException {

		RuleMLWriter writer;
		DLGP2RuleML options = new DLGP2RuleML();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		if (options.version) {
			Apps.printVersion("dlgp2ruleml");
			System.exit(0);
		}

		Dlgp1Parser parser;
		if (options.inputFile.isEmpty()) {
			parser = new Dlgp1Parser(System.in);
		} else {
			parser = new Dlgp1Parser(new File(options.inputFile));
		}

		if (options.outputFile.isEmpty()) {
			writer = new RuleMLWriter(System.out);
		} else {
			writer = new RuleMLWriter(new File(options.outputFile));
		}

		// MAIN
		for (Object o : parser) {
			writer.write(o);
		}
		writer.close();

	}
	

}
