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
 package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

class MarkedQuery extends DefaultConjunctiveQuery {

	private List<Atom> markedAtoms;

	public MarkedQuery() {
		super();
		markedAtoms = new ArrayList<Atom>();
	}

	/**
	 * create a MarkedQuery which has the same atom than the given fact, which
	 * has the given term as answerVariable and which has no marked atoms
	 * 
	 * @param atomset
	 * @param answerVariable
	 */
	public MarkedQuery(InMemoryAtomSet atomset, List<Term> answerVariable) {
		super(atomset, answerVariable);
		markedAtoms = new LinkedList<Atom>();
	}

	/**
	 * create a MarkedQuery which has the same atom than the given fact, which
	 * has the given term as answerVariable and which has no marked atoms
	 * 
	 * @param query
	 * @param markedAtoms
	 */
	public MarkedQuery(ConjunctiveQuery query, List<Atom> markedAtoms) {
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
	public List<Atom> getMarkedAtom() {
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
		CloseableIteratorWithoutException<Atom> it = this.getAtomSet().iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			markedAtoms.add(a);
		}
	}

	public void clear() {
		this.getAtomSet().clear();
		markedAtoms = new ArrayList<Atom>();
	}

	@Override
	public String toString() {

		String s = "(MQ| ";
		CloseableIteratorWithoutException<Atom> it = this.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (isMarked(a))
				s += "*";

			s += a.toString() + " ";
		}
		s += ")";
		return s;
	}

}
