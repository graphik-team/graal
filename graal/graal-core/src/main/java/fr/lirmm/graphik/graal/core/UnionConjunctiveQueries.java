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
import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;

/**
 * This class represents query which is the union of conjunctive queries.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class UnionConjunctiveQueries implements Query,
		Collection<ConjunctiveQuery> {

	private String label = "";
	private Collection<ConjunctiveQuery> queries;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public UnionConjunctiveQueries() {
		this.queries = new LinkedList<ConjunctiveQuery>();
	}

	public UnionConjunctiveQueries(Collection<ConjunctiveQuery> queries) {
		this.queries = queries;
	}

	public UnionConjunctiveQueries(Iterator<ConjunctiveQuery> queries) {
		this.queries = new LinkedList<ConjunctiveQuery>();
		while (queries.hasNext()) {
			this.queries.add(queries.next());
		}
	}

	public UnionConjunctiveQueries(ConjunctiveQuery... queries) {
		this.queries = new LinkedList<ConjunctiveQuery>();
		for (ConjunctiveQuery cq : queries)
			this.queries.add(cq);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean add(ConjunctiveQuery cquery) {
		return this.queries.add(cquery);
	}

	@Override
	public boolean addAll(Collection<? extends ConjunctiveQuery> queries) {
		return this.queries.addAll(queries);
	}

	@Override
	public void clear() {
		this.queries.clear();
	}

	@Override
	public Iterator<ConjunctiveQuery> iterator() {
		return this.queries.iterator();
	}

	@Override
	public boolean isEmpty() {
		return this.queries.isEmpty();
	}

	@Override
	public boolean remove(Object o) {
		return this.queries.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.queries.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.queries.retainAll(c);
	}

	@Override
	public int size() {
		return this.queries.size();
	}

	@Override
	public Object[] toArray() {
		return this.queries.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.queries.toArray(a);
	}

	@Override
	public boolean contains(Object o) {
		return this.queries.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.queries.containsAll(c);
	}

	@Override
	public boolean isBoolean() {
		return this.queries.isEmpty()
				|| this.queries.iterator().next().isBoolean();
	}

	public void setLabel(String label) {
		this.label = label;
	}

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
			sb.append(" | ");
		}
	}

}