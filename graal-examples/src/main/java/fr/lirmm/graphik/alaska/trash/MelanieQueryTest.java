package fr.lirmm.graphik.alaska.trash;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;
import fr.lirmm.graphik.obda.io.dlgp.DlgpWriter;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class MelanieQueryTest {
	public static void main(String[] args) throws AtomSetException, IOException {
		DlgpParser parser = new DlgpParser(new FileReader("/home/clement/projets/ontologies/univ-bench.dlgp"));
		
		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		for(Object o : parser) {
			ruleSet.add((Rule) o);
		}
		
		AtomSet atomSet = new DefaultRdbmsStore(new MysqlDriver("localhost", "melanie", "root", "root"));
		//RDFParser rdfParser = new RDFParser(new FileReader("/home/clement/projets/tools/univ-bench-generator/classesUniversity0_0.owl"));
		//atomSet.add(new RDFPrefixFilter(new RDF2Atom(rdfParser),"http://swat.cse.lehigh.edu/onto/univ-bench.owl#"));
		
		int i=0;
		for(Atom a : atomSet) {
			++i;
			System.out.println(a);
		}
		System.out.println(i);
		
		DlgpWriter writer = new	DlgpWriter("/tmp/test.dlgp");
		writer.write(atomSet);
		writer.close();
		
		
	}
}
