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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithNegatedParts;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.util.MethodNotImplementedError;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RuleWrapper2ConjunctiveQueryWithNegatedParts implements ConjunctiveQueryWithNegatedParts {

	private String label = "";
	private Rule rule;
	private List<Term> ans;
	private List<InMemoryAtomSet> negParts;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public RuleWrapper2ConjunctiveQueryWithNegatedParts(Rule rule) {
		this.rule = rule;
		ans = new LinkedList<Term>();
		for(Term t : rule.getFrontier()) {
			ans.add(t);
		}
		this.negParts = Collections.singletonList(this.rule.getHead());
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isBoolean() {
		// TODO implement this method
		throw new MethodNotImplementedError();
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public InMemoryAtomSet getPositivePart() {
		return this.rule.getBody();
	}

	@Override
	public List<InMemoryAtomSet> getNegatedParts() {
		return this.negParts;
	}

	@Override
	public List<Term> getAnswerVariables() {
		return ans;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.appendTo(sb);
		return sb.toString();
	}

	@Override
	public void appendTo(StringBuilder sb) {
		sb.append("ConjunctiveQueryWithNegation based on: ");
		this.rule.appendTo(sb);
	}

	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
