/**
 * 
 */
package fr.lirmm.graphik.alaska.examples;

import java.io.IOException;

import fr.lirmm.graphik.graal.core.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.RuleSet;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.solver.SolverException;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;
import fr.lirmm.graphik.graal.solver.StaticSolver;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultUseCaseExample {

	public static void main(String[] args) throws ChaseException, IOException, SolverFactoryException, SolverException {
		
		// /////////////////////////////////////////////////////////////////////
		// create an atom set
		AtomSet atomSet = new MemoryGraphAtomSet();

		// add assertions into this atom set
		atomSet.add(DlgpParser.parseAtom("p(a)."));
		atomSet.add(DlgpParser.parseAtom("p(c)."));
		atomSet.add(DlgpParser.parseAtom("q(b)."));
		atomSet.add(DlgpParser.parseAtom("q(c)."));
		atomSet.add(DlgpParser.parseAtom("s(z,z)."));
		
		// /////////////////////////////////////////////////////////////////////
		// create a rule set
		RuleSet ruleSet = new LinkedListRuleSet();
		
		// add a rule into this rule set
		ruleSet.add(DlgpParser.parseRule("r(X) :- p(X), q(X)."));
		ruleSet.add(DlgpParser.parseRule("s(X, Y) :- p(X), q(Y)."));
		
		// /////////////////////////////////////////////////////////////////////
		// run saturation
		StaticChase.executeChase(atomSet, ruleSet);
		// equivalent code:
		// Chase chase = new DefaultChase(ruleSet, atomSet);
		// chase.execute();
		
		// /////////////////////////////////////////////////////////////////////
		// show result with Dlgp format
		DlgpWriter writer = new DlgpWriter(System.out);
		writer.write(atomSet);
		// equivalent code:
		// for(Atom a : atomSet) {
		//	   writer.write(a);
		// }
		
		// /////////////////////////////////////////////////////////////////////
		// execute query
		Query query = DlgpParser.parseQuery("?(X,Y) :- s(X, Y), p(X), q(Y).");
		Iterable<Substitution> subReader = StaticSolver.executeQuery(query, atomSet);
		for(Substitution s : subReader) {
			System.out.println(s);
		}
				
	}
}
