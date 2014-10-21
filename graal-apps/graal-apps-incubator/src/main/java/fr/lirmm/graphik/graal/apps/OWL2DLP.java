/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.parser.semanticweb.Owl2Rules;
import fr.lirmm.graphik.graal.parser.semanticweb.RDFParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class OWL2DLP {

	public static void main(String[] args) throws IOException {
		Reader reader = null;
		if(args.length == 0) {
			reader = new InputStreamReader(System.in);
		} else {
			reader = new FileReader(args[0]);
		}
		
		RDFParser parser = new RDFParser(reader);
		DlgpWriter writer = new DlgpWriter();
		
		writer.write(new Owl2Rules(parser));
		writer.close();
	}
}
