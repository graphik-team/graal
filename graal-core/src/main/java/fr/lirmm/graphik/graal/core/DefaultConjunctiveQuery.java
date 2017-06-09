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
package fr.lirmm.graphik.graal.core;

import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * Class representing a conjunctive query. A conjunctive query is composed of a
 * fact and a set of answer variables.
 */
public class DefaultConjunctiveQuery implements ConjunctiveQuery {

	private String label;
	private InMemoryAtomSet atomSet;
	private List<Term> responseVariables;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DefaultConjunctiveQuery() {
		this.label = "";
		this.atomSet = DefaultAtomSetFactory.instance().create();
		this.responseVariables = new LinkedList<Term>();
	}

	public DefaultConjunctiveQuery(InMemoryAtomSet atomSet) {
		this.label = "";
		this.atomSet = atomSet;
		this.responseVariables = new LinkedList<Term>(atomSet.getVariables());
	}

	public DefaultConjunctiveQuery(InMemoryAtomSet atomSet, List<Term> ans) {
		this("", atomSet, ans);
	}

	public DefaultConjunctiveQuery(CloseableIterator<Atom> atomSet, CloseableIterator<Term> answerVariables) throws IteratorException {
		this.label = "";
		this.atomSet = DefaultAtomSetFactory.instance().create(atomSet);
		this.responseVariables = new LinkedList<Term>();
		while (answerVariables.hasNext()) {
			this.responseVariables.add(answerVariables.next());
		}
	}

	public DefaultConjunctiveQuery(CloseableIteratorWithoutException<Atom> atomSet,
	    CloseableIteratorWithoutException<Term> answerVariables) {
		this.label = "";
		this.atomSet = DefaultAtomSetFactory.instance().create(atomSet);
		this.responseVariables = new LinkedList<Term>();
		while (answerVariables.hasNext()) {
			this.responseVariables.add(answerVariables.next());
		}
	}

	/**
	 * 
	 * @param label
	 *            the name of this query
	 * @param atomSet
	 *            the conjunction of atom representing the query
	 * @param ans
	 *            the list of answer variables
	 */
	public DefaultConjunctiveQuery(String label, InMemoryAtomSet atomSet, List<Term> ans) {
		this.label = label;
		this.atomSet = atomSet;
		this.responseVariables = ans;
	}

	// copy constructor
	public DefaultConjunctiveQuery(ConjunctiveQuery query) {
		this.label = query.getLabel();
		this.atomSet = DefaultAtomSetFactory.instance().create(query.getAtomSet());
		this.responseVariables = new LinkedList<Term>(query.getAnswerVariables());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String getLabel() {
		return this.label;
	}
	
	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Returns the fact of the query.
	 */
	@Override
	public InMemoryAtomSet getAtomSet() {
		return this.atomSet;
	}

	public void setAtomSet(InMemoryAtomSet atomSet) {
		this.atomSet = atomSet;
	}

	/**
	 * Returns the answer variables of the query.
	 */
	@Override
	public List<Term> getAnswerVariables() {
		return this.responseVariables;
	}

	@Override
	public void setAnswerVariables(List<Term> v) {
		this.responseVariables = v;
	}

	@Override
	public boolean isBoolean() {
		return responseVariables.isEmpty();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public CloseableIteratorWithoutException<Atom> iterator() {
		return getAtomSet().iterator();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.appendTo(sb);
		return sb.toString();
	}

	@Override
	public void appendTo(StringBuilder sb) {
		sb.append("ANS(");
		boolean first = true;
		for (Term t : this.responseVariables) {
			if(!first) {
				sb.append(',');
			}
			first = false;
			sb.append(t);
		}

		sb.append(") : ");
		sb.append(this.atomSet);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ConjunctiveQuery)) {
			return false;
		}
		ConjunctiveQuery other = (ConjunctiveQuery) obj;
		return this.equals(other);
	}

	public boolean equals(ConjunctiveQuery other) {
		return this.getAnswerVariables().equals(other.getAnswerVariables())
		       && this.getAtomSet().equals(other.getAtomSet());
	}

}
