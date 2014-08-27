package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.Term.Type;

/**
 * This interface represents a logical atom like p(X,Y).
 */
public interface Atom extends Comparable<Atom>, Iterable<Term> {

	public static final Atom BOTTOM = new Atom() {
		private List<Term> terms = new LinkedList<Term>();
		private Predicate predicate = new Predicate("\u22A5", 0);

		@Override
		public int compareTo(Atom atom) {
			if (atom.getPredicate() == BOTTOM.getPredicate()) {
				return 0;
			}
			return -1;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o == null || !(o instanceof Atom))
				return false;
			return this.compareTo((Atom)o) == 0;
		}
		
		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public void setTerm(int index, Term term) {
		}

		@Override
		public void setPredicate(Predicate predicate) {
		}

		@Override
		public Collection<Term> getTerms(Type type) {
			return terms;
		}

		@Override
		public List<Term> getTerms() {
			return terms;
		}

		@Override
		public Term getTerm(int index) {
			return null;
		}

		@Override
		public Predicate getPredicate() {
			return this.predicate;
		}

		@Override
		public Iterator<Term> iterator() {
			return terms.iterator();
		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder(this.getPredicate().toString());
			s.append('(');
			boolean bool = false;
			for (Term term : this.getTerms()) {
				if (bool)
					s.append(',');
				s.append(term);
				bool = true;
			}
			s.append(')');

			return s.toString();
		}
	};

	/**
	 * Set the Predicate of this Atom.
	 * 
	 * @param predicate
	 */
	void setPredicate(Predicate predicate);

	/**
	 * Get the Predicate of this Atom.
	 * 
	 * @return
	 */
	Predicate getPredicate();

	/**
	 * Set the n<sup>th</sup> term of this Atom.
	 * 
	 * @param index
	 * @param term
	 */
	void setTerm(int index, Term term);

	/**
	 * get the n<sup>th</sup> term of this Atom.
	 * 
	 * @param index
	 * @return
	 */
	Term getTerm(int index);

	/**
	 * Get an ordered List that represents the terms of this Atom.
	 * 
	 * @return
	 */
	List<Term> getTerms();

	/**
	 * Get all Term of Type type.
	 * 
	 * @param type
	 * @return
	 */
	Collection<Term> getTerms(Type type);

	@Override
	Iterator<Term> iterator();

};
