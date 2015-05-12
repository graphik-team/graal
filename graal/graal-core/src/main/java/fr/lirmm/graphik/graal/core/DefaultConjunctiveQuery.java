package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.term.Term;


/**
 * Class representing a conjunctive query.
 * A conjunctive query is composed of a fact and a set of answer variables.
 */
public class DefaultConjunctiveQuery implements ConjunctiveQuery {

	private String label;
	private InMemoryAtomSet atomSet;
	private List<Term> responseVariables;

	// /////////////////////////////////////////////////////////////////////////
    //	CONSTRUCTOR
    // /////////////////////////////////////////////////////////////////////////

	public DefaultConjunctiveQuery() {
		this.label = "";
		this.atomSet = AtomSetFactory.getInstance().createAtomSet();
		this.responseVariables = new LinkedList<Term>();
	}

	public DefaultConjunctiveQuery(InMemoryAtomSet atomSet) {
		this.label = "";
        this.atomSet = atomSet;
        this.responseVariables = new LinkedList<Term>(atomSet.getTerms(Term.Type.VARIABLE));
    }

	public DefaultConjunctiveQuery(InMemoryAtomSet atomSet, List<Term> ans) {
		this("", atomSet, ans);
	}

	public DefaultConjunctiveQuery(Iterable<Atom> atomSet, Iterable<Term> answerVariables) {
		this.label = "";
		this.atomSet = new LinkedListAtomSet(atomSet);
		this.responseVariables = new LinkedList<Term>();
		for(Term t : answerVariables) {
			this.responseVariables.add(t);
		}
	}
	
	/**
	 * 
	 * @param label the name of this query
	 * @param atomSet the conjunction of atom representing the query
	 * @param ans the list of answer variables
	 */
	public DefaultConjunctiveQuery(String label, InMemoryAtomSet atomSet, List<Term> ans) {
		this.label = label;
		this.atomSet = atomSet;
		this.responseVariables = ans;
	}

	// copy constructor
	public DefaultConjunctiveQuery(ConjunctiveQuery query) {
		this.label = query.getLabel();
		this.atomSet = new LinkedListAtomSet(query.getAtomSet());
		this.responseVariables = new LinkedList<Term>(query.getAnswerVariables());
	}

	// /////////////////////////////////////////////////////////////////////////
    //	PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
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
	public List<Term> getAnswerVariables() { 
		return this.responseVariables; 
	}

	public void setAnswerVariables(List<Term> v) { 
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
