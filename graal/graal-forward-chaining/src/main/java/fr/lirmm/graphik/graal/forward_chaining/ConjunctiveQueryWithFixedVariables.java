/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class ConjunctiveQueryWithFixedVariables implements ConjunctiveQuery {

	private InMemoryAtomSet atomSet;
	private List<Term> answerVariables;

	public ConjunctiveQueryWithFixedVariables(AtomSet atomSet, Iterable<Term> fixedTerms) {
		this.atomSet = computeFixedQuery(atomSet, fixedTerms);
        this.answerVariables = new LinkedList(this.atomSet.getTerms(Term.Type.VARIABLE));
    }

	public ConjunctiveQueryWithFixedVariables(/*ReadOnly*/AtomSet atomSet,
			List<Term> responseVariables, Iterable<Term> fixedTerms) {

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
	
	@Override
	public boolean isBoolean() {
		return this.answerVariables.isEmpty();
	}

	@Override
	public InMemoryAtomSet getAtomSet() {
		return this.atomSet;
	}

	@Override
	public List<Term> getAnswerVariables() {
		return this.answerVariables;
	}
	
	@Override
	public void setAnswerVariables(List<Term> ans) {
		this.answerVariables = ans;
	}
	

	@Override
	public String getLabel() {
		return "";
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	private static InMemoryAtomSet computeFixedQuery(/*ReadOnly*/AtomSet atomSet,
			Iterable<Term> fixedTerms) {
		// create a Substitution for fixed query
		InMemoryAtomSet fixedQuery = AtomSetFactory.getInstance().createAtomSet();
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
