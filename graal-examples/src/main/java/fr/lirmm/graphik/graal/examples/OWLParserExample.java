/**
 * 
 */
package fr.lirmm.graphik.graal.examples;

import java.io.File;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.lirmm.graphik.graal.io.dlp.DlpWriter;
import fr.lirmm.graphik.graal.io.owl.OWLParser;
import fr.lirmm.graphik.graal.io.owl.OWLParserException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLParserExample {

	public static void main(String args[]) throws OWLOntologyCreationException,
			IOException, OWLParserException {

		DlpWriter writer = new DlpWriter();
		File f = new File("../graal/graal-io/src/test/resources/test.owl");

		OWLParser parser = new OWLParser(f);
		for(Object o : parser) {
			writer.write(o);
		}
		parser.close();
	}

}
