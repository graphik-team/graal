package fr.lirmm.graphik.graal.core;

import java.io.Serializable;

import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.URIUtils;

/**
 * (Immutable object)
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class Term implements Comparable<Term>, Serializable {

	/**
	 * The enumeration of term types.
	 */
	public static enum Type {
		CONSTANT, VARIABLE, LITERAL
	}

	private static final long serialVersionUID = -8596306338753616109L;

	private final URI uri;
	private final Object value;
	private final Type type;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public Term(URI uri) {
		this.type = Term.Type.CONSTANT;
		this.value = null;
		this.uri = uri;
	}

	/**
	 * 
	 * @param value
	 *            if the type is VARIABLE or CONSTANT, this parameter will be
	 *            interpreted as a string.
	 * @param type
	 */
	public Term(Object value, Type type) {
		this.type = type;

		switch (type) {
		case LITERAL:
			this.value = value;
			this.uri = URIUtils.createURI(value.toString(), Prefix.LITERAL);
			break;
		case CONSTANT:
			this.value = null;
			this.uri = URIUtils.createURI(value.toString(), Prefix.DEFAULT);
			break;
		case VARIABLE:
		default:
			this.value = null;
			this.uri = new DefaultURI(value.toString());
			break;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Get the value.
	 * 
	 * @return
	 */
	public String getIdentifier() {
		return this.uri.toString();
	}

	/**
	 * Returns true if this Term is a constant (Type.LITERAL is considered as a
	 * constant).
	 * 
	 * @return
	 */
	public boolean isConstant() {
		return Type.CONSTANT.equals(this.type)
				|| Type.LITERAL.equals(this.type);
	}

	/**
	 * Returns true if this Term is a variable.
	 * 
	 * @return
	 */
	public boolean isVariable() {
		return !isConstant();
	}

	/**
	 * Returns the Type of this Term.
	 * 
	 * @return
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * Create a new Term from this term with a different type.
	 * 
	 * @param newType
	 * @return
	 */
	public Term transtypage(Type newType) {
		return new Term(this.value, newType);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getType().toString().hashCode();
		result = prime * result + this.getIdentifier().hashCode();
		return result;
	}

	/**
	 * Verifies if two terms are the same or not. Two terms are the same if they
	 * have the same label and if they are both constants or variables.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof Term)) {
			return false;
		}
		Term other = (Term) obj;
		if (!this.getType().equals(other.getType())) {
			return false;
		}
		return this.getIdentifier().equals(other.getIdentifier());
	}

	@Override
	public int compareTo(Term other) {
		int cmpVal = this.toString().compareTo(other.toString());
		if (cmpVal == 0) {
			cmpVal = this.getType().compareTo(other.getType());
		}
		return cmpVal;
	}

	@Override
	public String toString() {
		return this.getIdentifier();
	}

};
