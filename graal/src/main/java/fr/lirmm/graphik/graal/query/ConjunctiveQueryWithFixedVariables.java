/**
 * 
 */
package fr.lirmm.graphik.graal.query;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class ConjunctiveQueryWithFixedVariables implements ConjunctiveQuery {

	private /*ReadOnly*/AtomSet atomSet;
	private Collection<Term> answerVariables;

	public ConjunctiveQueryWithFixedVariables(/*ReadOnly*/AtomSet atomSet, Iterable<Term> fixedTerms) {
		this.atomSet = computeFixedQuery(atomSet, fixedTerms);
        this.answerVariables = this.atomSet.getTerms(Term.Type.VARIABLE);
    }

	public ConjunctiveQueryWithFixedVariables(ReadOnlyAtomSet atomSet, Iterable<Term> fixedTerms) {
		this.atomSet = computeFixedQuery(new LinkedListAtomSet(atomSet), fixedTerms);
        this.answerVariables = this.atomSet.getTerms(Term.Type.VARIABLE);
    }

	public ConjunctiveQueryWithFixedVariables(/*ReadOnly*/AtomSet atomSet,
			Collection<Term> responseVariables, Iterable<Term> fixedTerms) {

		this.atomSet = computeFixedQuery(atomSet, fixedTerms);
		this.answerVariables = responseVariables;
		if (this.answerVariables == null) {
			this.answerVariables = new LinkedList<Term>();
		}
	}

	@Override
	public Iterator<Atom> iterator() { return getAtomSet().iterator(); }
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.Query#isBoolean()
	 */
	@Override
	public boolean isBoolean() {
		return this.answerVariables.isEmpty();
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.ConjunctiveQuery#getAtomSet()
	 */
	@Override
	public /*ReadOnly*/AtomSet getAtomSet() {
		return this.atomSet;
	}

	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.core.ConjunctiveQuery#getResponseVariables()
	 */
	@Override
	public Collection<Term> getAnswerVariables() {
		return this.answerVariables;
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	private static AtomSet computeFixedQuery(/*ReadOnly*/AtomSet atomSet,
			Iterable<Term> fixedTerms) {
		// create a Substitution for fixed query
		AtomSet fixedQuery = AtomSetFactory.getInstance().createAtomSet();
		Substitution fixSub = SubstitutionFactory.getInstance().createSubstitution();
		for (Term t : fixedTerms) {
			if (Type.VARIABLE.equals(t.getType()))
				fixSub.put(t, t.transtypage(Type.CONSTANT));
		}

		// apply substitution
		for (Atom a : atomSet) {
			fixedQuery.add(fixSub.getSubstitut(a));
		}
		
		return fixedQuery;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("FIXED(");
		for (Term t : this.atomSet.getTerms(Term.Type.CONSTANT))
			s.append(t).append(',');

		s.append("), ANS(");
		for (Term t : this.answerVariables)
			s.append(t).append(',');

		s.append(") :- ");
		s.append(this.atomSet);
		return s.toString();
	}
	
}
