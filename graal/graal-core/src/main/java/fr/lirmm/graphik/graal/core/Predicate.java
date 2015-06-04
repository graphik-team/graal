package fr.lirmm.graphik.graal.core;

import java.io.Serializable;

/**
 * Represents a Predicate of an Atom.
 * 
 */
public class Predicate implements Comparable<Predicate>, Serializable {

	public static final Predicate EQUALITY = new Predicate("=", 2);
	public static final Predicate BOTTOM = new Predicate("\u22A5", 1);
	public static final Predicate TOP = new Predicate("\u22A4", 1);

	private static final long serialVersionUID = 3098419922942769704L;

	private final Object identifier;
	private final int arity;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Construct a prefix with a specified prefix and a local name.
	 * 
	 * @param prefix
	 * @param name
	 * @param arity
	 */
	public Predicate(Object identifier, int arity) {
		this.identifier = identifier;
		this.arity = arity;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Get the URI representation of this predicate.
	 * 
	 * @return a string representing predicate label.
	 */
	public Object getIdentifier() {
		return this.identifier;
	}

	/**
	 * Get the arity of this predicate.
	 * 
	 * @return
	 */
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
		result = prime * result
				+ (this.getIdentifier() == null ? 0 : this.getIdentifier().hashCode());
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
		if (obj == null || !(obj instanceof Predicate)) {
			return false;
		}
		Predicate other = (Predicate) obj;
		if (this.getArity() != other.getArity()) {
			return false;
		}
		return this.getIdentifier().equals(other.getIdentifier());
	}

	@Override
	public int compareTo(Predicate other) {
		int cmpVal = this.getArity() < other.getArity() ? -1 : this
				.getArity() == other.getArity() ? 0 : 1;
		if (cmpVal == 0) {
			cmpVal = this.getIdentifier().toString()
					.compareTo(other.getIdentifier().toString());
		}
		return cmpVal;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getIdentifier()).append('[').append(this.getArity())
				.append("]");
		return sb.toString();
	}

};