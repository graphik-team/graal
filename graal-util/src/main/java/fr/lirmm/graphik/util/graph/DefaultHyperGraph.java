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
package fr.lirmm.graphik.util.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DefaultHyperGraph implements HyperGraph {

	private ArrayList<LinkedList<Integer>> adjacencyList;
	private int                 nbVertices;

	public DefaultHyperGraph() {
		this.nbVertices = 0;
		this.adjacencyList = new ArrayList<LinkedList<Integer>>();
	}

	public DefaultHyperGraph(int nbVerticesInit) {
		this.nbVertices = nbVerticesInit;
		this.adjacencyList = new ArrayList<LinkedList<Integer>>(nbVerticesInit);
		for (int i = 0; i < nbVertices; ++i) {
			this.adjacencyList.add(new LinkedList<Integer>());
		}
	}

	@Override
	public int nbVertices() {
		return nbVertices;
	}

	@Override
	public int addVertex() {
		this.adjacencyList.add(new LinkedList<Integer>());
		return this.nbVertices++;
	}

	@Override
	public Iterator<Integer> adjacencyList(int v) {
		return this.adjacencyList.get(v).iterator();
	}

	@Override
	public void add(HyperEdge e) {
		Iterator<Integer> it1 = e.vertices();
		Iterator<Integer> it2;
		int v1, v2;

		while (it1.hasNext()) {
			v1 = it1.next();
			it2 = e.vertices();
			while (it2.hasNext()) {
				v2 = it2.next();
				if (v1 != v2) {
					this.adjacencyList.get(v1).add(v2);
				}
			}

		}
	}

	@Override
	public void addEdge(int... vertices) {
		for (int i = 0; i < vertices.length - 1; ++i) {
			for (int j = i + 1; j < vertices.length; ++j) {
				this.adjacencyList.get(vertices[i]).add(vertices[j]);
				this.adjacencyList.get(vertices[j]).add(vertices[i]);
			}
		}
	}

}
