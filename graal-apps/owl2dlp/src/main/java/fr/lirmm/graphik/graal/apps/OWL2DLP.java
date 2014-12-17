/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWLAxiomParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWL2DLP {
	
	private static final String VERSION = "0.6.2-SNAPSHOT";
	
	@Parameter(names = { "-h", "--help" }, description = "Print this message", help = true)
	private boolean help;

	@Parameter(names = { "-v", "--verbose" }, description = "Enable verbose mode")
	private boolean verbose = false;

	@Parameter(names = { "--version" }, description = "Print version information")
	private boolean version = false;
	
	@Parameter(names = { "-f", "--file" }, description = "OWL input file")
	private String inputFile = "";
	
	@Parameter(names = { "-o", "--output" }, description = "The output file")
	private String outputFile = "";
	
	public static void main(String args[]) throws OWLOntologyCreationException, IOException {
		
		DlgpWriter writer;
		OWL2DLP options = new OWL2DLP();
		JCommander commander = new JCommander(options, args);
		
		if (options.help) {
			commander.usage();
			System.exit(0);
		}
		
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntology onto;
		if (options.inputFile.isEmpty()) {
			onto = man.loadOntologyFromOntologyDocument(System.in);
		} else {
			onto = man.loadOntologyFromOntologyDocument(new File(options.inputFile));
		}
		
		if (options.outputFile.isEmpty()) {
			writer = new DlgpWriter(System.out);
		} else {
			writer = new DlgpWriter(new File(options.outputFile));
		}
		
		// MAIN
		
		OWLAxiomParser visitor = OWLAxiomParser.getInstance();

		for (OWLAxiom a : onto.getAxioms()) {
			Iterable<?> iterable = a.accept(visitor);
			if(iterable != null) {
				for(Object o : iterable) {
					 writer.write(o);
				}
			}
		}
			
		writer.close();
		
	}

}
