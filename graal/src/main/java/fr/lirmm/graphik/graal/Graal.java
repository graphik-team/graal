/**
 * 
 */
package fr.lirmm.graphik.graal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.solver.DefaultSolverFactory;
import fr.lirmm.graphik.graal.solver.Solver;
import fr.lirmm.graphik.graal.solver.SolverException;
import fr.lirmm.graphik.graal.solver.SolverFactory;
import fr.lirmm.graphik.graal.solver.SolverFactoryException;
import fr.lirmm.graphik.graal.solver.SqlSolverChecker;
import fr.lirmm.graphik.graal.solver.SqlUnionConjunctiveQueriesSolverChecker;
import fr.lirmm.graphik.graal.transformation.TransformatorSolverChecker;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class Graal {

	private static final Logger logger = LoggerFactory.getLogger(Graal.class);

	// /////////////////////////////////////////////////////////////////////////
	// FACTORY METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static boolean isInit = false;

	public static SolverFactory getSolverFactory() {
		if (!isInit) {
			DefaultSolverFactory.getInstance().addChecker(
					new TransformatorSolverChecker());
			DefaultSolverFactory.getInstance().addChecker(
					new SqlSolverChecker());
			DefaultSolverFactory.getInstance().addChecker(
					new SqlUnionConjunctiveQueriesSolverChecker());
			isInit = true;
		}
		return DefaultSolverFactory.getInstance();
	}

	// /////////////////////////////////////////////////////////////////////////
	// EXECUTE METHODS
	// /////////////////////////////////////////////////////////////////////////

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
	public static SubstitutionReader executeQuery(Query query,
			ReadOnlyAtomSet atomSet) throws SolverFactoryException,
			SolverException {
		if (logger.isDebugEnabled())
			logger.debug("Query : " + query);

		Solver solver = Graal
				.getSolverFactory().getSolver(query, atomSet);
		return solver.execute(query, atomSet);

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
		for (Atom a : atomSet) {
			newAtomSet.add(substitution.getSubstitut(a));
		}
		return newAtomSet;
	}

	public static SubstitutionReader getRuleBodyHomomorphisms(Rule rule,
			ReadOnlyAtomSet atomSet) throws SolverFactoryException,
			SolverException {
		Query query = new DefaultConjunctiveQuery(rule.getBody(),
				rule.getFrontier());
		return executeQuery(query, atomSet);
	}

	public static ReadOnlyAtomSet substitute(Substitution s,
			ReadOnlyAtomSet atomSet) {
		AtomSet newAtomSet = new LinkedListAtomSet();
		for (Atom a : atomSet) {
			newAtomSet.add(s.getSubstitut(a));
		}

		return newAtomSet;
	}

	/*
	 * private static void addExistentialSubstitution(Rule rule, Substitution
	 * sub) { StringBuilder skolem = null; int tmpLength = 0; for (Term t :
	 * rule.getExistentials()) { if (skolem == null) { skolem = new
	 * StringBuilder(Integer.toString(rule.hashCode())); skolem.append('['); for
	 * (Term var : rule.getFrontier()) { skolem.append(var).append(':');
	 * skolem.append(sub.getSubstitut(var)).append(','); }
	 * skolem.setCharAt(skolem.length() - 1, ']'); skolem.append('['); tmpLength
	 * = skolem.length(); } skolem.setLength(tmpLength); sub.put(t, new
	 * Term(skolem.append(t).append(']').toString(), Type.VARIABLE)); } }
	 */
}
