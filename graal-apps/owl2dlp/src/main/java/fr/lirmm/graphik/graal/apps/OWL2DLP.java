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

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;
import fr.lirmm.graphik.graal.io.owl.OWL2ParserException;
import fr.lirmm.graphik.util.Apps;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWL2DLP {

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(names = { "--version" }, description = "Print version information")
	private boolean version = false;

	@Parameter(names = { "-f", "--file" }, description = "OWL input file")
	private String inputFile = "";

	@Parameter(names = { "-o", "--output" }, description = "The output file")
	private String outputFile = "";

	@Parameter(names = { "-d", "--debug" }, description = "enable debug mode", hidden = true)
	private Boolean debugMode = false;

	public static void main(String args[]) throws OWLOntologyCreationException,
			IOException, OWL2ParserException {

		DlgpWriter writer;
		OWL2DLP options = new OWL2DLP();
		JCommander commander = new JCommander(options, args);

		if (options.help) {
			commander.usage();
			System.exit(0);
		}

		if (options.version) {
			Apps.printVersion("owl2dlp");
			System.exit(0);
		}

		if (options.debugMode) {
			((ch.qos.logback.classic.Logger) LoggerFactory
					.getLogger(OWL2Parser.class)).setLevel(Level.DEBUG);
		}

		OWL2Parser parser;
		if (options.inputFile.isEmpty()) {
			parser = new OWL2Parser(System.in);
		} else {
			parser = new OWL2Parser(new File(options.inputFile));
		}

		if (options.outputFile.isEmpty()) {
			writer = new DlgpWriter(System.out);
		} else {
			writer = new DlgpWriter(new File(options.outputFile));
		}


		// MAIN
		Object o;
		while (parser.hasNext()) {
			o = parser.next();
			if (!(o instanceof Prefix)) {
				writer.writeTopPredicate(new Predicate("top", 1));
				writer.write(o);
				break;
			}
			writer.write(o);
		}

		while (parser.hasNext()) {
			writer.write(parser.next());
		}

		writer.close();

	}
	

}
