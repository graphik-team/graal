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
package fr.lirmm.graphik.graal.core.atomset.graph;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.api.core.AbstractTerm;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.util.stream.GIterator;
import fr.lirmm.graphik.util.stream.IteratorAdapter;

abstract class AbstractTermVertex extends AbstractTerm implements TermVertex {

	private static final long serialVersionUID = -1087277093687686210L;

	// private final TreeSet<Edge> edges = new TreeSet<Edge>();
	private final TreeMap<Predicate, Collection<Atom>[]> index            = new TreeMap<Predicate, Collection<Atom>[]>();

	// /////////////////////////////////////////////////////////////////////////
	// ABSTRACT METHODS
	// /////////////////////////////////////////////////////////////////////////

	protected abstract Term getTerm();

	// /////////////////////////////////////////////////////////////////////////
	// VERTEX METHODS
	// /////////////////////////////////////////////////////////////////////////

	// @Override
	// public Set<Edge> getEdges() {
	// return this.edges;
	// }

	@Override
	public GIterator<Atom> getNeighbors(Predicate p, int position) {
		Iterator<Atom> it = null;
		Collection<Atom>[] map = this.index.get(p);
		if(map != null) {
			Collection<Atom> collection = map[position];
			if(collection != null) {
				it = collection.iterator();
			}
		}
		if (it == null) {
			it = Collections.<Atom> emptyIterator();
		}
		return new IteratorAdapter<Atom>(it);
	}

	@Override
	public void add(Atom a) {
		Collection<Atom>[] map = this.index.get(a.getPredicate());
		if (map == null) {
			map = new Collection[a.getPredicate().getArity()];
			this.index.put(a.getPredicate(), map);
		}

		int i = -1;
		for (Term t : a) {
			++i;
			if (this.equals(t)) {
				Collection<Atom> collection = map[i];
				if (collection == null) {
					collection = new LinkedList<Atom>();
					map[i] = collection;
				}
				collection.add(a);
			}
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// TERM METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isConstant() {
		return this.getTerm().isConstant();
	}

	@Override
	public Type getType() {
		return this.getTerm().getType();
	}

	@Override
	public String toString() {
		return this.getTerm().toString();
	}

}
