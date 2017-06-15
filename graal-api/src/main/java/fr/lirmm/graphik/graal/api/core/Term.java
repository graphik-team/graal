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
/**
* 
*/
package fr.lirmm.graphik.graal.api.core;

import java.io.Serializable;

/**
 * In analogy to natural language, where a noun phrase refers to an object, 
 * a term denotes a mathematical object referring to someone or something. 
 * A term is either a {@link Constant} or {@link Variable}, a variable is
 * a placeholder for an other term.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Term extends Comparable<Term>, Serializable {

	/*
	 * This class is deprecated since 1.3. <br>
	 * <br>
	 * The enumeration of term types.
	 */
	@Deprecated
	public static enum Type {
							 CONSTANT(false), VARIABLE(true), LITERAL(false);

		private boolean isVariable;

		Type(boolean isVariable) {
			this.isVariable = isVariable;
		}

		boolean isConstant() {
			return !this.isVariable;
		}

		boolean isVariable() {
			return this.isVariable;
		}
	}

	/**
	 * Returns true iff this term is a Constant. 
	 * The returned value of {@link #isConstant()} must be equals to !{@link #isVariable()}.
	 * 
	 * @return true if this term is a constant, false otherwise.
	 */
	boolean isConstant();

	/** 
	 * Returns true if this term is a Variable.
	 * The returned value of {@link #isVariable()} must be equals to !{@link #isConstant()}.
	 *
	 * 
	 * @return true if this term is a variable, false otherwise.
	 */
	boolean isVariable();
	
	/**
	 * Returns true if this term is a literal, in this case
	 * {@link #isConstant()} must also return true.
	 * 
	 * @return true if this term is a literal, false otherwise.
	 */
	boolean isLiteral();

	/**
	 * Return a label that represents this term. 
	 * There is no guarantee that two terms with the same label represent the same term (see {@link #getIdentifier()}).
	 * 
	 * @return a label that represents this term.
	 */
	String getLabel();

	/**
	 * A unique identifier of this term. The following constraint must be fulfil: <br>
	 * <code>this.getIdentifier().equals(o.getIdentifier())</code> 
	 * must be equivalent to
	 * <code>this.equals(o)</code>.
	 * @return a unique identifier of this term.
	 */
	Object getIdentifier();

	/**
	 * This method is deprecated since 1.3, use {@link #isVariable()}, {@link #isConstant()} or {@link #isLiteral()} instead. <br>
	 * <br>
	 * 
	 * @return the {@link Type} of this term.
	 */
	@Deprecated
	Type getType();

}
