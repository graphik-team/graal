package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;


/**
 * Class representing a conjunctive query.
 * A conjunctive query is composed of a fact and a set of answer variables.
 */
public class DefaultConjunctiveQuery implements ConjunctiveQuery {

	private /*ReadOnly*/AtomSet atomSet;
	private Collection<Term> responseVariables;

	// /////////////////////////////////////////////////////////////////////////
    //	CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

	public DefaultConjunctiveQuery() {
		this.atomSet = AtomSetFactory.getInstance().createAtomSet();
		this.responseVariables = new LinkedList<Term>();
	}
	
	public DefaultConjunctiveQuery(ReadOnlyAtomSet atomSet) {
        this.atomSet = new LinkedListAtomSet(atomSet);
        this.responseVariables = atomSet.getTerms(Term.Type.VARIABLE);
	}
	public DefaultConjunctiveQuery(/*ReadOnly*/AtomSet atomSet) {
        this.atomSet = atomSet;
        this.responseVariables = atomSet.getTerms(Term.Type.VARIABLE);
    }
	
	public DefaultConjunctiveQuery(/*ReadOnly*/AtomSet atomSet, Collection<Term> responseVariables) {
		this.atomSet = atomSet;
		this.responseVariables = responseVariables;
		if(this.responseVariables == null) {
			this.responseVariables = new LinkedList<Term>();
		}
	}

	// /////////////////////////////////////////////////////////////////////////
    //	PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the fact of the query.
	 */
	@Override
	public /*ReadOnly*/AtomSet getAtomSet() { return this.atomSet; }
	
	public void setAtomSet(/*ReadOnly*/AtomSet atomSet) {
	    this.atomSet = atomSet;
	}

	/**
	 * Returns the answer variables of the query.
	 */
	@Override
	public Collection<Term> getAnswerVariables() { return this.responseVariables; }

	public void setAnswerVariables(Collection<Term> v) { this.responseVariables = v; }
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.kb.query.Query#isBoolean()
	 */
	@Override
	public boolean isBoolean() {
		return responseVariables.isEmpty();
	}
	// /////////////////////////////////////////////////////////////////////////
    //	OVERRIDE METHODS
    // /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterator<Atom> iterator() {
		return getAtomSet().iterator();
	}

	@Override
	public String toString() {
	    StringBuilder s = new StringBuilder("ANS(");
	    for(Term t : this.responseVariables)
	        s.append(t).append(',');
	    
	    s.append(") : ");
	    s.append(this.atomSet);
	    return s.toString();
	}

	
}
