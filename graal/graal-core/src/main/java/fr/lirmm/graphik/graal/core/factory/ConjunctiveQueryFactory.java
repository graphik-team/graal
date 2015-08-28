/**
 * 
 */
package fr.lirmm.graphik.graal.core.factory;

import java.util.List;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.impl.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class ConjunctiveQueryFactory {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private static ConjunctiveQueryFactory instance = new ConjunctiveQueryFactory();

	protected ConjunctiveQueryFactory() {
		super();
	}

	public static synchronized ConjunctiveQueryFactory instance() {
		return instance;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Create an empty query
	 * 
	 * @return
	 */
	public ConjunctiveQuery create() {
		return new DefaultConjunctiveQuery();
	}

	/**
	 * Copy
	 * 
	 * @param query
	 * @return
	 */
	public ConjunctiveQuery create(ConjunctiveQuery query) {
		return new DefaultConjunctiveQuery(query);
	}

	/**
	 * Create a query from the specified atom set. All variables appearing in
	 * the atom set will be considered as answer variables.
	 * 
	 * @param atomSet
	 * @return
	 */
	public ConjunctiveQuery create(InMemoryAtomSet atomSet) {
		return new DefaultConjunctiveQuery(atomSet);
	}

	/**
	 * Create a query from the specified atom set and the specified answer
	 * variables.
	 * 
	 * @param atomSet
	 * @param ans
	 * @return
	 */
	public ConjunctiveQuery create(InMemoryAtomSet atomSet, List<Term> ans) {
		return new DefaultConjunctiveQuery(atomSet, ans);
	}

	/**
	 * Create a query from the specified atom set and the specified answer
	 * variables.
	 * 
	 * @param atomSet
	 * @param answerVariables
	 * @return
	 */
	public ConjunctiveQuery create(Iterable<Atom> atomSet, Iterable<Term> answerVariables) {
		return new DefaultConjunctiveQuery(atomSet, answerVariables);
	}

	/**
	 * Create a query from the specified atom set, answer variables and label.
	 * 
	 * @param label
	 * @param atomSet
	 * @param ans
	 * @return
	 */
	public ConjunctiveQuery create(String label, InMemoryAtomSet atomSet, List<Term> ans) {
		return new DefaultConjunctiveQuery(label, atomSet, ans);
	}

}
