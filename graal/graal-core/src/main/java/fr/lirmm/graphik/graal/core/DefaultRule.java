/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class DefaultRule implements Rule {

	private String label;
	private final InMemoryAtomSet body;
	private final InMemoryAtomSet head;

	private Set<Term> terms = null;
	private Set<Term> frontier = null;
	private Set<Term> existentials = null;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DefaultRule() {
		this("", new LinkedListAtomSet(), new LinkedListAtomSet());
	}

	public DefaultRule(Iterator<Atom> body, Iterator<Atom> head) {
		this("", new LinkedListAtomSet(body), new LinkedListAtomSet(head));

	}

	public DefaultRule(Iterable<Atom> body, Iterable<Atom> head) {
		this("", body, head);
	}

	public DefaultRule(String label, Iterable<Atom> body, Iterable<Atom> head) {
		this.label = label;
		LinkedListAtomSet atomSet = new LinkedListAtomSet();
		atomSet.addAll(body);
		this.body = atomSet;

		atomSet = new LinkedListAtomSet();
		atomSet.addAll(head);

		this.head = atomSet;
	}

	// copy constructor
	public DefaultRule(Rule rule) {
		this(rule.getLabel(), new LinkedListAtomSet(rule.getBody()),
				new LinkedListAtomSet(rule.getHead()));
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public InMemoryAtomSet getBody() {
		return this.body;
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

	@Override
	public Set<Term> getTerms() {
		if(this.terms == null) {
			this.terms = new TreeSet<Term>();
			this.terms.addAll(this.getBody().getTerms());
			this.terms.addAll(this.getHead().getTerms());
		}
		return this.terms;
	}

	@Override
	public Set<Term> getTerms(Term.Type type) {
		Set<Term> terms = new TreeSet<Term>();
		terms.addAll(this.getBody().getTerms(type));
		terms.addAll(this.getHead().getTerms(type));
		return terms;
	}

	@Override
	public Set<Term> getFrontier() {
		if (this.frontier == null) {
			this.computeFrontierAndExistentials();
		}

		return this.frontier;
	}

	@Override
	public Set<Term> getExistentials() {
		if (this.existentials == null) {
			this.computeFrontierAndExistentials();
		}

		return this.existentials;
	}


	@Override
	public int compareTo(Rule other) {
		return this.label.compareTo(other.getLabel());
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		this.appendTo(builder);
		return builder.toString();
	}
	
	@Override
	public void appendTo(StringBuilder builder) {
		if (!this.label.isEmpty()) {
			builder.append('[');
			builder.append(this.label);
			builder.append("] ");
		}
		builder.append(this.body.toString());
		builder.append(" -> ");
		builder.append(this.head);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Rule)) {
			return false;
		}
		return this.equals((Rule) obj);
	}

	public boolean equals(Rule other) { // NOPMD
		if(this.label.compareTo(other.getLabel()) != 0)
			return false;
		if(!other.getHead().equals(this.getHead()))
			return false;
		if(!other.getBody().equals(this.getBody()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return this.label.hashCode();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void computeFrontierAndExistentials() {
		this.frontier = new TreeSet<Term>();
		this.existentials = new TreeSet<Term>();
		Collection<Term> body = this.getBody().getTerms(Type.VARIABLE);

		for (Term termHead : this.getHead().getTerms(Type.VARIABLE)) {
			boolean isExistential = true;
			for (Term termBody : body) {
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
