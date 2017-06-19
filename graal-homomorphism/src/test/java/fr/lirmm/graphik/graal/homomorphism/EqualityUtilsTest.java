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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.core.factory.DefaultPredicateFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.homomorphism.utils.EqualityUtils;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class EqualityUtilsTest {

	private static Variable x = DefaultTermFactory.instance().createVariable("X");
	private static Variable y = DefaultTermFactory.instance().createVariable("Y");

	private static Constant a = DefaultTermFactory.instance().createConstant("a");

	private static Predicate p = DefaultPredicateFactory.instance().create("p", 2);

	@Test
	public void test1() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(X,Y) :- b(X), Y=a.");
		Pair<ConjunctiveQuery, Substitution> pair = EqualityUtils.processEquality(q);

		q = pair.getLeft();
		Substitution s = pair.getRight();

		// check substitution
		Assert.assertEquals(1, s.getTerms().size());
		Assert.assertEquals(a, s.createImageOf(y));

		// check query
		Assert.assertEquals(DlgpParser.parseQuery("?(X) :- b(X)."), q);
	}

	@Test
	public void test2() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(X,Y) :- p(X,Y), Y=a.");
		Pair<ConjunctiveQuery, Substitution> pair = EqualityUtils.processEquality(q);

		q = pair.getLeft();
		Substitution s = pair.getRight();

		// check substitution
		Assert.assertEquals(1, s.getTerms().size());
		Assert.assertEquals(a, s.createImageOf(y));

		// check query
		Assert.assertEquals(DlgpParser.parseQuery("?(X) :- p(X,a)."), q);
	}

	@Test
	public void test3() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(X,Y) :- p(X,Y), X=Y.");
		Pair<ConjunctiveQuery, Substitution> pair = EqualityUtils.processEquality(q);

		q = pair.getLeft();
		Substitution s = pair.getRight();

		// check substitution
		Set<Variable> terms = new HashSet<Variable>(s.getTerms());
		terms.remove(x);
		terms.remove(y);
		Assert.assertTrue(terms.isEmpty());
		Assert.assertEquals(s.createImageOf(x), s.createImageOf(y));
		Assert.assertTrue(s.createImageOf(y).isVariable());

		// check query ans part
		Assert.assertEquals(1, q.getAnswerVariables().size());
		Assert.assertEquals(s.createImageOf(x), q.getAnswerVariables().get(0));

		// check query atomset
		Assert.assertEquals(1, Iterators.count(q.getAtomSet().iterator()));
		Atom atom = q.getAtomSet().iterator().next();
		Assert.assertEquals(p, atom.getPredicate());
		Assert.assertEquals(s.createImageOf(x), atom.getTerm(0));
		Assert.assertEquals(s.createImageOf(x), atom.getTerm(1));
	}

	@Test
	public void test4() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(X,Y) :- p(X,Y), X=a, X=Y.");
		Pair<ConjunctiveQuery, Substitution> pair = EqualityUtils.processEquality(q);

		q = pair.getLeft();
		Substitution s = pair.getRight();

		// check substitution minimality
		Set<Variable> terms = new HashSet<Variable>(s.getTerms());
		terms.remove(x);
		terms.remove(y);
		Assert.assertTrue(terms.isEmpty());

		// check substitution completude
		Assert.assertEquals(a, s.createImageOf(y));
		Assert.assertEquals(a, s.createImageOf(y));

		// check query ans part
		Assert.assertEquals(0, q.getAnswerVariables().size());

		// check query atomset
		Assert.assertEquals(1, Iterators.count(q.getAtomSet().iterator()));
		Atom atom = q.getAtomSet().iterator().next();
		Assert.assertEquals(p, atom.getPredicate());
		Assert.assertEquals(a, atom.getTerm(0));
		Assert.assertEquals(a, atom.getTerm(1));
	}

	@Test
	public void testBottomQuery1() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(X,Y) :- p(X,Y), a=b.");
		Pair<ConjunctiveQuery, Substitution> pair = EqualityUtils.processEquality(q);

		q = pair.getLeft();
		Substitution s = pair.getRight();

		// check substitution minimality
		Assert.assertTrue(s.getTerms().isEmpty());

		// check query ans part
		Assert.assertEquals(0, q.getAnswerVariables().size());

		// check query atomset
		Assert.assertEquals(1, Iterators.count(q.getAtomSet().iterator()));
		Atom atom = q.getAtomSet().iterator().next();
		Assert.assertEquals(Predicate.BOTTOM, atom.getPredicate());
	}

	@Test
	public void testBottomQuery2() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(X,Y) :- p(X,Y), X=a, X=Y, Y=b.");
		Pair<ConjunctiveQuery, Substitution> pair = EqualityUtils.processEquality(q);

		q = pair.getLeft();
		Substitution s = pair.getRight();

		// check substitution minimality
		Assert.assertTrue(s.getTerms().isEmpty());

		// check query ans part
		Assert.assertEquals(0, q.getAnswerVariables().size());

		// check query atomset
		Assert.assertEquals(1, Iterators.count(q.getAtomSet().iterator()));
		Atom atom = q.getAtomSet().iterator().next();
		Assert.assertEquals(Predicate.BOTTOM, atom.getPredicate());
	}

	@Test
	public void testQuery1() throws HomomorphismException, IteratorException, AtomSetException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(X,Y) :- b(X), Y=a.");
		InMemoryAtomSet store = new DefaultInMemoryGraphStore();
		store.addAll(DlgpParser.parseAtomSet("b(a),b(b)."));

		BacktrackHomomorphism h = new BacktrackHomomorphism();
		CloseableIterator<Substitution> results = h.execute(q, store);
		while(results.hasNext()) {
			results.next();
		}
		results.close();

	}
	
	@Test
	public void testQuery2() throws HomomorphismException, IteratorException, AtomSetException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(X,Y) :- p(X,Y), X=Y.");
		InMemoryAtomSet store = new DefaultInMemoryGraphStore();
		store.addAll(DlgpParser.parseAtomSet("p(a,a),p(a,b),p(b,b)."));

		BacktrackHomomorphism h = new BacktrackHomomorphism();
		CloseableIterator<Substitution> results = h.execute(q, store);
		while(results.hasNext()) {
			results.next();
		}
		results.close();

	}

}
