/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2018)
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
package fr.lirmm.graphik.graal.rdbms.store.test;

import org.junit.Assert;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultPredicateFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.rdbms.RdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.homomorphism.SqlHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@RunWith(Theories.class)
public class SQLHomomorphismTest {

	@DataPoints
	public static RdbmsStore[] atomset() {
		return TestUtil.getStores();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Theory
	public void simpleTest(RdbmsStore store) throws AtomSetException, IteratorException, ParseException, HomomorphismException {
		Constant a = DefaultTermFactory.instance().createConstant("a");
		Constant b = DefaultTermFactory.instance().createConstant("b");
		Constant c = DefaultTermFactory.instance().createConstant("c");
		
		Variable x = DefaultTermFactory.instance().createVariable("X");
		Variable y = DefaultTermFactory.instance().createVariable("Y");
		Variable z = DefaultTermFactory.instance().createVariable("Z");


		store.addAll(DlgpParser.parseAtomSet("<P>(a,b). <Q>(b,c)."));
		
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X,Y,Z) :- <P>(X,Y), <Q>(Y,Z).");
		CloseableIterator<Substitution> results = SqlHomomorphism.instance().execute(query, store);
		
		Assert.assertTrue(results.hasNext());
		Substitution next = results.next();
		Assert.assertEquals(a, next.createImageOf(x));
		Assert.assertEquals(b, next.createImageOf(y));
		Assert.assertEquals(c, next.createImageOf(z));
		
		Assert.assertFalse(results.hasNext());
		results.close();
	}
	
	@Theory
	public void manageUnconventionalVariableName(RdbmsStore store) throws AtomSetException, IteratorException, ParseException, HomomorphismException {
		Constant a = DefaultTermFactory.instance().createConstant("a");
		Constant b = DefaultTermFactory.instance().createConstant("b");
		
		Variable x = DefaultTermFactory.instance().createVariable("1");
		Variable y = DefaultTermFactory.instance().createVariable("Y");

		Predicate p = DefaultPredicateFactory.instance().create("P", 2);
		store.add(DefaultAtomFactory.instance().create(p, a, b));
		
		Atom atom = DefaultAtomFactory.instance().create(p, x, y);
		ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(atom, atom.getTerms());
		
		CloseableIterator<Substitution> results = SqlHomomorphism.instance().execute(query, store);
		
		Assert.assertTrue(results.hasNext());
		Substitution next = results.next();
		Assert.assertEquals(a, next.createImageOf(x));
		Assert.assertEquals(b, next.createImageOf(y));
		
		Assert.assertFalse(results.hasNext());
		results.close();
	}

}
