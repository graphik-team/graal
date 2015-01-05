/**
 * 
 */
package fr.lirmm.graphik.graal.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.rio.RDFFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWLAxiomParser;
import fr.lirmm.graphik.graal.io.owl.OWLParser;
import fr.lirmm.graphik.graal.io.owl.OWLParserException;
import fr.lirmm.graphik.graal.parser.semanticweb.RDFParser;

/**
 * @author clement
 *
 */
public class OWLParserExample {

	public static void main(String args[]) throws OWLOntologyCreationException,
			IOException, OWLParserException {

		DlgpWriter writer = new DlgpWriter();
		File f = new File("../graal/graal-io/src/test/resources/test.owl");

		OWLParser parser = new OWLParser(f);
		for(Object o : parser) {
			writer.write(o);
		}
		parser.close();
	}

}
