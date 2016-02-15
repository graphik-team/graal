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
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.AbstractAtom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
class AtomEdge extends AbstractAtom implements Edge {

	private PredicateVertex  predicate;
	private List<TermVertex> terms;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param predicate
	 * @param terms
	 */
	public AtomEdge(PredicateVertex predicate, List<TermVertex> terms) {
		this.predicate = predicate;
		this.terms = terms;
	}

	// /////////////////////////////////////////////////////////////////////////
	// IATOM METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void setPredicate(Predicate predicate) {
		this.predicate = new PredicateVertex(predicate);
	}

	@Override
	public Predicate getPredicate() {
		return this.predicate;
	}

	@Override
	public void setTerm(int index, Term term) {
		this.terms.set(index, TermVertexFactory.instance().createTerm(term));
	}

	@Override
	public TermVertex getTerm(int index) {
		return this.terms.get(index);
	}

	@Override
	public int indexOf(Term t) {
		return this.terms.indexOf(t);
	}

	@Override
	public boolean contains(Term t) {
		return this.terms.contains(t);
	}

	@Override
	public List<Term> getTerms() {
		return new LinkedList<Term>(this.terms);
	}

	@Override
	public Collection<Term> getTerms(Type type) {
		Collection<Term> typedTerms = new LinkedList<Term>();
		for (Term term : this.terms)
			if (type.equals(term.getType()))
				typedTerms.add(term);

		return typedTerms;
	}

	// /////////////////////////////////////////////////////////////////////////
	// EDGE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Set<Vertex> getVertices() {
		Set<Vertex> set = new TreeSet<Vertex>(new VertexComparator());
		set.addAll(terms);
		set.add(predicate);
		return set;
	}

}
