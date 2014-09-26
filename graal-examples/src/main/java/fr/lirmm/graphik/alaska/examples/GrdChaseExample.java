package fr.lirmm.graphik.alaska.examples;
import java.io.File;
import java.io.IOException;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.graal.forward_chaining.Chase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.ChaseWithGRD;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.io.grd.GRDParser;
import fr.lirmm.graphik.graal.parser.ParseException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class GrdChaseExample {
	public static void main(String[] args) throws IOException, ChaseException, ParseException   {
		
		GraphOfRuleDependencies grd = GRDParser.getInstance().parse(new File("./src/main/resources/test-grd.grd"));
		
		AtomSet facts = new MemoryGraphAtomSet();
		facts.add(DlgpParser.parseAtom("r(a)."));

		Chase chase = new ChaseWithGRD(grd, facts);
		chase.execute();
		
		System.out.println("########### SATURATED FACTS BASE ##############");
		DlgpWriter writer = new DlgpWriter();
		writer.write(facts);
		writer.close();
		System.out.println("###############################################");
		
	}
}
