/**
 * 
 */
package fr.lirmm.graphik.alaska.examples;

import java.io.IOException;

import fr.lirmm.graphik.alaska.Alaska;
import fr.lirmm.graphik.alaska.chase.ChaseException;
import fr.lirmm.graphik.kb.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.LinkedListRuleSet;
import fr.lirmm.graphik.kb.core.RuleSet;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;
import fr.lirmm.graphik.obda.io.dlgp.DlgpWriter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultUseCaseExample {

	public static void main(String[] args) throws ChaseException, IOException {
		
		// /////////////////////////////////////////////////////////////////////
		// create an atom set
		AtomSet atomSet = new MemoryGraphAtomSet();

		// add assertions into this atom set
		atomSet.add(DlgpParser.parseAtom("p(a)."));
		atomSet.add(DlgpParser.parseAtom("p(c)."));
		atomSet.add(DlgpParser.parseAtom("q(b)."));
		atomSet.add(DlgpParser.parseAtom("q(c)."));
		
		// /////////////////////////////////////////////////////////////////////
		// create a rule set
		RuleSet ruleSet = new LinkedListRuleSet();
		
		// add a rule into this rule set
		ruleSet.add(DlgpParser.parseRule("r(X) :- p(X), q(X)."));
		ruleSet.add(DlgpParser.parseRule("s(X, Y) :- p(X), q(Y)."));
		
		// /////////////////////////////////////////////////////////////////////
		// run saturation
		Alaska.executeChase(atomSet, ruleSet);
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
		writer.close();
				
	}
}
