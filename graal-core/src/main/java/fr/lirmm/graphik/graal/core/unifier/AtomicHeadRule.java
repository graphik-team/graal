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
package fr.lirmm.graphik.graal.core.unifier;

import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;

/**
 * rule with only one atom in its head
 * 
 * @author Mélanie KÖNIG
 * 
 */
@SuppressWarnings("deprecation")
public class AtomicHeadRule implements Rule {

	private Rule rule;

	/**
	 * Construct an AtomicHeadRule
	 * 
	 * @param b
	 *            a fact
	 * @param h
	 *            must be an AtomicFact
	 */
	public AtomicHeadRule(InMemoryAtomSet b, Atom h) {
		this.rule = DefaultRuleFactory.instance().create(b, new AtomicAtomSet(h));
	}

	@Override
	public AtomicAtomSet getHead() {
		return (AtomicAtomSet) this.rule.getHead();
	}

	@Override
	public int compareTo(Rule arg0) {
		return this.rule.compareTo(arg0);
	}

	@Override
	public String getLabel() {
		return this.rule.getLabel();
	}

	@Override
	public void setLabel(String label) {
		this.rule.setLabel(label);
	}

	@Override
	public InMemoryAtomSet getBody() {
		return this.rule.getBody();
	}

	@Override
	public Set<Variable> getFrontier() {
		return this.rule.getFrontier();
	}

	@Override
	public Set<Variable> getExistentials() {
		return this.rule.getExistentials();
	}

	@Override
	@Deprecated
	public Set<Term> getTerms(Type type) {
		return this.rule.getTerms(type);
	}

	@Override
	public Set<Term> getTerms() {
		return this.rule.getTerms();
	}
	
	@Override
	public Set<Variable> getVariables() {
		return this.rule.getVariables();
	}
	
	@Override
	public Set<Constant> getConstants() {
		return this.rule.getConstants();
	}
	
	@Override
	public Set<Literal> getLiterals() {
		return this.rule.getLiterals();
	}

	@Override
	public void appendTo(StringBuilder sb) {
		this.rule.appendTo(sb);
	}

}
