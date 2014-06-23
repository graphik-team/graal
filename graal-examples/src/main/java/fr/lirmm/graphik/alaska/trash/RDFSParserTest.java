package fr.lirmm.graphik.alaska.trash;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;

import fr.lirmm.graphik.obda.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.obda.parser.semanticweb.Owl2Rules;
import fr.lirmm.graphik.obda.parser.semanticweb.RDFParser;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class RDFSParserTest {
	
    public static final String SP_ENTITY_EXPANSION_LIMIT = "entityExpansionLimit"; 
	
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		Thread.sleep(2000);
//		System.setProperty(SP_ENTITY_EXPANSION_LIMIT, "128000");
//		System.out.println(System.getProperty(SP_ENTITY_EXPANSION_LIMIT));
		URL uri = new URL("file:///home/clement/projets/ontologies/rdfSnomed.owl");
		 RDFParser parser =  new RDFParser(new InputStreamReader(uri.openStream()));
		 DlgpWriter writer = new DlgpWriter();
		 
		 int i = 0;
	       for(Object o : new Owl2Rules(parser)) {
	    	   //System.out.println("---> " + o);
	    	   if(++i %10000 == 0)
	    		   System.out.println(i);
//	    	   if(o instanceof Atom)
//	    		   writer.write((Atom)o);
//	    	   else if(o instanceof Rule)
//	    		   writer.write((Rule)o);
//	    	   else
//	    		   writer.write(" ??? " + o);
	       }
	       writer.close();
	       System.out.println("end");
	}
}
