package fr.lirmm.graphik.alaska.trash;
import java.io.File;

import fr.lirmm.graphik.alaska.Alaska;
import fr.lirmm.graphik.alaska.incubator.DataGenerator;
import fr.lirmm.graphik.alaska.solver.SolverException;
import fr.lirmm.graphik.alaska.solver.SolverFactoryException;
import fr.lirmm.graphik.alaska.store.StoreException;
import fr.lirmm.graphik.alaska.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.alaska.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.kb.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.LinkedListRuleSet;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.kb.core.RuleSet;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class Main {
	public static void main(String[] args) throws StoreException, SolverFactoryException, SolverException {
		
		AtomSet atomSet = new MemoryGraphAtomSet();
		
		DlgpParser parser = new DlgpParser("");
		for(Object o : parser) {
			if(o instanceof Atom) {
				
			}
		
		}
		
		File f = new File("/tmp/test.db");
		f.delete();
		RuleSet rules = new LinkedListRuleSet();
		
		for(Object o : new DlgpParser("" +
				"q(Y1) :- p(X1, Y1), q(X1)." +
				"z(X2) :- q(X2), r(X2)." +
				"t(X3) :- r(X3)." +
				"")) {
			if(o instanceof Atom) {
				atomSet.add((Atom) o);
			} else if(o instanceof Rule) {
				rules.add((Rule)o); 
			}
		}
		
		DataGenerator dg = new DataGenerator(rules);
		dg.run();
		
		
		/*AtomSet head = new LinkedListAtomSet();
		head.add(new Basic)
		rules.add(new BasicRule(new LinkedListAtomSet(), ))*/
		
		//ConjunctiveQuery query = new ConjunctiveQuery(new LinkedListAtomSet());
		
		/*int i= 0;
		for(Substitution s : Util.execute(query, atomSet)) {
			++i;
			System.out.println(s);
		}
		System.out.println(i);*/
		
		/*Util.applyRuleSet(rules, atomSet);
		
		System.out.println("######################");
		for(Atom a : atomSet) {
			System.out.println(a);
		}*/
	}
}
