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
package fr.lirmm.graphik.graal.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Assert;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.factory.AtomFactory;
import fr.lirmm.graphik.graal.core.atomset.graph.AtomEdgeFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
@RunWith(Theories.class)
public class AtomTest {

	@DataPoints
	public static AtomFactory[] getAtomset() {
		AtomFactory[] factories = { DefaultAtomFactory.instance(), AtomEdgeFactory.instance() };
		return factories;
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#getConstants()}.
	 */
	@Theory
	public void testGetConstants(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXA);

		// when
		Set<Constant> constants = a.getConstants();

		// then
		Assert.assertEquals(1, constants.size());
		Assert.assertTrue(constants.contains(TestUtils.A));
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#getVariables()}.
	 */
	@Theory
	public void testGetVariables(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXA);

		// when
		Set<Variable> variables = a.getVariables();

		// then
		Assert.assertEquals(1, variables.size());
		Assert.assertTrue(variables.contains(TestUtils.X));
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#getLiterals()}.
	 */
	@Theory
	public void testGetLiterals(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXONE);

		// when
		Set<Literal> literals = a.getLiterals();

		// then
		Assert.assertEquals(1, literals.size());
		Assert.assertTrue(literals.contains(TestUtils.ONE));
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#iterator()}.
	 */
	@Theory
	public void testIterator(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXA);

		// when
		Iterator<Term> it = a.iterator();

		// then
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals(TestUtils.X, it.next());
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals(TestUtils.A, it.next());
		Assert.assertFalse(it.hasNext());
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#DefaultAtom(fr.lirmm.graphik.graal.api.core.Predicate)}.
	 */
	@Theory
	public void testDefaultAtomPredicate(AtomFactory factory) {
		// given
		Predicate p = TestUtils.p;

		// when
		Atom a = factory.create(p);

		// then
		Assert.assertEquals(p, a.getPredicate());
		Assert.assertNull(a.getTerm(0));
		Assert.assertNull(a.getTerm(1));
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#DefaultAtom(fr.lirmm.graphik.graal.api.core.Predicate, java.util.List)}.
	 */
	@Theory
	public void testDefaultAtomPredicateListOfTerm(AtomFactory factory) {
		// given
		Predicate p = TestUtils.p;
		List<Term> terms = new ArrayList<>();
		terms.add(TestUtils.A);
		terms.add(TestUtils.X);

		// when
		Atom a = factory.create(p, terms);

		// then
		Assert.assertEquals(p, a.getPredicate());
		Assert.assertEquals(TestUtils.A, a.getTerm(0));
		Assert.assertEquals(TestUtils.X, a.getTerm(1));
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#DefaultAtom(fr.lirmm.graphik.graal.api.core.Predicate, fr.lirmm.graphik.graal.api.core.Term[])}.
	 */
	@Theory
	public void testDefaultAtomPredicateTermArray(AtomFactory factory) {
		// given
		Predicate p = TestUtils.p;

		// when
		Atom a = factory.create(p, TestUtils.A, TestUtils.X);

		// then
		Assert.assertEquals(p, a.getPredicate());
		Assert.assertEquals(TestUtils.A, a.getTerm(0));
		Assert.assertEquals(TestUtils.X, a.getTerm(1));
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#DefaultAtom(fr.lirmm.graphik.graal.api.core.Atom)}.
	 */
	@Theory
	public void testDefaultAtomAtom(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXA);

		// when
		Atom copy = new DefaultAtom(a);

		// then
		Assert.assertEquals(a.getPredicate(), copy.getPredicate());
		Assert.assertEquals(a.getTerm(0), copy.getTerm(0));
		Assert.assertEquals(a.getTerm(1), copy.getTerm(1));
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#contains(fr.lirmm.graphik.graal.api.core.Term)}.
	 */
	@Theory
	public void testContainsTrue(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXA);

		// when
		boolean contains = a.contains(TestUtils.X);

		// then
		Assert.assertTrue(contains);
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#contains(fr.lirmm.graphik.graal.api.core.Term)}.
	 */
	@Theory
	public void testContainsFalse(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXA);

		// when
		boolean contains = a.contains(TestUtils.B);

		// then
		Assert.assertFalse(contains);
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#indexOf(fr.lirmm.graphik.graal.api.core.Term)}.
	 */
	@Theory
	public void testIndexOf(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXA);

		// when
		int index = a.indexOf(TestUtils.A);

		// then
		Assert.assertEquals(1, index);
	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#indexOf(fr.lirmm.graphik.graal.api.core.Term)}.
	 */
	@Theory
	public void testIndexOfNotFound(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXA);

		// when
		int index = a.indexOf(TestUtils.B);

		// then
		Assert.assertEquals(-1, index);
	}

	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#indexesOf(fr.lirmm.graphik.graal.api.core.Term)}.
	 */
	@Theory
	public void testIndexesOf(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXX);

		// when
		int[] indexes = a.indexesOf(TestUtils.X);

		// then
		Assert.assertEquals(2, indexes.length);
		Assert.assertTrue(ArrayUtils.contains(indexes, 0));
		Assert.assertTrue(ArrayUtils.contains(indexes, 1));

	}
	
	/**
	 * Test method for
	 * {@link fr.lirmm.graphik.graal.core.DefaultAtom#indexesOf(fr.lirmm.graphik.graal.api.core.Term)}.
	 */
	@Theory
	public void testIndexesOfNotFound(AtomFactory factory) {
		// given
		Atom a = factory.create(TestUtils.pXX);

		// when
		int[] indexes = a.indexesOf(TestUtils.A);

		// then
		Assert.assertEquals(0, indexes.length);

	}

}
