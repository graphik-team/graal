package fr.lirmm.graphik.graal.apps;

import java.io.FileReader;
import java.io.IOException;

import org.openrdf.rio.RDFFormat;

import fr.lirmm.graphik.graal.io.dlp.DlpWriter;
import fr.lirmm.graphik.graal.parser.semanticweb.RDF2Atom;
import fr.lirmm.graphik.graal.parser.semanticweb.RDFParser;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class RDF2DLP {

	private RDF2DLP(){}

	public static void main(final String[] args) throws IOException {
		if(args.length == 0)
			System.out.println("give me a RDF file path.");
		
		RDFParser parser = new RDFParser(new FileReader(args[0]), RDFFormat.RDFXML);
		DlpWriter writer = new DlpWriter();
		
		writer.write(new RDF2Atom(parser));
		writer.close();
	}
}
