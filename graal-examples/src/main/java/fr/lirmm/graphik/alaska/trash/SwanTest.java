package fr.lirmm.graphik.alaska.trash;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.Graal;
import fr.lirmm.graphik.graal.chase.ChaseException;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.parser.misc.BasicStringFormat;
import fr.lirmm.graphik.graal.parser.misc.StringFormat;
import fr.lirmm.graphik.graal.solver.SolverException;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;
import fr.lirmm.graphik.graal.transformation.AAtomTransformator;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SwanTest {
	
	public static class AddIndexTransformator extends AAtomTransformator {
 
		Integer i = 0;
		
		/* (non-Javadoc)
		 * @see fr.lirmm.graphik.alaska.transformation.AAtomTransformator#transform(fr.lirmm.graphik.kb.core.Atom)
		 */
		@Override
		public AtomSet transform(Atom atom) {
			Predicate p = new Predicate(atom.getPredicate().getLabel(), atom.getPredicate().getArity() + 1);
			List<Term> terms = atom.getTerms();
			LinkedList<Term> newTerms = new LinkedList(terms);
			newTerms.addFirst(new Term(++i, Type.CONSTANT));
			
			AtomSet set = new LinkedListAtomSet();
			set.add(new DefaultAtom(p, newTerms));
			return set;
		}
		
	}
	
	public static void main(String[] args) throws SolverFactoryException, SolverException, AtomSetException, ChaseException {
		/*WriteableAtomSet set = new LinkedListAtomSet();
		WriteableAtomSet indexedSet = new WriteableTransformAtomSet(set, new AddIndexTransformator());
		
		for(Object o : new DlgpParser("p(a,b),q(b,c).")) {
			indexedSet.add((Atom) o);
		}
		
		System.out.println("#########$");
		for(Atom a : set) {
			System.out.println(a);
		}
		System.out.println("#########$");
		for(Atom a : indexedSet) {
			System.out.println(a);
		}
		
		LinkedListAtomSet list = new LinkedListAtomSet();
		for(Object o : new DlgpParser("p(x,y)."))
			list.add((Atom)o);
		
		Query q = new ConjunctiveQuery(list);
		ISolver s = BasicSolverFactory.getFactory().getSolver(q, indexedSet);
		for(Substitution sub : s.execute()) {
			System.out.println(sub);
		}*/
		
		AtomSet atomSet = new DefaultRdbmsStore(new MysqlDriver("localhost", "test", "root", "root"));
		StringFormat format = new BasicStringFormat();
		atomSet.add(format.parse("p(a,i0), s(a,i1)"));

		LinkedList<Rule> ruleSet = new LinkedList<Rule>();
		DlgpParser parser = new DlgpParser("" +
				"from(I0,E),from(I1,E),to(E,I2),r(X,I2):-p(X,I0),q(X,I1)." +
				"from(I0,E),to(E,I1),q(X,I1):-s(X,I0)." +
				"conflict(C,I0),conflict(C,I1):-s(X,I0),r(X,I1)." +
				"conflict(C2,I1),conflict(C2,I2):-conflict(C,I0),conflict(C,I1),to(E,I0),from(I2,E)."
				);
		for(Object o : parser) {
			ruleSet.add((Rule)o);
		}

		Graal.executeChase(atomSet, ruleSet);

		
		System.out.println("#####################");
		for(Atom a : atomSet) {
			System.out.println(a);
		}
		
		
		
	}
	
}
