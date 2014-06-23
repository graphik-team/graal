/**
 * 
 */
package fr.lirmm.graphik;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.alaska.Alaska;
import fr.lirmm.graphik.alaska.chase.Chase;
import fr.lirmm.graphik.alaska.chase.ChaseException;
import fr.lirmm.graphik.alaska.chase.ChaseWithGRD;
import fr.lirmm.graphik.alaska.chase.DefaultChase;
import fr.lirmm.graphik.alaska.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.alaska.solver.SolverException;
import fr.lirmm.graphik.alaska.solver.SolverFactoryException;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.Query;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.kb.exception.AtomSetException;
import fr.lirmm.graphik.obda.io.basic.BasicParser;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@RunWith(Theories.class)
public class ChaseTest {

	@DataPoints
	public static AtomSet[] writeableStore() {
		return TestUtil.writeableStore();
	}
	
	@Theory
	public void test1(AtomSet atomSet) throws AtomSetException, SolverFactoryException, SolverException, ChaseException {
		atomSet.add(BasicParser.parse("p(X,a).q(a,a)"));

		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		ruleSet.add(DlgpParser.parseRule("q(X,Y) :- p(X,Y)."));

		Chase chase = new DefaultChase(ruleSet, atomSet);
		chase.execute();
		
		Query query = DlgpParser.parseQuery("? :- p(X,Y),q(X,Y).");
		Assert.assertTrue(Alaska.execute(query, atomSet).hasNext());
	}
	
	@Theory
	public void restrictedChaseTest(AtomSet atomSet) throws AtomSetException, SolverFactoryException, SolverException, ChaseException {
		atomSet.add(BasicParser.parse("p(a)"));
		
		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		ruleSet.add(DlgpParser.parseRule("q(X,Z) :- p(X)."));
		ruleSet.add(DlgpParser.parseRule("r(X,Z) :- q(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("q(X,Z) :- r(X,Y)."));

		Chase chase = new DefaultChase(ruleSet, atomSet);
		chase.execute();
		
		int size = 0;
		for(Atom a : atomSet) {
			++size;
		}
		
		Assert.assertEquals(3, size);
	}
	
	@Theory
	public void restrictedChaseTestWithGrd(AtomSet atomSet) throws IOException, ChaseException {
		GraphOfRuleDependencies grd = new GraphOfRuleDependencies();
		DlgpParser parser = new DlgpParser(new File("./src/test/resources/test1.dlp"));

		for(Object o : parser) {
			System.out.println(o);
			if(o instanceof Rule) {
				grd.addRule((Rule)o);
			} else if (o instanceof Atom) {
				atomSet.add((Atom) o);
			}
		}
		
		System.out.println("#########################");
		grd.parseGrd(new BufferedReader(new FileReader("./src/test/resources/test1.grd")));
		Chase chase = new ChaseWithGRD(grd, atomSet);
		chase.execute();
		
		int size = 0;
		for(Atom a : atomSet) {
			++size;
		}
		
		Assert.assertEquals(3, size);
	}
	
//	@Theory
//	public void test2(WriteableAtomSet atomSet) throws AtomSetException {
//		atomSet.add(BasicParser.parse("p(a,Z).q(a,b)"));
//		
//		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
//		ruleSet.add(new BasicRule(BasicParser.parse("p(X,Y)"), BasicParser.parse("q(X,Y)")));
//
//		Util.applyRuleSet(ruleSet, atomSet);
//
//		int size = 0;
//		System.out.println("##################");
//		for(Atom a : atomSet) {
//			++size;
//			System.out.println(a);
//			if(a.getPredicate().getArity() == 2) {
//				System.out.println(a.getTerm(1).getType());
//			}
//		}
//		
//		//Assert.assertEquals(3, size);
//	}
	
	
}
