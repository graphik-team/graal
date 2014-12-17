/**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openrdf.rio.RDFFormat;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.io.owl.OWLAxiomParser;
import fr.lirmm.graphik.graal.parser.semanticweb.RDFParser;

/**
 * @author clement
 *
 */
public class OWLParser {
	
	 public static void main(String args[]) throws OWLOntologyCreationException, IOException {
		
		Boolean prefixEnable = true;
		DlgpWriter writer = new DlgpWriter(System.out);
		
		File f;
		f = new File("../graal/graal-io/src/test/resources/test2.owl");
		//f = new File("/home/clement/graphik/ontologies/U/U.owl");
		
		test(f);
		
		System.out.println("\n############################################\n");
		
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		OWLOntology onto;
		onto = man.loadOntologyFromOntologyDocument(f);
		
		OWLDocumentFormat ontologyFormat = man.getOntologyFormat(onto);
		DefaultPrefixManager pm = new DefaultPrefixManager();
		
		if(prefixEnable && ontologyFormat.isPrefixOWLOntologyFormat()) {
			PrefixDocumentFormat prefixFormat = ontologyFormat.asPrefixOWLOntologyFormat();
			
			Map<String, String> prefixMap = prefixFormat.getPrefixName2PrefixMap();
		
			Set<String> forbiddenPrefix = new TreeSet<String>();
			forbiddenPrefix.add("xml:");
			forbiddenPrefix.add("rdf:");
			forbiddenPrefix.add("rdfs:");
			forbiddenPrefix.add("owl:");
			
			for(Map.Entry<String, String> entry : prefixMap.entrySet()) {
				String prefix = entry.getKey();
				if(!forbiddenPrefix.contains(prefix)) {
					pm.setPrefix(prefix, entry.getValue());
					prefix = prefix.substring(0, prefix.length() - 1);
					writer.write("@prefix ");
					writer.write(prefix);
					writer.write(" <");
					writer.write(entry.getValue());
					writer.write(">\n");
				}
			}
			writer.write(">\n");
		}
		

		OWLAxiomParser visitor = new OWLAxiomParser(pm);
		

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
