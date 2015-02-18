package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;


/**
 * Class representing a conjunctive query.
 * A conjunctive query is composed of a fact and a set of answer variables.
 */
public class DefaultConjunctiveQuery implements ConjunctiveQuery {

	private InMemoryAtomSet atomSet;
	private Collection<Term> responseVariables;

	// /////////////////////////////////////////////////////////////////////////
    //	CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

	public DefaultConjunctiveQuery() {
		this.atomSet = AtomSetFactory.getInstance().createAtomSet();
		this.responseVariables = new LinkedList<Term>();
	}

	public DefaultConjunctiveQuery(InMemoryAtomSet atomSet) {
        this.atomSet = atomSet;
        this.responseVariables = atomSet.getTerms(Term.Type.VARIABLE);
    }

	public DefaultConjunctiveQuery(InMemoryAtomSet atomSet, Collection<Term> answerVariables) {
		this.atomSet = atomSet;
		this.responseVariables = answerVariables;
		if(this.responseVariables == null) {
			this.responseVariables = Collections.<Term>emptyList();
		}
	}

	public DefaultConjunctiveQuery(Iterable<Atom> atomSet, Iterable<Term> answerVariables) {
		this.atomSet = new LinkedListAtomSet(atomSet);
		this.responseVariables = new LinkedList<Term>();
		for(Term t : answerVariables) {
			this.responseVariables.add(t);
		}
	}

	// copy constructor
	public DefaultConjunctiveQuery(ConjunctiveQuery query) {
		this.atomSet = new LinkedListAtomSet(query.getAtomSet());
		this.responseVariables = new LinkedList<Term>(query.getAnswerVariables());
	}

	// /////////////////////////////////////////////////////////////////////////
    //	PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Returns the fact of the query.
	 */
	@Override
	public InMemoryAtomSet getAtomSet() { 
		return this.atomSet; 
	}
	
	public void setAtomSet(InMemoryAtomSet atomSet) {
	    this.atomSet = atomSet;
	}

	/**
	 * Returns the answer variables of the query.
	 */
	@Override
	public Collection<Term> getAnswerVariables() { 
		return this.responseVariables; 
	}

	public void setAnswerVariables(Collection<Term> v) { 
		this.responseVariables = v; 
	}
	
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
