package fr.lirmm.graphik.graal.core;

import java.io.Serializable;

/**
 * Represents a Predicate of an Atom.
 * 
 */
public class Predicate implements Comparable<Predicate>, Serializable {

	private static final long serialVersionUID = 3098419922942769704L;

	private final String label;
	private final int arity;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public Predicate(String label, int arity) {
		this.label = label;
		this.arity = arity;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public String getLabel() {
		return label;
	}

	public int getArity() {
		return arity;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getArity();
		result = prime
				 * result
				 + ((this.getLabel() == null) ? 0 : this.getLabel().hashCode());
		return result;
	}

	/**
	 * Verifies if two predicates are the same or not.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Predicate)) {
			return false;
		}
		Predicate other = (Predicate) obj;
		if (this.getArity() != other.getArity()) {
			return false;
		}
		if (this.getLabel() == null) {
			if (other.getLabel() != null) {
				return false;
			}
		} else if (!this.getLabel().equals(other.getLabel())) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Predicate other) {
		int cmpVal = (this.getArity() < other.getArity()) ? -1 : ((this
				.getArity() == other.getArity()) ? 0 : 1);
		if (cmpVal == 0) {
			cmpVal = this.getLabel().compareTo(other.getLabel());
		}
		return cmpVal;
	}

	@Override
	public String toString() {
		return this.getLabel();
	}

};
