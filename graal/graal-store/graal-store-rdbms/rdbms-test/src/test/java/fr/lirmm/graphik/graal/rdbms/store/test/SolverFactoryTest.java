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
package fr.lirmm.graphik.graal.rdbms.store.test;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlUCQHomomorphism;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
@RunWith(Theories.class)
public class SolverFactoryTest {

	@DataPoints
	public static Store[] atomset() {
		return TestUtil.getStores();
	}

	@Theory
	public void testSqlSolver(Store atomSet) throws IOException, AtomSetException,
	    HomomorphismFactoryException, SQLException {

		Query query = DlgpParser.parseQuery("?(X) :- p(X).");
		Homomorphism solver = StaticHomomorphism.getSolverFactory().getSolver(query, atomSet);
		Assert.assertTrue(solver instanceof SqlHomomorphism);
	}

	@Theory
	public void testUnionConjunctiveQuery(Store atomSet)
	    throws IOException, AtomSetException,
	    HomomorphismFactoryException, SQLException {

		ConjunctiveQuery query1 = DlgpParser.parseQuery("?(X) :- p(X).");
		ConjunctiveQuery query2 = DlgpParser.parseQuery("?(Y) :- q(Y).");
		DefaultUnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(query1.getAnswerVariables(), query1,
				query2);

		Homomorphism solver = StaticHomomorphism.getSolverFactory().getSolver(ucq, atomSet);
		Assert.assertTrue(solver instanceof SqlUCQHomomorphism);
	}

}
