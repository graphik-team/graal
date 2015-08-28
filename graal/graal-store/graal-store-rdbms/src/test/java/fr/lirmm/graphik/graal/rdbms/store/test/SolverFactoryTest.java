/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.rdbms.store.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.impl.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.UnionConjunctiveQueriesHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class SolverFactoryTest {

	@Test
	public void testSqlSolver() throws IOException, AtomSetException,
			HomomorphismFactoryException, DriverException {
		AtomSet atomSet = TestUtil.getStore();

		Query query = DlgpParser.parseQuery("?(X) :- p(X).");
		Homomorphism solver = StaticHomomorphism.getSolverFactory().getSolver(query, atomSet);
		Assert.assertTrue(solver instanceof SqlHomomorphism);
	}

	@Test
	public void testUnionConjunctiveQuery() throws IOException, AtomSetException,
			HomomorphismFactoryException, DriverException {
		AtomSet atomSet = TestUtil.getStore();

		ConjunctiveQuery query1 = DlgpParser.parseQuery("?(X) :- p(X).");
		ConjunctiveQuery query2 = DlgpParser.parseQuery("?(Y) :- q(Y).");
		UnionConjunctiveQueries ucq = new UnionConjunctiveQueries(query1,
				query2);

		Homomorphism solver = StaticHomomorphism.getSolverFactory().getSolver(ucq, atomSet);
		Assert.assertTrue(solver instanceof UnionConjunctiveQueriesHomomorphism);
	}

}
