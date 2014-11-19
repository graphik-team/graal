/**
 * 
 */
package fr.lirmm.graphik.graal.examples;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.openrdf.rio.RDFFormat;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.parser.semanticweb.RDFParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class RDFParserExample {

	public static void main(String args[]) throws HomomorphismFactoryException,
			HomomorphismException, URISyntaxException, IOException {

		URL url = new URL("http://dbpedia.org/data/Montpellier.ntriples");
		DlgpWriter writer = new DlgpWriter(System.out);
		
		RDFParser parser = new RDFParser(url, RDFFormat.NTRIPLES);
		
		for(Atom a : parser) {
			writer.write(a);
		}
		
	}
	
}
