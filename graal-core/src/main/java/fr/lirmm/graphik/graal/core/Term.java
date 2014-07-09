package fr.lirmm.graphik.graal.core;

import java.io.Serializable;

/**
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

	public Term(Object value, Type type) {
		this.value = value;
		this.type = type;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @return
	 */
	public Object getValue() {
		return this.value;
	}
	
	public boolean isConstant() {
		return Type.CONSTANT.equals(this.type) || Type.LITERAL.equals(this.type);
	}
	
	public boolean isVariable() {
		return !isConstant();
	}

	/**
	 * 
	 * @return
	 */
	public Type getType() {
		return this.type;
	}
	
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
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Term)) {
			return false;
		}
		Term other = (Term) obj;
		if (!this.getType().equals(other.getType())) {
			return false;
		} else if (!this.getValue().equals(other.getValue())) {
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
