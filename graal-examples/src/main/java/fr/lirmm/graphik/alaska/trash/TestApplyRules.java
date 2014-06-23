package fr.lirmm.graphik.alaska.trash;
import java.util.LinkedList;

import fr.lirmm.graphik.alaska.Alaska;
import fr.lirmm.graphik.alaska.chase.DefaultChase;
import fr.lirmm.graphik.alaska.chase.Chase;
import fr.lirmm.graphik.alaska.chase.ChaseException;
import fr.lirmm.graphik.alaska.solver.SolverException;
import fr.lirmm.graphik.alaska.solver.SolverFactoryException;
import fr.lirmm.graphik.alaska.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.alaska.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.alaska.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.kb.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.kb.atomset.graph.MemoryGraphAtomSet;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.kb.core.DefaultAtom;
import fr.lirmm.graphik.kb.core.LinkedListRuleSet;
import fr.lirmm.graphik.kb.core.Predicate;
import fr.lirmm.graphik.kb.core.Query;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.kb.core.RuleSet;
import fr.lirmm.graphik.kb.core.Substitution;
import fr.lirmm.graphik.kb.core.Term;
import fr.lirmm.graphik.kb.exception.AtomSetException;
import fr.lirmm.graphik.kb.stream.SubstitutionReader;
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

		Alaska.executeChase(atomSet, ruleSet);
	

		System.out.println("#####################");
		for(Atom a : atomSet) {
			System.out.println(a);
		}

		Query query = DlgpParser.parseQuery("?(X) :- p(X,Y),q(X,Y).");
		
		Predicate p = new Predicate("p", 2);
		Term a = new Term("a", Term.Type.CONSTANT);
		Atom atom = new DefaultAtom(p, a, a);
		
		SubstitutionReader sub = Alaska.execute(query, atomSet);
		if(sub.hasNext()) {
			sub.next();
			System.out.println("ok");
		} else {
			System.out.println("nok");
		}
		
	}
	
	public static void applyRule(Rule rule, AtomSet atomSet) throws AtomSetException, SolverFactoryException, SolverException {
		Query query = new DefaultConjunctiveQuery(rule.getBody(), rule.getFrontier());
		SubstitutionReader reader = Alaska.execute(query, atomSet);
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

		Alaska.executeChase(atomSet, ruleSet);

		
		System.out.println("#####################");
		for(Atom a : atomSet) {
			System.out.println(a);
		}
		
		Query query = DlgpParser.parseQuery("?(X,Y) :- p(X,Y).q(X,Y).");
		if(Alaska.execute(query, atomSet).hasNext()) {
			System.out.println("ok");
		} else {
			System.out.println("nok");
		}
	}
}

