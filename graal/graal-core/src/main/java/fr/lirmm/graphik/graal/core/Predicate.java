package fr.lirmm.graphik.graal.core;

import java.io.Serializable;

import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.URIUtils;

/**
 * Represents a Predicate of an Atom.
 * 
 */
public class Predicate implements Comparable<Predicate>, Serializable {

    // FIXME this class is *not* consistent with equals
	public static final Predicate EQUALITY = new Predicate("=", 2) {

		private static final long serialVersionUID = -8961695871557858255L;

		@Override
		public int compareTo(Predicate predicate) {
			if (predicate == this) {
				return 0;
			}
			return -1;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || !(o instanceof Predicate))
				return false;
			return this.compareTo((Predicate) o) == 0;
		}

	};

	private static final long serialVersionUID = 3098419922942769704L;

    // discuss with Michel & Alain: Object instead of URI?
	private final URI uri;
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
	public Predicate(URI uri, int arity) {
		this.uri = uri;
		this.arity = arity;
	}

	/**
	 * Construct a predicate
	 * 
	 * @param string
	 * @param arity
	 */
	public Predicate(String string, int arity) {
		this.uri = URIUtils.createURI(string, Prefix.PREDICATE);
		;
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
	public String getIdentifier() {
		return this.uri.toString();
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
			cmpVal = this.getIdentifier().compareTo(other.getIdentifier());
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