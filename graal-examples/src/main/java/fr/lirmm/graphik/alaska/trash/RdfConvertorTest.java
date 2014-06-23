package fr.lirmm.graphik.alaska.trash;
import java.io.FileReader;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class RdfConvertorTest {
	public static void main(String[] args)
			throws Exception
		{
			final RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
			final RDFWriter rdfWriter = Rio.createWriter(RDFFormat.RDFXML, System.out);
			
			rdfWriter.startRDF();
			rdfParser.setRDFHandler(new RDFHandlerBase() {
				@Override
				public void handleStatement(Statement st) throws RDFHandlerException {
					rdfWriter.handleStatement(st);
				}
			}
			);
			rdfParser.parse(new FileReader("/home/clement/projets/ontologies/test.owl"), "");
			rdfWriter.endRDF();
		}
}
