/**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependenciesWithUnifiers;
import fr.lirmm.graphik.graal.io.dlp.DlpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RuleCompilationTest {
	
	static Predicate a = new Predicate("a", 1);
	static Predicate b = new Predicate("b", 1);
	static Predicate c = new Predicate("c", 1);
	static Predicate d = new Predicate("d", 1);

	public static void main(String args[]) throws ChaseException {
		InMemoryAtomSet atomset1 = new LinkedListAtomSet();
		InMemoryAtomSet atomset2 = new LinkedListAtomSet();
		LinkedList<Rule> rules = new LinkedList<Rule>();
		
		for(int i=0; i<9; ++i) {
			Atom atom = null;
			switch(i%4) {
			case 0:
				atom = new DefaultAtom(a, new Term(i, Term.Type.CONSTANT));
				break;
			case 1:
				atom = new DefaultAtom(b, new Term(i, Term.Type.CONSTANT));
				break;
			case 2:
				atom = new DefaultAtom(c, new Term(i, Term.Type.CONSTANT));
				break;
			default:
				atom = new DefaultAtom(d, new Term(i, Term.Type.CONSTANT));
				break;				
			}
			
			atomset1.add(atom);
			atomset2.add(atom);
		}
		
		rules.add(DlpParser.parseRule("b(X0) :- a(X0)."));
		rules.add(DlpParser.parseRule("c(X1) :- b(X1)."));
		rules.add(DlpParser.parseRule("d(X2) :- c(X2)."));
		rules.add(DlpParser.parseRule("e(X3) :- d(X3)."));
		
		rules.add(DlpParser.parseRule("c(X4) :- a(X4)."));
		rules.add(DlpParser.parseRule("d(X5) :- a(X5)."));
		rules.add(DlpParser.parseRule("e(X6) :- a(X6)."));
		
		rules.add(DlpParser.parseRule("d(X7) :- b(X7)."));
		rules.add(DlpParser.parseRule("e(X8) :- b(X8)."));
		
		rules.add(DlpParser.parseRule("e(X9) :- c(X9)."));
		
		GraphOfRuleDependenciesWithUnifiers grd = new GraphOfRuleDependenciesWithUnifiers(rules);
		for(Rule r : rules) {
			System.out.println(r);
		}
		System.out.println(grd);
		Scanner scan = new Scanner(System.in);
		scan.next();
		
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		long startTime = bean.getCurrentThreadUserTime();
		StaticChase.executeChase(atomset1, grd);
		long endTime = bean.getCurrentThreadCpuTime();
		
		System.out.println("time:" + (endTime - startTime));
		
		int count = 0;
		Iterator<Atom> it = atomset1.iterator();
		while(it.hasNext()) {
			++count;
			it.next();
		}
		System.out.println("count check: " + count);
	}
}
