/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

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
	
	@Parameter(names = { "-p", "--prefix" }, description = "disable prefix")
	private Boolean prefixDisable = false;
	
	public static void main(String args[]) throws OWLOntologyCreationException, IOException {
		
		DlgpWriter writer;
		OWL2DLP options = new OWL2DLP();
		JCommander commander = new JCommander(options, args);
		
		if (options.help) {
			commander.usage();
			System.exit(0);
		}
		
		if (options.version) {
			printVersion();
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
		
		OWLDocumentFormat ontologyFormat = man.getOntologyFormat(onto);
		DefaultPrefixManager pm = new DefaultPrefixManager();
		
		if(!options.prefixDisable && ontologyFormat.isPrefixOWLOntologyFormat()) {
			PrefixDocumentFormat prefixFormat = ontologyFormat.asPrefixOWLOntologyFormat();	
			Map<String, String> prefixMap = prefixFormat.getPrefixName2PrefixMap();
		
			Set<String> forbiddenPrefix = new TreeSet<String>();
			forbiddenPrefix.add("xml:");
			forbiddenPrefix.add("rdf:");
			forbiddenPrefix.add("rdfs:");
			forbiddenPrefix.add("owl:");
			forbiddenPrefix.add("owl2:");
			forbiddenPrefix.add("owl2xml:");
			
			for(Map.Entry<String, String> entry : prefixMap.entrySet()) {
				String prefix = entry.getKey();
				if(!forbiddenPrefix.contains(prefix)) {
					pm.setPrefix(prefix, entry.getValue());
					prefix = prefix.substring(0, prefix.length() - 1);
					writer.write("@prefix ");
					writer.write(prefix);
					writer.write(" <");
					writer.write(entry.getValue());
					writer.write(">\n");
				}
			}
			writer.write(">\n");
		}
		
		// MAIN
		
		OWLAxiomParser visitor = new OWLAxiomParser(pm);

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
	
	private static void printVersion() {
		System.out.print("owl2dlp version ");
		System.out.println(VERSION);
	}

}
