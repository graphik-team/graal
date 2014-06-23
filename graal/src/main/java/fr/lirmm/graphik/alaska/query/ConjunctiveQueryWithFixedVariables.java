/**
 * 
 */
package fr.lirmm.graphik.alaska.query;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;

import fr.lirmm.graphik.alaska.Alaska;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.ConjunctiveQuery;
import fr.lirmm.graphik.kb.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.core.Substitution;
import fr.lirmm.graphik.kb.core.Term;
import fr.lirmm.graphik.kb.core.Term.Type;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class ConjunctiveQueryWithFixedVariables implements ConjunctiveQuery {

	private /*ReadOnly*/AtomSet atomSet;
	private Collection<Term> responseVariables;

	public ConjunctiveQueryWithFixedVariables(/*ReadOnly*/AtomSet atomSet, Iterable<Term> fixedTerms) {
		this.atomSet = computeFixedQuery(atomSet, fixedTerms);
        this.responseVariables = this.atomSet.getTerms(Term.Type.VARIABLE);
       
    }

	public ConjunctiveQueryWithFixedVariables(ReadOnlyAtomSet atomSet, Iterable<Term> fixedTerms) {
		this.atomSet = computeFixedQuery(new LinkedListAtomSet(atomSet), fixedTerms);
        this.responseVariables = this.atomSet.getTerms(Term.Type.VARIABLE);
       
    }

	public ConjunctiveQueryWithFixedVariables(/*ReadOnly*/AtomSet atomSet,
			Collection<Term> responseVariables, Iterable<Term> fixedTerms) {

		this.atomSet = computeFixedQuery(atomSet, fixedTerms);
		this.responseVariables = responseVariables;
		if (this.responseVariables == null) {
			this.responseVariables = new LinkedList<Term>();
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
		return this.responseVariables.isEmpty();
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
	public Collection<Term> getResponseVariables() {
		return this.responseVariables;
	}
	
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	private static AtomSet computeFixedQuery(/*ReadOnly*/AtomSet atomSet,
			Iterable<Term> fixedTerms) {
		// create a Substitution for fixed query
		AtomSet fixedQuery = Alaska.getFactory().createAtomSet();
		Substitution fixSub = Alaska.getFactory().createSubstitution();
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


	
}
