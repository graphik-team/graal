/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 package fr.lirmm.graphik.graal.api.core;

import java.io.Serializable;

import fr.lirmm.graphik.util.string.AppendableToStringBuilder;

/**
 * Represents a Predicate of an Atom.
 * 
 */
public class Predicate implements Comparable<Predicate>, Serializable, AppendableToStringBuilder {

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
	 * @param identifier
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
	 * @return the arity of this predicate.
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
		this.appendTo(sb);
		return sb.toString();
	}

	@Override
	public void appendTo(StringBuilder sb) {
		sb.append(this.getIdentifier()).append('\\').append(this.getArity());
	}

};

