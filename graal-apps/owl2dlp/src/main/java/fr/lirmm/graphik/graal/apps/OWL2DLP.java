/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.io.dlp.DlpWriter;
import fr.lirmm.graphik.graal.io.owl.OWLParser;
import fr.lirmm.graphik.graal.io.owl.OWLParserException;
import fr.lirmm.graphik.util.Apps;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWL2DLP {

	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

//	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
//	private boolean verbose = false;

	@Parameter(names = { "--version" }, description = "Print version information")
	private boolean version = false;

	@Parameter(names = { "-f", "--file" }, description = "OWL input file")
	private String inputFile = "";

	@Parameter(names = { "-o", "--output" }, description = "The output file")
	private String outputFile = "";

	@Parameter(names = { "-p", "--prefix" }, description = "disable prefix")
	private Boolean prefixDisable = false;

	public static void main(String args[]) throws OWLOntologyCreationException,
			IOException, OWLParserException {

		DlpWriter writer;
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

		OWLParser parser;
		if (options.inputFile.isEmpty()) {
			parser = new OWLParser(System.in);
		} else {
			parser = new OWLParser(new File(options.inputFile));
		}

		if (options.outputFile.isEmpty()) {
			writer = new DlpWriter(System.out);
		} else {
			writer = new DlpWriter(new File(options.outputFile));
		}

		parser.prefixEnable(!options.prefixDisable);

		// MAIN
		for (Object o : parser) {
			writer.write(o);
		}

		writer.close();

	}
	

}
