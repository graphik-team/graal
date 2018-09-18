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
package fr.lirmm.graphik.graal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractSubstitution implements Substitution {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public List<Term> createImageOf(Collection<? extends Term> terms) {
		List<Term> l = new ArrayList<Term>(terms.size());
		for (Term t : terms) {
			l.add(this.createImageOf(t));
		}
		return l;
	}
	
	@Override
	public boolean put(Substitution substitution) {
		for (Variable term : substitution.getTerms()) {
			if (!this.put(term, substitution.createImageOf(term))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Atom createImageOf(Atom atom) {
		List<Term> terms = atom.getTerms();
		Term[] termsSubstitut = new Term[terms.size()];
		int i = -1;
		for (Term term : atom)
			termsSubstitut[++i] = this.createImageOf(term);

		return new DefaultAtom(atom.getPredicate(), termsSubstitut);
	}
	
	@Override
	public InMemoryAtomSet createImageOf(AtomSet src) throws AtomSetException {
		InMemoryAtomSet dest = DefaultAtomSetFactory.instance().create();
		this.apply(src, dest);
		return dest;
	}

	@Override
	public InMemoryAtomSet createImageOf(InMemoryAtomSet src) {
		InMemoryAtomSet dest = DefaultAtomSetFactory.instance().create();
		this.apply(src, dest);
		return dest;
	}
	
	@Override
	public void apply(AtomSet src, AtomSet dest) throws AtomSetException {
		CloseableIterator<Atom> it = src.iterator();
		try {
			while (it.hasNext()) {
				Atom a = it.next();
				dest.add(this.createImageOf(a));
			}
		} catch (IteratorException e) {
			throw new AtomSetException("Error during the iteration over src", e);
		}
	}
	

	@Override
	public void apply(InMemoryAtomSet src, InMemoryAtomSet dest) {
		CloseableIteratorWithoutException<Atom> it = src.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			dest.add(this.createImageOf(a));
		}
	}

	@Override
	public Rule createImageOf(Rule rule) {
		Rule substitut = DefaultRuleFactory.instance().create();
		this.apply(rule.getBody(), substitut.getBody());
		this.apply(rule.getHead(), substitut.getHead());
		return substitut;
	}
	
	@Override
	public ConjunctiveQuery createImageOf(ConjunctiveQuery cq) {
		List<Term> ans = this.createImageOf(cq.getAnswerVariables());
		InMemoryAtomSet body = this.createImageOf(cq.getAtomSet());
		return DefaultConjunctiveQueryFactory.instance().create(body, ans);
	}


	@Override
	public boolean compose(Variable term, Term substitut) {
		substitut = this.createImageOf(substitut);
		return this.put(term, substitut);
	}

	@Override
	public Substitution compose(Substitution s) {
		Substitution newSub = DefaultSubstitutionFactory.instance().createSubstitution(this);
		for (Variable term : s.getTerms()) {
			if (!newSub.compose(term, s.createImageOf(term))) {
				return null;
			}
		}
		return newSub;
	}
	
	@Override
	public Substitution aggregate(Substitution s) {
		Substitution newSub = DefaultSubstitutionFactory.instance().createSubstitution(this);
		for (Variable term : s.getTerms()) {
			if (!newSub.aggregate(term, s.createImageOf(term))) {
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
		Set<Term> termsThis = new HashSet<Term>();
		termsThis.addAll(this.getTerms());
		termsThis.addAll(this.getValues());

		Set<Term> termsOther = new HashSet<Term>();
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
		Set<Variable> set = this.getTerms();
		Set<Variable> otherset = other.getTerms();
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


}
