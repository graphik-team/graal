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
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * This class represents query which is the union of conjunctive queries.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultUnionOfConjunctiveQueries implements UnionOfConjunctiveQueries {

	private String label = "";
	private Collection<ConjunctiveQuery> queries;
	private List<Term> ans;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultUnionOfConjunctiveQueries() {
		this.queries = new LinkedList<ConjunctiveQuery>();
		this.ans = new LinkedList<Term>();
	}

	public DefaultUnionOfConjunctiveQueries(List<Term> ans) {
		this.ans = ans;
		this.queries = new LinkedList<ConjunctiveQuery>();
	}

	public DefaultUnionOfConjunctiveQueries(List<Term> ans, Collection<ConjunctiveQuery> queries) {
		this.ans = ans;
		this.queries = queries;
	}

	public DefaultUnionOfConjunctiveQueries(List<Term> ans,
			CloseableIteratorWithoutException<ConjunctiveQuery> queries) {
		this.ans = ans;
		this.queries = new LinkedList<ConjunctiveQuery>();
		while (queries.hasNext()) {
			this.queries.add(queries.next());
		}
		queries.close();
	}

	public DefaultUnionOfConjunctiveQueries(List<Term> ans, CloseableIterator<ConjunctiveQuery> queries) throws IteratorException {
		this.ans = ans;
		this.queries = new LinkedList<ConjunctiveQuery>();
		while (queries.hasNext()) {
			this.queries.add(queries.next());
		}
		queries.close();
	}

	public DefaultUnionOfConjunctiveQueries(List<Term> ans, ConjunctiveQuery... queries) {
		this.ans = ans;
		this.queries = new LinkedList<ConjunctiveQuery>();
		for (ConjunctiveQuery cq : queries)
			this.queries.add(cq);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public List<Term> getAnswerVariables() {
		return this.ans;
	}

	public void setAnswerVariables(List<Term> ans) {
		this.ans = ans;
	}

	public boolean add(ConjunctiveQuery cquery) {
		return this.queries.add(cquery);
	}

	public boolean addAll(Collection<? extends ConjunctiveQuery> queries) {
		return this.queries.addAll(queries);
	}

	public void clear() {
		this.queries.clear();
	}

	@Override
	public CloseableIteratorWithoutException<ConjunctiveQuery> iterator() {
		return new CloseableIteratorAdapter<ConjunctiveQuery>(this.queries.iterator());
	}

	public boolean isEmpty() {
		return this.queries.isEmpty();
	}

	public boolean remove(ConjunctiveQuery o) {
		return this.queries.remove(o);
	}

	public int size() {
		return this.queries.size();
	}

	@Override
	public boolean isBoolean() {
		return this.queries.isEmpty() || this.queries.iterator().next().isBoolean();
	}

	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.appendTo(sb);
		return sb.toString();
	}

	@Override
	public void appendTo(StringBuilder sb) {
		for (Query q : this.queries) {
			sb.append(q);
			sb.append("\n| ");
		}
	}

}