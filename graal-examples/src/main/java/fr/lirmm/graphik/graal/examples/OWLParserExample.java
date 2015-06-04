/**
 * 
 */
package fr.lirmm.graphik.graal.examples;

import java.io.File;
import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWL2Parser;
import fr.lirmm.graphik.graal.io.owl.OWL2ParserException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWLParserExample {

	public static void main(String args[]) throws OWLOntologyCreationException,
			IOException, OWL2ParserException {

		DlgpWriter writer = new DlgpWriter();
		File f = new File("/tmp/00012.owl");

		OWL2Parser parser = new OWL2Parser(f);
		for(Object o : parser) {
			writer.write(o);
		}
		parser.close();
	}

}
