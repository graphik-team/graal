/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractSubstitution implements Substitution {

	protected abstract Map<Term, Term> getMap();

	@Override
	public Set<Term> getTerms() {
		return this.getMap().keySet();
	}

	@Override
	public Set<Term> getValues() {
		return new TreeSet<Term>(this.getMap().values());
	}

	@Override
	public Term createImageOf(Term term) {
		Term substitut = this.getMap().get(term);
		return (substitut == null) ? term : substitut;
	}

	@Override
	public boolean put(Term term, Term substitut) {
		if (term.isConstant() && substitut.isConstant()
				&& !term.equals(substitut)) {
			return false;
		}
		this.getMap().put(term, substitut);
		return true;
	}

	@Override
	public void put(Substitution substitution) {
		for (Term term : substitution.getTerms()) {
			this.put(term, substitution.createImageOf(term));
		}
	}

	@Override
	public Atom createImageOf(Atom atom) {
		List<Term> termsSubstitut = new LinkedList<Term>();
		for (Term term : atom.getTerms())
			termsSubstitut.add(this.createImageOf(term));

		return new DefaultAtom(atom.getPredicate(), termsSubstitut);
	}

	@Override
	public InMemoryAtomSet createImageOf(AtomSet src) {
		InMemoryAtomSet dest = AtomSetFactory.instance().createAtomSet();
		this.apply(src, dest);
		return dest;
	}

	@Override
	public void apply(AtomSet src, AtomSet dest) throws AtomSetException {
		for (Atom a : src) {
			dest.add(this.createImageOf(a));
		}
	}

	@Override
	public void apply(AtomSet src, InMemoryAtomSet dest) {
		for (Atom a : src) {
			dest.add(this.createImageOf(a));
		}
	}

	@Override
	public Rule createImageOf(Rule rule) {
		Rule substitut = RuleFactory.instance().create();
		this.apply(rule.getBody(), substitut.getBody());
		this.apply(rule.getHead(), substitut.getHead());
		return substitut;
	}

	@Override
	public boolean compose(Term term, Term substitut) {
		Term termSubstitut = this.createImageOf(term);
		Term substitutSubstitut = this.createImageOf(substitut);

		if (Term.Type.CONSTANT.equals(termSubstitut.getType())) {
			Term tmp = termSubstitut;
			termSubstitut = substitutSubstitut;
			substitutSubstitut = tmp;
		}

		for (Term t : this.getTerms()) {
			if (termSubstitut.equals(this.createImageOf(t))) {
				if (!this.put(t, substitutSubstitut)) {
					return false;
				}
			}
		}

		if (!this.put(termSubstitut, substitutSubstitut)) {
			return false;
		}
		return true;
	}

	@Override
	public Substitution compose(Substitution s) {
		Substitution newSub = SubstitutionFactory.instance()
				.createSubstitution();
		for (Term term : this.getTerms()) {
			if (!newSub.compose(term, this.createImageOf(term))) {
				return null;
			}
		}
		for (Term term : s.getTerms()) {
			if (!newSub.compose(term, s.createImageOf(term))) {
				return null;
			}
		}
		return newSub;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.appendTo(sb);
		return sb.toString();
	}

	@Override
	public void appendTo(StringBuilder sb) {
		boolean first = true;
		sb.append('{');
		for (Term key : this.getTerms()) {
			if (first)
				first = false;
			else
				sb.append(',');
			sb.append(key).append("->");
			sb.append(this.createImageOf(key));
		}
		sb.append('}');
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Substitution)) {
			return false;
		}
		return this.equals((Substitution) obj);
	}

	// TODO to check and improve
	public boolean equals(Substitution other) { // NOPMD
		Set<Term> termsThis = new TreeSet<Term>();
		termsThis.addAll(this.getTerms());
		termsThis.addAll(this.getValues());

		Set<Term> termsOther = new TreeSet<Term>();
		termsOther.addAll(other.getTerms());
		termsOther.addAll(other.getValues());

		if (!termsThis.equals(termsOther)) {
			return false;
		}

		for (Term t1 : termsThis) {
			for (Term t2 : termsThis) {
				boolean a = this.createImageOf(t1).equals(this.createImageOf(t2));
				boolean b = other.createImageOf(t1).equals(other.createImageOf(t2));
				if (a != b) {
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public int compareTo(Substitution other) {
		Set<Term> set = this.getTerms();
		Set<Term> otherset = other.getTerms();
		if (set.size() != otherset.size()) {
			return set.size() - otherset.size();
		}
		for (Term t : set) {
			int val = this.createImageOf(t).compareTo(other.createImageOf(t));
			if (val != 0) {
				return val;
			}
		}
		return 0;
	}

};
