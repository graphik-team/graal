/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.owl.logic;

import java.util.List;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author clement
 *
 */
public class Literal extends DefaultAtom {

	private static final long serialVersionUID = -5605704795350835620L;
	
	boolean isPositive;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public Literal(Predicate predicate, boolean isPositive) {
		super(predicate);
		this.isPositive = isPositive;
	}

	public Literal(Predicate predicate, List<Term> terms, boolean isPositive) {
		super(predicate, terms);
		this.isPositive = isPositive;
	}

	public Literal(Predicate predicate, boolean isPositive, Term... terms) {
		super(predicate, terms);
		this.isPositive = isPositive;
	}

	/**
	 * @param atom
	 */
	public Literal(Atom atom, boolean isPositive) {
		super(atom);
		this.isPositive = isPositive;
	}
	
	public Literal(Literal literal) {
		super((Atom) literal);
		this.isPositive = literal.isPositive();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public boolean isPositive() {
		return this.isPositive;
	}
	
	public Literal getComplement() {
		return new Literal((Atom) this, !this.isPositive);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param sb
	 */
	public void toString(StringBuilder sb) {
		if(!isPositive) {
			sb.append("!");
		}
		super.toString(sb);
	}

}
