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
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.rdf.RDFParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class RDFParserExample {

	public static void main(String args[]) throws HomomorphismFactoryException,
			HomomorphismException, URISyntaxException, IOException {

		URL url = new URL(
"file:///tmp/Levenshtein.xml");
		DlgpWriter writer = new DlgpWriter(System.out);
		
		RDFParser parser = new RDFParser(url, RDFFormat.RDFXML);
		
		int i = 0;
		for(Atom a : parser) {
			System.out.println(++i);
			writer.write(a);
		}
		writer.flush();
		
	}
	
}
