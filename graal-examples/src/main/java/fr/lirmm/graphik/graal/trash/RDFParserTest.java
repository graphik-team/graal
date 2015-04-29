package fr.lirmm.graphik.graal.trash;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class RDFParserTest {

	public static void main(String args[]) throws IOException, RDFParseException, RDFHandlerException {
		
		 long time = System.currentTimeMillis();
			       
	        System.out.println(System.currentTimeMillis() - time);
	    
	        System.out.println("THE END");
	}
	
	static void methodA() throws RDFParseException, RDFHandlerException, IOException{

		java.net.URL documentUrl = new URL("http://dbpedia.org/data/The_Lord_of_the_Rings.rdf");
		InputStream inputStream = documentUrl.openStream();
		
		// detect format based on file extension
		//RDFFormat format = Rio.getParserFormatForFileName(documentURL.toString());
		//RDFFormat format = Rio.getParserFormatForMIMEType(contentType);
		
		org.openrdf.rio.RDFParser rdfParser = Rio.createParser(org.openrdf.rio.RDFFormat.RDFXML);
		rdfParser.setRDFHandler(new StatementCounter());
		rdfParser.parse(inputStream, documentUrl.toString());
	}
	
	/*static void methodB() throws IOException {
		
       RDFParser parser =  new RDFParser(new FileReader("/home/clement/projets/query-rewriting/dataset/univ-bench/univ-bench.owl"));

       for(Atom a : parser)
    	   System.out.println(a);
	}*/
	
	static class StatementCounter extends RDFHandlerBase {
		 
		  private int countedStatements = 0;
		 
		  @Override
		  public void handleStatement(Statement st) {
			DefaultAtom a = new DefaultAtom(new Predicate(st.getPredicate()
					.toString(), 2), DefaultTermFactory.instance()
					.createConstant(st.getSubject().toString()),
					DefaultTermFactory.instance().createConstant(
							st.getObject().toString()));
		     System.out.println(a);
		     
		     
		  }
		 
		 public int getCountedStatements() {
		   return countedStatements;
		 }
	}
}
