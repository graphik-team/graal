package fr.lirmm.graphik.util.graph.scc;

import static org.junit.Assert.assertEquals;

import org.apache.commons.graph.model.DirectedMutableGraph;
import org.junit.Test;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class SccTest {

	@Test
	public void test1() {

		DirectedMutableGraph<Integer, Integer> graph = new DirectedMutableGraph<Integer, Integer>();
		graph.addVertex(0);
		graph.addVertex(1);
		graph.addVertex(2);
		graph.addVertex(3);
		graph.addEdge(1, 0, 0);
		graph.addEdge(1, 1, 2);
		graph.addEdge(2, 2, 1);
		graph.addEdge(2, 3, 3);

		TarjanAlgorithm2<Integer> tarjan = new TarjanAlgorithm2<Integer>(graph);
		StronglyConnectedComponentsGraph<Integer> scc = tarjan.perform();

		assertEquals(3, scc.getNbrComponents());
	}

	@Test
	public void test2() {

		DirectedMutableGraph<Integer, Integer> graph = new DirectedMutableGraph<Integer, Integer>();
		graph.addVertex(0);
		graph.addVertex(1);
		graph.addVertex(2);
		graph.addVertex(3);
		graph.addVertex(4);
		graph.addVertex(5);
		graph.addEdge(0, 0, 1);
		graph.addEdge(1, 1, 2);
		graph.addEdge(2, 2, 0);
		graph.addEdge(1, 3, 3);
		graph.addEdge(2, 4, 4);
		graph.addEdge(3, 5, 4);
		graph.addEdge(4, 6, 3);
		graph.addEdge(4, 7, 5);
		graph.addEdge(2, 8, 5);
		TarjanAlgorithm2<Integer> tarjan = new TarjanAlgorithm2<Integer>(graph);
		StronglyConnectedComponentsGraph<Integer> scc = tarjan.perform();

		assertEquals(3, scc.getNbrComponents());

	}

};
