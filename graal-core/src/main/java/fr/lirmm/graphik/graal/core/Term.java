package fr.lirmm.graphik.graal.core;

import java.io.Serializable;

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

	private final Object value;
	private final Type type;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param value
	 *            if the type is VARIABLE, this parameter will be interpreted as
	 *            a string.
	 * @param type
	 */
	public Term(Object value, Type type) {
		if (Type.VARIABLE.equals(type))
			value = value.toString();

		this.value = value;
		this.type = type;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Get the value.
	 * 
	 * @return
	 */
	public Object getValue() {
		return this.value;
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
		result = prime * result + this.getValue().hashCode();
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
		return this.getValue().equals(other.getValue());
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
		return this.value.toString();
	}

};
