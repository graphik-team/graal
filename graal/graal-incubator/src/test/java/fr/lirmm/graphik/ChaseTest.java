/**
 * 
 */
package fr.lirmm.graphik;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.forward_chaining.Chase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.ChaseWithGRDAndUnfiers;
import fr.lirmm.graphik.graal.forward_chaining.DefaultChase;
import fr.lirmm.graphik.graal.forward_chaining.StaticChase;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependenciesWithUnifiers;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.basic.BasicParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.grd.GRDParser;
import fr.lirmm.graphik.graal.parser.ParseException;

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
	public void test1(AtomSet atomSet) throws AtomSetException, HomomorphismFactoryException, HomomorphismException, ChaseException {
		atomSet.addAll(BasicParser.parse("p(X,a).q(a,a)"));

		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		ruleSet.add(DlgpParser.parseRule("q(X,Y) :- p(X,Y)."));

		Chase chase = new DefaultChase(ruleSet, atomSet);
		chase.execute();
		
		Query query = DlgpParser.parseQuery("? :- p(X,Y),q(X,Y).");
		Assert.assertTrue(StaticHomomorphism.executeQuery(query, atomSet).hasNext());
	}
	
	@Theory
	public void restrictedChaseTest(AtomSet atomSet) throws AtomSetException, HomomorphismFactoryException, HomomorphismException, ChaseException {
		atomSet.addAll(BasicParser.parse("p(a)"));
		
		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		ruleSet.add(DlgpParser.parseRule("q(X,Z) :- p(X)."));
		ruleSet.add(DlgpParser.parseRule("r(X,Z) :- q(X,Y)."));
		ruleSet.add(DlgpParser.parseRule("q(X,Z) :- r(X,Y)."));

		Chase chase = new DefaultChase(ruleSet, atomSet);
		chase.execute();
		
		int size = 0;
		for(Iterator<Atom> it = atomSet.iterator(); it.hasNext(); it.next()) {
			++size;
		}
		
		Assert.assertEquals(3, size);
	}
	
	@Theory
	public void restrictedChaseTestWithGrd(AtomSet atomSet) throws IOException, ChaseException, ParseException {
		GraphOfRuleDependenciesWithUnifiers grd = GRDParser.getInstance().parse(new File("./src/test/resources/test1.grd"));
		DlgpParser parser = new DlgpParser(new File("./src/test/resources/test1.dlp"));

		for(Object o : parser) {
			if (o instanceof Atom) {
				atomSet.add((Atom) o);
			}
		}
		
		System.out.println("#########################");
		System.out.println(grd.toString());
		Chase chase = new ChaseWithGRDAndUnfiers(grd, atomSet);
		chase.execute();
		
		int size = 0;
		for(Iterator<Atom> it = atomSet.iterator(); it.hasNext(); it.next()) {
			++size;
		}
		
		Assert.assertEquals(3, size);
	}
	
	@Theory
	public void test2(AtomSet atomSet) throws ChaseException, HomomorphismFactoryException, HomomorphismException {

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
		
		// /////////////////////////////////////////////////////////////////////
		// execute query
		Query query = DlgpParser.parseQuery("?(X,Y) :- s(X, Y), p(X), q(Y).");
		Iterable<Substitution> subReader = StaticHomomorphism.executeQuery(query, atomSet);
		Assert.assertTrue(subReader.iterator().hasNext());
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
