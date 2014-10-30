package fr.lirmm.graphik.graal.core;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

/**
 * Class that implements atoms.
 */
public class DefaultAtom extends AbstractAtom implements Serializable {

	private static final long serialVersionUID = -5889218407173357933L;

	private Predicate predicate;
	private List<Term> terms;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultAtom(Predicate predicate) {
		this.predicate = predicate;
		int n = predicate.getArity();
		this.terms = new ArrayList<Term>(n);
		for (int i = 0; i < n; ++i)
			this.terms.add(null);
	}

	public DefaultAtom(Predicate predicate, List<Term> terms) {
		this.predicate = predicate;
		this.terms = terms;
	}

	public DefaultAtom(Predicate predicate, Term... terms) {
		this(predicate, Arrays.asList(terms));
	}

	/**
	 * @param atom
	 */
	public DefaultAtom(Atom atom) {
		this.predicate = atom.getPredicate(); // Predicate is immutable
		this.terms = new LinkedList<Term>();
		for (Term t : atom.getTerms())
			this.terms.add(t); // Term is immutable
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Returns all Term with the Type type.
	 */
	@Override
	public Collection<Term> getTerms(Term.Type type) {
		Collection<Term> typedTerms = new LinkedList<Term>();
		for (Term term : this.terms)
			if (type.equals(term.getType()))
				typedTerms.add(term);

		return typedTerms;
	}

	/**
	 * Verifies if a certain term is contained in the atom or not.
	 */
	public boolean contains(Term term) {
		int size = terms.size();
		for (int i = 0; i < size; i++) {
			if (terms.get(i).equals(term)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the index of a given term in the atom.
	 */
	public int[] getIndexOf(Term term) {
		int[] result = null;
		int resultCounter = 0;
		int termsSize = terms.size();
		for (int i = 0; i < termsSize; i++) {
			if (terms.get(i).equals(term)) {
				resultCounter++;
			}
		}
		if (resultCounter != 0) {
			result = new int[resultCounter];
			int pos = 0;
			for (int i = 0; i < termsSize; i++) {
				if (terms.get(i).equals(term)) {
					result[pos] = i;
					pos++;
				}
			}
		}
		return result;
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS/SETTERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Set the predicate.
	 */
	@Override
	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

	/**
	 * Get the predicate.
	 */
	@Override
	public Predicate getPredicate() {
		return predicate;
	}

	/**
	 * Set the Term with the specified index.
	 */
	@Override
	public void setTerm(int index, Term term) {
		this.terms.set(index, term);

	}

	/**
	 * Returns the Term with the specified index.
	 */
	@Override
	public Term getTerm(int index) {
		return this.terms.get(index);
	}

	/**
	 * Set the List of Term.
	 * 
	 * @param terms
	 */
	public void setTerms(List<Term> terms) {
		this.terms = terms;
	}

	/**
	 * Returns the List of Term.
	 */
	@Override
	public List<Term> getTerms() {
		return this.terms;
	}

};
