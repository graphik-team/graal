/**
 * 
 */
package fr.lirmm.graphik.alaska;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.alaska.chase.DefaultChase;
import fr.lirmm.graphik.alaska.chase.Chase;
import fr.lirmm.graphik.alaska.chase.ChaseException;
import fr.lirmm.graphik.alaska.chase.ChaseWithGRD;
import fr.lirmm.graphik.alaska.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.alaska.solver.Solver;
import fr.lirmm.graphik.alaska.solver.SolverException;
import fr.lirmm.graphik.alaska.solver.SolverFactory;
import fr.lirmm.graphik.alaska.solver.SolverFactoryException;
import fr.lirmm.graphik.kb.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.kb.core.DefaultRule;
import fr.lirmm.graphik.kb.core.Query;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.kb.core.RuleSet;
import fr.lirmm.graphik.kb.core.Substitution;
import fr.lirmm.graphik.kb.core.factory.Factory;
import fr.lirmm.graphik.kb.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class Alaska {

	private static final Logger logger = LoggerFactory.getLogger(Alaska.class);
	
	public static void executeChase(AtomSet atomSet, Iterable<Rule> ruleSet) throws ChaseException {
		Chase chase = new DefaultChase(ruleSet, atomSet);
		chase.execute();
	}
	
	public static void executeChase(AtomSet atomSet, GraphOfRuleDependencies grd) throws ChaseException {
		Chase chase = new ChaseWithGRD(grd, atomSet);
		chase.execute();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// @TODO to clean
	// /////////////////////////////////////////////////////////////////////////
	
	public static Rule substitut(Rule rule, Substitution substitution) {
		AtomSet body = substitut(rule.getBody(), substitution);
		AtomSet head = substitut(rule.getHead(), substitution);
		return new DefaultRule(body, head);
	}
	
	public static AtomSet substitut(AtomSet atomSet, Substitution substitution) {
		AtomSet newAtomSet = new LinkedListAtomSet();
		for(Atom a : atomSet) {
			newAtomSet.add(substitution.getSubstitut(a));
		}
		return newAtomSet;
	}
	
	/**
	 * For boolean query, return a SubstitutionReader with an empty Substitution
	 * for true and no substitution for false.
	 * 
	 * @param query
	 * @param atomSet
	 * @return A substitution stream that represents homomorphisms.
	 * @throws SolverFactoryException
	 * @throws SolverException
	 */
	public static SubstitutionReader execute(Query query, ReadOnlyAtomSet atomSet)
																		  throws SolverFactoryException,
																		  SolverException {
		if (logger.isDebugEnabled())
			logger.debug("Query : " + query);

		Solver solver;
		solver = SolverFactory.getFactory().getSolver(query, atomSet);
		return solver.execute();

	}

	public static SubstitutionReader getRuleBodyHomomorphisms(Rule rule, ReadOnlyAtomSet atomSet)
																		  throws SolverFactoryException,
																		  SolverException {
		Query query = new DefaultConjunctiveQuery(rule.getBody(), rule.getFrontier());
		return execute(query, atomSet);
	}

	public static ReadOnlyAtomSet substitute(Substitution s, ReadOnlyAtomSet atomSet) {
		AtomSet newAtomSet = new LinkedListAtomSet();
		for (Atom a : atomSet) {
			newAtomSet.add(s.getSubstitut(a));
		}

		return newAtomSet;
	}

	/**
	 * @return
	 */
	public static Factory getFactory() {
		return Factory.getInstance();
	}

	
	

/*	private static void addExistentialSubstitution(Rule rule, Substitution sub) {
		StringBuilder skolem = null;
		int tmpLength = 0;
		for (Term t : rule.getExistentials()) {
			if (skolem == null) {
				skolem = new StringBuilder(Integer.toString(rule.hashCode()));
				skolem.append('[');
				for (Term var : rule.getFrontier()) {
					skolem.append(var).append(':');
					skolem.append(sub.getSubstitut(var)).append(',');
				}
				skolem.setCharAt(skolem.length() - 1, ']');
				skolem.append('[');
				tmpLength = skolem.length();
			}
			skolem.setLength(tmpLength);
			sub.put(t, new Term(skolem.append(t).append(']').toString(),
					Type.VARIABLE));
		}
	}*/
}
