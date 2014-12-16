/**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.openrdf.rio.RDFFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWL2ELProfile;
import fr.lirmm.graphik.graal.io.owl.OWLAxiomParser;
import fr.lirmm.graphik.graal.parser.semanticweb.Owl2Rules;
import fr.lirmm.graphik.graal.parser.semanticweb.RDFParser;

/**
 * @author clement
 *
 */
public class OWLParser {
	
	 public static void main(String args[]) throws OWLOntologyCreationException, IOException {
		
		DlgpWriter writer = new DlgpWriter(System.out);
		
		File f;
		f = new File("../graal/graal-io/src/test/resources/test.owl");
		//f = new File("/home/clement/graphik/ontologies/U/U.owl");
		
		//test(f);
		
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntology onto;
		onto = man.loadOntologyFromOntologyDocument(f);
		

		OWLAxiomParser visitor = OWLAxiomParser.getInstance();
		

		for (OWLAxiom a : onto.getAxioms()) {
			Iterable iterable = a.accept(visitor);
			if(iterable != null) {
				for(Object o : iterable) {
					 writer.write(o);
				}
			}
		}
		
		/*OWLOntologyWalker walker = new OWLOntologyWalker(
				Collections.singleton(onto));

		walker.walkStructure(visitor);*/

	}

	
	public static void test(File f) throws FileNotFoundException {
		
		for(Object r : new RDFParser(new FileReader(f), RDFFormat.RDFXML)) {
			System.out.println(r);
		}
	}
}
