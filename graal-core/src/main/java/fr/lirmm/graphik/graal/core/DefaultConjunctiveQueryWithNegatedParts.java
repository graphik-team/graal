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

import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithNegatedParts;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultConjunctiveQueryWithNegatedParts implements ConjunctiveQueryWithNegatedParts {

	private String label;
	private InMemoryAtomSet positiveAtomSet;
	private List<InMemoryAtomSet> negatedParts;
	private List<Term> responseVariables;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultConjunctiveQueryWithNegatedParts(InMemoryAtomSet positiveAtomSet, List<InMemoryAtomSet> negatedParts) {
		this("", positiveAtomSet, negatedParts);
	}

	public DefaultConjunctiveQueryWithNegatedParts(String label, InMemoryAtomSet positiveAtomSet,
			List<InMemoryAtomSet> negatedParts) {
		this("", positiveAtomSet, negatedParts, new LinkedList<Term>(positiveAtomSet.getVariables()));
	}

	public DefaultConjunctiveQueryWithNegatedParts(InMemoryAtomSet positiveAtomSet, List<InMemoryAtomSet> negatedParts,
			List<Term> ans) {
		this("", positiveAtomSet, negatedParts, ans);
	}

	/**
	 * 
	 * @param label
	 *            the name of this query
	 * @param positiveAtomSet
	 *            the positive part of the conjunction of atom representing the
	 *            query
	 * @param negatedParts
	 *            a list of negated conjunction of atoms.
	 * @param ans
	 *            the list of answer variables
	 */
	public DefaultConjunctiveQueryWithNegatedParts(String label, InMemoryAtomSet positiveAtomSet,
			List<InMemoryAtomSet> negatedParts, List<Term> ans) {
		this.label = label;
		this.positiveAtomSet = positiveAtomSet;
		this.negatedParts = negatedParts;
		this.responseVariables = ans;
	}

	// copy constructor
	public DefaultConjunctiveQueryWithNegatedParts(ConjunctiveQueryWithNegatedParts query) {
		this(query.getLabel(), DefaultAtomSetFactory.instance().create(query.getPositivePart()),
				deepCopy(query.getNegatedParts()),
				new LinkedList<Term>(query.getAnswerVariables()));
	}
	

	private static List<InMemoryAtomSet> deepCopy(List<InMemoryAtomSet> sets) {
		List<InMemoryAtomSet> negParts = new LinkedList<InMemoryAtomSet>();
		for(InMemoryAtomSet set : sets) {
			negParts.add(DefaultAtomSetFactory.instance().create(set));
		}
		return negParts;
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
	public InMemoryAtomSet getPositivePart() {
		return this.positiveAtomSet;
	}

	@Override
	public List<InMemoryAtomSet> getNegatedParts() {
		return this.negatedParts;
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
		for(InMemoryAtomSet atomset : this.negatedParts) {
			sb.append(", \u22A5(");
			sb.append(atomset);
			sb.append(")");
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ConjunctiveQueryWithNegatedParts)) {
			return false;
		}
		ConjunctiveQueryWithNegatedParts other = (ConjunctiveQueryWithNegatedParts) obj;
		return this.equals(other);
	}

	public boolean equals(ConjunctiveQueryWithNegatedParts other) {
		return this == other;
	}


}
