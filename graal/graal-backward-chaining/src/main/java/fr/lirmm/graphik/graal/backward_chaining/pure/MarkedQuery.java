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
 package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.ArrayList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.term.Term;

class MarkedQuery extends DefaultConjunctiveQuery {

	private ArrayList<Atom> markedAtoms;

	public MarkedQuery() {
		super();
		markedAtoms = new ArrayList<Atom>();
	}

	/**
	 * create a MarkedQuery which has the same atom than the given fact, which
	 * has the given term as answerVariable and which has no marked atoms
	 * 
	 * @param f
	 */
	public MarkedQuery(AtomSet atomset, ArrayList<Term> answerVariable) {
		super(atomset, answerVariable);
		markedAtoms = new ArrayList<Atom>();
	}

	/**
	 * create a MarkedQuery which has the same atom than the given fact, which
	 * has the given term as answerVariable and which has no marked atoms
	 * 
	 * @param f
	 */
	public MarkedQuery(ConjunctiveQuery query, ArrayList<Atom> markedAtoms) {
		super(query);
		this.markedAtoms = markedAtoms;
	}

	public MarkedQuery(MarkedQuery query) {
		super(query);
		this.markedAtoms = new ArrayList<Atom>();
		for (Atom a : query.getMarkedAtom())
			this.markedAtoms.add(new DefaultAtom(a));
	}

	/**
	 * Add the given atom into this fact as a marked atom
	 */
	public void addMarkedAtom(Atom atom) {
		this.getAtomSet().add(atom);
		markedAtoms.add(atom);
	}

	/**
	 * return the atoms of this fact that are marked
	 */
	public ArrayList<Atom> getMarkedAtom() {
		return markedAtoms;
	}

	/**
	 * Change the marked atoms by the given atoms
	 */
	public void setMarkedAtom(ArrayList<Atom> markedAtoms) {
		this.markedAtoms = markedAtoms;
	}

	/**
	 * Returns true if the given atom is marked in the receiving fact else false
	 * 
	 * @param a
	 *            atom
	 * @return true if the given atom is marked in the receiving fact else false
	 */
	public boolean isMarked(Atom a) {
		return markedAtoms.contains(a);

	}

	/**
	 * mark all the atom of the fact
	 */
	public void markAll() {
		markedAtoms = new ArrayList<Atom>();
		for (Atom a : this.getAtomSet())
			markedAtoms.add(a);
	}

	public void clear() {
		this.getAtomSet().clear();
		markedAtoms = new ArrayList<Atom>();
	}

	@Override
	public String toString() {

		String s = "(MQ| ";
		for (Atom a : this) {
			if (isMarked(a))
				s += a.toString().toUpperCase() + " ";
		}
		s += ")";
		return s;
	}

}
