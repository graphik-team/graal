package fr.lirmm.graphik.alaska.trash;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.Graal;
import fr.lirmm.graphik.graal.chase.Chase;
import fr.lirmm.graphik.graal.chase.ChaseException;
import fr.lirmm.graphik.graal.chase.DefaultChase;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.RuleSet;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.solver.SolverException;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.obda.io.basic.BasicParser;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;
import fr.lirmm.graphik.obda.io.dlgp.DlgpWriter;



/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class TestApplyRules {
	
	public static void main(String[] args) throws AtomSetException, SolverFactoryException, SolverException, ChaseException {
		
		AtomSet atomSet = new LinkedListAtomSet();
		
		atomSet.add(BasicParser.parse("p(X,a).q(a,a)"));

		RuleSet ruleSet = new LinkedListRuleSet();
		ruleSet.add(DlgpParser.parseRule("q(X,Y) :- p(X,Y)."));

		Graal.executeChase(atomSet, ruleSet);
	

		System.out.println("#####################");
		for(Atom a : atomSet) {
			System.out.println(a);
		}

		Query query = DlgpParser.parseQuery("?(X) :- p(X,Y),q(X,Y).");
		
		Predicate p = new Predicate("p", 2);
		Term a = new Term("a", Term.Type.CONSTANT);
		Atom atom = new DefaultAtom(p, a, a);
		
		SubstitutionReader sub = Graal.executeQuery(query, atomSet);
		if(sub.hasNext()) {
			sub.next();
			System.out.println("ok");
		} else {
			System.out.println("nok");
		}
		
	}
	
	public static void applyRule(Rule rule, AtomSet atomSet) throws AtomSetException, SolverFactoryException, SolverException {
		Query query = new DefaultConjunctiveQuery(rule.getBody(), rule.getFrontier());
		SubstitutionReader reader = Graal.executeQuery(query, atomSet);
		for(Substitution s : reader) {
			System.out.print(s);
			AtomSet tmp = substitute(s, rule.getHead());
			System.out.println(" -> " + tmp);
			atomSet.add(tmp);
		}
	}
	
	public static AtomSet substitute(Substitution s, AtomSet atomSet) {
		AtomSet newAtomSet = new LinkedListAtomSet();
		for(Atom a : atomSet) {
			newAtomSet.add(s.getSubstitut(a));
		}
		
		return newAtomSet;
	}
	
	public static void test() throws AtomSetException, SolverFactoryException, SolverException, ChaseException {
		AtomSet atomSet = new LinkedListAtomSet();
		atomSet.add(BasicParser.parse("p(X,a).q(a,a)"));

		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		ruleSet.add(DlgpParser.parseRule("q(X,Y) :- p(X,Y)."));

		Graal.executeChase(atomSet, ruleSet);

		
		System.out.println("#####################");
		for(Atom a : atomSet) {
			System.out.println(a);
		}
		
		Query query = DlgpParser.parseQuery("?(X,Y) :- p(X,Y).q(X,Y).");
		if(Graal.executeQuery(query, atomSet).hasNext()) {
			System.out.println("ok");
		} else {
			System.out.println("nok");
		}
	}
}

