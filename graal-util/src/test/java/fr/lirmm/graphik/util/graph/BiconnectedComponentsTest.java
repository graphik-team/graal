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

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.util.graph.algorithm.BiconnectedComponents;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BiconnectedComponentsTest {

	/**
	 * Test on example from Tarjan 1972, depth-first search and linear graph
	 * algorithms
	 */
	@Test
	public void test1() {
		Graph g = new DefaultGraph(9);
		g.addPath(0, 1, 2, 3, 1, 4, 5, 0, 6, 7, 8, 6);

		Set<Integer> c1 = new TreeSet<Integer>();
		Set<Integer> c2 = new TreeSet<Integer>();
		Set<Integer> c3 = new TreeSet<Integer>();
		Set<Integer> c4 = new TreeSet<Integer>();

		c1.add(0);
		c1.add(6);

		c2.add(1);
		c2.add(2);
		c2.add(3);

		c3.add(6);
		c3.add(7);
		c3.add(8);

		c4.add(0);
		c4.add(1);
		c4.add(4);
		c4.add(5);

		List<Set<Integer>> components = BiconnectedComponents.execute(g);
		Assert.assertEquals(4, components.size());
		for (Set<Integer> c : components) {
			Assert.assertTrue(c.equals(c1) || c.equals(c2) || c.equals(c3) || c.equals(c4));

		}
	}

	@Test
	public void testEmptyGraph() {
		Graph g = new DefaultGraph(0);

		List<Set<Integer>> components = BiconnectedComponents.execute(g);
		Assert.assertEquals(0, components.size());
	}

	@Test
	public void testClique() {
		Graph g = new DefaultGraph(10);
		Set<Integer> c1 = new TreeSet<Integer>();
		for (int i = 0; i < 10; ++i) {
			c1.add(i);
			for (int j = i + 1; j < 10; ++j) {
				g.addEdge(i, j);
			}
		}

		List<Set<Integer>> components = BiconnectedComponents.execute(g);
		Assert.assertEquals(1, components.size());
		for (Set<Integer> c : components) {
			Assert.assertEquals(c1, c);
		}
	}

}
