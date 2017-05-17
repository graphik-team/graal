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
package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class DefaultRule extends AbstractRule {

	private String label;
	private InMemoryAtomSet body;
	private InMemoryAtomSet head;

	private Set<Term> terms = null;
	private Set<Variable> variables = null;
	private Set<Constant> constants = null;
	private Set<Literal> literals = null;
	private Set<Variable> frontier = null;
	private Set<Variable> existentials = null;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DefaultRule() {
		this("", new LinkedListAtomSet(), new LinkedListAtomSet());
	}

	public DefaultRule(CloseableIteratorWithoutException<Atom> body, CloseableIteratorWithoutException<Atom> head) {
		this("", body, head);
	}

	public DefaultRule(String label, CloseableIteratorWithoutException<Atom> body, CloseableIteratorWithoutException<Atom> head) {
		this(label, new LinkedListAtomSet(body), new LinkedListAtomSet(head));
	}

	public DefaultRule(InMemoryAtomSet body, InMemoryAtomSet head) {
		this("", body, head);
	}

	public DefaultRule(String label, InMemoryAtomSet body, InMemoryAtomSet head) {
		this.label = label;
		this.body = body;
		this.head = head;
	}

	// copy constructor
	public DefaultRule(Rule rule) {
		this(rule.getLabel(), rule.getBody().iterator(), rule.getHead().iterator());
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public InMemoryAtomSet getBody() {
		return this.body;
	}

	public void setBody(InMemoryAtomSet b) {
		this.body = b;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public InMemoryAtomSet getHead() {
		return this.head;
	}

	public void setHead(InMemoryAtomSet h) {
		this.head = h;
	}

	@Override
	public Set<Term> getTerms() {
		if(this.terms == null) {
			this.terms = new HashSet<Term>();
			this.terms.addAll(this.getBody().getTerms());
			this.terms.addAll(this.getHead().getTerms());
		}
		return this.terms;
	}
	
	@Override
	public Set<Variable> getVariables() {
		if(this.variables == null) {
			this.variables = new HashSet<Variable>();
			this.variables.addAll(this.getBody().getVariables());
			this.variables.addAll(this.getHead().getVariables());
		}
		return this.variables;
	}
	
	@Override
	public Set<Constant> getConstants() {
		if(this.constants == null) {
			this.constants = new HashSet<Constant>();
			this.constants.addAll(this.getBody().getConstants());
			this.constants.addAll(this.getHead().getConstants());
		}
		return this.constants;
	}
	
	@Override
	public Set<Literal> getLiterals() {
		if(this.literals == null) {
			this.literals = new HashSet<Literal>();
			this.literals.addAll(this.getBody().getLiterals());
			this.literals.addAll(this.getHead().getLiterals());
		}
		return this.literals;
	}

	@Override
	@Deprecated
	public Set<Term> getTerms(Term.Type type) {
		Set<Term> terms = new HashSet<Term>();
		terms.addAll(this.getBody().getTerms(type));
		terms.addAll(this.getHead().getTerms(type));
		return terms;
	}

	@Override
	public Set<Variable> getFrontier() {
		if (this.frontier == null) {
			this.computeFrontierAndExistentials();
		}

		return this.frontier;
	}

	@Override
	public Set<Variable> getExistentials() {
		if (this.existentials == null) {
			this.computeFrontierAndExistentials();
		}

		return this.existentials;
	}


	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void computeFrontierAndExistentials() {
		this.frontier = new TreeSet<Variable>();
		this.existentials = new TreeSet<Variable>();
		Collection<Variable> body = this.getBody().getVariables();

		for (Variable termHead : this.getHead().getVariables()) {
			boolean isExistential = true;
			for (Variable termBody : body) {
				if (termBody.equals(termHead)) {
					this.frontier.add(termHead);
					isExistential = false;
				}
			}
			if (isExistential) {
				this.existentials.add(termHead);
			}
		}
	}

}
