package fr.lirmm.graphik.graal.examples;
import java.io.File;
import java.io.IOException;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.graal.forward_chaining.Chase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.ChaseWithGRDAndUnfiers;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependenciesWithUnifiers;
import fr.lirmm.graphik.graal.io.dlp.DlpParser;
import fr.lirmm.graphik.graal.io.dlp.DlpWriter;
import fr.lirmm.graphik.graal.io.grd.GRDParser;
import fr.lirmm.graphik.graal.parser.ParseException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class GrdChaseExample {
	public static void main(String[] args) throws IOException, ChaseException, ParseException   {
		
		GraphOfRuleDependenciesWithUnifiers grd = GRDParser.getInstance().parse(new File("./src/main/resources/test-grd.grd"));
		
		InMemoryAtomSet facts = new MemoryGraphAtomSet();
		facts.add(DlpParser.parseAtom("r(a)."));

		Chase chase = new ChaseWithGRDAndUnfiers(grd, facts);
		chase.execute();
		
		System.out.println("########### SATURATED FACTS BASE ##############");
		DlpWriter writer = new DlpWriter();
		writer.write(facts);
		writer.close();
		System.out.println("###############################################");
		
	}
}
