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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithNegation;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultConjunctiveQueryWithNegation implements ConjunctiveQueryWithNegation {

	private String label;
	private InMemoryAtomSet positiveAtomSet;
	private InMemoryAtomSet negativeAtomSet;
	private List<Term> responseVariables;
	private Set<Variable> frontierVariables;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DefaultConjunctiveQueryWithNegation() {
		this("", DefaultAtomSetFactory.instance().create(), DefaultAtomSetFactory.instance().create());
	}

	public DefaultConjunctiveQueryWithNegation(InMemoryAtomSet positiveAtomSet, InMemoryAtomSet negativeAtomSet) {
		this("", positiveAtomSet, negativeAtomSet);
	}

	public DefaultConjunctiveQueryWithNegation(String label, InMemoryAtomSet positiveAtomSet,
			InMemoryAtomSet negativeAtomSet) {
		this("", positiveAtomSet, negativeAtomSet, new LinkedList<Term>(positiveAtomSet.getVariables()));
	}

	public DefaultConjunctiveQueryWithNegation(InMemoryAtomSet positiveAtomSet, InMemoryAtomSet negativeAtomSet,
			List<Term> ans) {
		this("", positiveAtomSet, negativeAtomSet, ans);
	}

	/**
	 * 
	 * @param label
	 *            the name of this query
	 * @param positiveAtomSet
	 *            the positive part of the conjunction of atom representing the
	 *            query
	 * @param negativeAtomSet
	 *            the negative part of the conjunction of atom representing the
	 *            query
	 * @param ans
	 *            the list of answer variables
	 */
	public DefaultConjunctiveQueryWithNegation(String label, InMemoryAtomSet positiveAtomSet,
			InMemoryAtomSet negativeAtomSet, List<Term> ans) {
		this.label = label;
		this.positiveAtomSet = positiveAtomSet;
		this.negativeAtomSet = negativeAtomSet;
		this.responseVariables = ans;
		this.frontierVariables = null;
	}

	// copy constructor
	public DefaultConjunctiveQueryWithNegation(ConjunctiveQueryWithNegation query) {
		this(query.getLabel(), DefaultAtomSetFactory.instance().create(query.getPositiveAtomSet()),
				DefaultAtomSetFactory.instance().create(query.getNegativeAtomSet()),
				new LinkedList<Term>(query.getAnswerVariables()));
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

	@Override
	public InMemoryAtomSet getPositiveAtomSet() {
		return this.positiveAtomSet;
	}

	@Override
	public InMemoryAtomSet getNegativeAtomSet() {
		return this.negativeAtomSet;
	}

	/**
	 * Returns the answer variables of the query.
	 */
	@Override
	public List<Term> getAnswerVariables() {
		return this.responseVariables;
	}

	@Override
	public boolean isBoolean() {
		return responseVariables.isEmpty();
	}

	@Override
	public Set<Variable> getFrontierVariables() {
		if (this.frontierVariables == null) {
			this.computeFrontierAndExistentials();
		}

		return this.frontierVariables;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

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
			if (!first) {
				sb.append(',');
			}
			first = false;
			sb.append(t);
		}

		sb.append(") : ");
		sb.append(this.positiveAtomSet);
		sb.append(", \u22A5(");
		sb.append(this.negativeAtomSet);
		sb.append(")");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ConjunctiveQueryWithNegation)) {
			return false;
		}
		ConjunctiveQueryWithNegation other = (ConjunctiveQueryWithNegation) obj;
		return this.equals(other);
	}

	public boolean equals(ConjunctiveQueryWithNegation other) {
		return this.getAnswerVariables().equals(other.getAnswerVariables())
				&& this.getPositiveAtomSet().equals(other.getPositiveAtomSet()) 
				&& this.getNegativeAtomSet().equals(other.getNegativeAtomSet());
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void computeFrontierAndExistentials() {
		this.frontierVariables = new TreeSet<Variable>();
		Collection<Variable> body = this.getPositiveAtomSet().getVariables();

		for (Variable termHead : this.getNegativeAtomSet().getVariables()) {
			for (Variable termBody : body) {
				if (termBody.equals(termHead)) {
					this.frontierVariables.add(termHead);
				}
			}
		}
	}

}
