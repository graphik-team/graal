/**
 * 
 */
package fr.lirmm.graphik;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.homomorphism.UnionConjunctiveQueriesHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlpParser;
import fr.lirmm.graphik.graal.store.homomorphism.SqlHomomorphism;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.DriverException;
import fr.lirmm.graphik.graal.store.rdbms.driver.SqliteDriver;
import fr.lirmm.graphik.graal.transformation.ToTripleTransformation;
import fr.lirmm.graphik.graal.transformation.TransformAtomSet;
import fr.lirmm.graphik.graal.transformation.TransformatorSolver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class SolverFactoryTest {

	@Test
	public void testSqlSolver() throws IOException, AtomSetException,
			HomomorphismFactoryException, DriverException {

		File file = new File(TestUtil.DB_TEST);
		file.delete();
		file.createNewFile();

		AtomSet atomSet = new DefaultRdbmsStore(new SqliteDriver(file));

		Query query = DlpParser.parseQuery("?(X) :- p(X).");
		Homomorphism solver = StaticHomomorphism.getSolverFactory().getSolver(query, atomSet);
		Assert.assertTrue(solver instanceof SqlHomomorphism);
	}

	@Test
	public void testUnionConjunctiveQuery() throws IOException, AtomSetException,
			HomomorphismFactoryException, DriverException {

		File file = new File(TestUtil.DB_TEST);
		file.delete();
		file.createNewFile();

		AtomSet atomSet = new DefaultRdbmsStore(new SqliteDriver(file));

		ConjunctiveQuery query1 = DlpParser.parseQuery("?(X) :- p(X).");
		ConjunctiveQuery query2 = DlpParser.parseQuery("?(Y) :- q(Y).");
		UnionConjunctiveQueries ucq = new UnionConjunctiveQueries(query1,
				query2);

		Homomorphism solver = StaticHomomorphism.getSolverFactory().getSolver(ucq, atomSet);
		Assert.assertTrue(solver instanceof UnionConjunctiveQueriesHomomorphism);
	}
	
	@Test
	public void testTransformAtomSet() throws IOException, AtomSetException,
			HomomorphismFactoryException {

		File file = new File(TestUtil.DB_TEST);
		file.delete();
		file.createNewFile();

		AtomSet atomSet = new TransformAtomSet(new LinkedListAtomSet(), new ToTripleTransformation(new DefaultFreeVarGen("gen_")));

		ConjunctiveQuery query = DlpParser.parseQuery("?(X) :- p(X).");


		Homomorphism solver = StaticHomomorphism.getSolverFactory().getSolver(query, atomSet);
		Assert.assertTrue(solver instanceof TransformatorSolver);
	}
	
}
