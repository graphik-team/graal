package fr.lirmm.graphik.alaska.examples;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import fr.lirmm.graphik.graal.chase.Chase;
import fr.lirmm.graphik.graal.chase.ChaseException;
import fr.lirmm.graphik.graal.chase.ChaseWithGRD;
import fr.lirmm.graphik.graal.core.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;
import fr.lirmm.graphik.obda.io.dlgp.DlgpWriter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class GrdChaseExample {
	public static void main(String[] args) throws IOException, ChaseException   {
		
		GraphOfRuleDependencies grd = new GraphOfRuleDependencies();
		grd.parseGrd(new BufferedReader(new FileReader("./test-grd.grd")));
		
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
