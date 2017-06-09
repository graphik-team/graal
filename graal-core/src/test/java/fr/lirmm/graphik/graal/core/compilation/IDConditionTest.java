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
/**
* 
*/
package fr.lirmm.graphik.graal.core.compilation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.Partition;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class IDConditionTest {

	private static final boolean DEBUG = false;

	private static final Term X = DefaultTermFactory.instance().createVariable("X");
	private static final Term Y = DefaultTermFactory.instance().createVariable("Y");
	private static final Term Z = DefaultTermFactory.instance().createVariable("Z");

	private static final Term U = DefaultTermFactory.instance().createVariable("U");
	private static final Term V = DefaultTermFactory.instance().createVariable("V");
	private static final Term W = DefaultTermFactory.instance().createVariable("W");

	private static final Term A = DefaultTermFactory.instance().createConstant("a");
	private static final Term B = DefaultTermFactory.instance().createConstant("b");
	//private static final Term C = DefaultTermFactory.instance().createConstant("c");
	//private static final Term D = DefaultTermFactory.instance().createConstant("d");

	private static IDCondition createCondition(Term[] body, Term[] head) {
		return new IDConditionImpl(Arrays.asList(body), Arrays.asList(head));
	}

	/**
	 * h(X) :- b(X,Y,Y).
	 *
	 * generateBody([X]) => [X, Y, Y].
	 */
	@Test
	public void generateBodyTest1() {
		Term[] body = { X, Y, Y };
		Term[] head = { X };

		IDCondition cond = createCondition(body, head);

		List<Term> headList = Arrays.asList(head);
		List<Term> newBody = cond.generateBody(headList).getLeft();

		Assert.assertTrue(newBody.get(0).equals(X));
		Assert.assertTrue(newBody.get(1).equals(newBody.get(2)));
	}

	/**
	 * h(X) :- b(X,X).
	 *
	 * generateBody([X]) => [X, X].
	 */
	@Test
	public void generateBodyTest2() {
		Term[] body = { X, X };
		Term[] head = { X };

		IDCondition cond = createCondition(body, head);

		List<Term> headList = Arrays.asList(head);
		List<Term> newBody = cond.generateBody(headList).getLeft();
		Assert.assertTrue(newBody.get(0).equals(newBody.get(1)));
	}

	/**
	 * h(X,Y) :- b(X,Y).
	 *
	 * generateBody([X,X]) => [X, X].
	 */
	@Test
	public void generateBodyTest3() {
		Term[] set = { X, Y };
		Term[] head = { X, X };

		IDCondition cond = createCondition(set, set);

		List<Term> headList = Arrays.asList(head);
		List<Term> newBody = cond.generateBody(headList).getLeft();

		Assert.assertTrue(newBody.get(0).equals(X));
		Assert.assertTrue(newBody.get(1).equals(X));
	}

	/**
	 * h(X,Y,X,Y) :- b(X,Y).
	 *
	 * generateBody([X,Y,a,b]) => [a, b].
	 */
	@Test
	public void generateBodyTest4() {

		Term[] body1 = { X, Y };
		Term[] head1 = { X, Y, X, Y };
		IDCondition cond = createCondition(body1, head1);

		Term[] head = { X, Y, A, B };

		Term[] expected = { A, B };

		Pair<List<Term>, Substitution> ret = cond.generateBody(Arrays.asList(head));
		List<Term> computed = ret.getLeft();
		Substitution s = ret.getRight();

		Assert.assertEquals(Arrays.asList(expected), computed);
		Assert.assertEquals(A, s.createImageOf(X));
		Assert.assertEquals(B, s.createImageOf(Y));
		Assert.assertEquals(2, s.getTerms().size());
	}

	/**
	 * h(X,Y,X,Y) :- b(X,Y).
	 *
	 * generateBody([a,b,X,Y]) => [a, b].
	 */
	@Test
	public void generateBodyTest5() {

		Term[] body1 = { X, Y };
		Term[] head1 = { X, Y, X, Y };
		IDCondition cond = createCondition(body1, head1);

		Term[] head = { A, B, X, Y };

		Term[] expected = { A, B };

		Pair<List<Term>, Substitution> ret = cond.generateBody(Arrays.asList(head));
		List<Term> computed = ret.getLeft();
		Substitution s = ret.getRight();

		Assert.assertEquals(Arrays.asList(expected), computed);
		Assert.assertEquals(A, s.createImageOf(X));
		Assert.assertEquals(B, s.createImageOf(Y));
		Assert.assertEquals(2, s.getTerms().size());
	}

	/**
	 * h(X,Y) :- b(X,Y).
	 *
	 * generateBody([X,X]) => [X, X].
	 */
	@Test
	public void generateBodyTest6() {
		Term[] set = { X, Y };
		Term[] query = { X, X };

		IDCondition cond = createCondition(set, set);

		Pair<List<Term>, Substitution> ret = cond.generateBody(Arrays.asList(query));
		List<Term> computed = ret.getLeft();

		Assert.assertTrue(computed.get(0).equals(X));
		Assert.assertTrue(computed.get(1).equals(X));
	}

	/**
	 * h(X,Y,X) :- b(X,Y).
	 *
	 * generateBody([X,Y,Y]) => [X, X] || [Y, Y].
	 */
	@Test
	public void generateBodyTest7() {
		Term[] head = { X, Y, X };
		Term[] body = { X, Y };
		Term[] query = { X, Y, Y };

		IDCondition cond = createCondition(body, head);

		Pair<List<Term>, Substitution> ret = cond.generateBody(Arrays.asList(query));
		if (DEBUG) {
			System.out.println(ret);
		}
		List<Term> computed = ret.getLeft();
		Substitution s = ret.getRight();

		Assert.assertTrue(computed.get(0).equals(X) || computed.get(0).equals(Y));
		Assert.assertEquals(computed.get(0), computed.get(1));
		Assert.assertEquals(s.createImageOf(Y), s.createImageOf(X));
		Assert.assertEquals(1, s.getTerms().size()); // {X -> Y} || {Y -> X}
	}

	/**
	 * h(X,Y,X,Y,X) :- b(X,Y).
	 * 
	 * generateBody([X,Y,a,b,Y]) -> null
	 */
	@Test
	public void generateBodyFail1() {
		Term[] body = { X, Y };
		Term[] head = { X, Y, X, Y, X };
		IDCondition cond = createCondition(body, head);

		Term[] query = { X, Y, A, B, Y };

		Assert.assertNull(cond.generateBody(Arrays.asList(query)));
	}

	/**
	 * h(X,X) :- b(X).
	 * 
	 * generateBody([a,b]) -> null
	 */
	@Test
	public void generateBodyFail2() {
		Term[] body = { X };
		Term[] head = { X, X };
		IDCondition cond = createCondition(body, head);

		Term[] query = { A, B };

		Assert.assertNull(cond.generateBody(Arrays.asList(query)));
	}

	@Test
	public void getUnificationTest() {

		Term[] body = { X, Y, X };
		Term[] newBody = { U, V, W };

		Term[] head = { X };
		Term[] newHead = { Z };

		IDCondition cond = createCondition(body, head);

		List<Term> newHeadList = Arrays.asList(newHead);
		List<Term> newBodyList = Arrays.asList(newBody);

		Partition<Term> partition = cond.generateUnification(newBodyList, newHeadList);
		boolean isFound = false;
		for (Collection<Term> cl : partition) {
			if (cl.contains(U) && cl.contains(W) && cl.contains(Z)) {
				isFound = !cl.contains(V);
			}
		}

		Assert.assertTrue("Good partition not found", isFound);
	}

	@Test
	public void getUnificationFailTest() {

		Term[] body = { X, Y };
		Term[] head = { X, Y, X, Y, Y };

		IDCondition cond = createCondition(body, head);

		Term[] newBody = { A, B };
		Term[] newHead = { A, B, X, Y, X };

		List<Term> newHeadList = Arrays.asList(newHead);
		List<Term> newBodyList = Arrays.asList(newBody);

		Partition<Term> partition = cond.generateUnification(newBodyList, newHeadList);

		Assert.assertNull(partition);
	}

	/**
	 * Given [(x,y,x) -> (y,x) composeWith (x,y) -> (y)] then return [(x,y,x) ->
	 * (y)]
	 */
	@Test
	public void composeWithTest1() {
		Term[] body1 = { X, Y, X };
		Term[] head1 = { Y, X };
		IDCondition cond = createCondition(body1, head1);

		Term[] body2 = { X, Y };
		Term[] head2 = { X };
		IDCondition cond2 = createCondition(body2, head2);

		Term[] bodyRes = { X, Y, X };
		Term[] headRes = { Y };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);
	}

	/**
	 * Given [(x,y,x) -> (y,x) composeWith (x,x) -> (x)] then return [(x,x,x) ->
	 * (x)]
	 */
	@Test
	public void composeWithTest2() {
		Term[] body1 = { X, Y, X };
		Term[] head1 = { Y, X };
		IDCondition cond = createCondition(body1, head1);

		Term[] body2 = { X, X };
		Term[] head2 = { X };
		IDCondition cond2 = createCondition(body2, head2);

		Term[] bodyRes = { X, X, X };
		Term[] headRes = { X };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);

	}

	/**
	 * Given [(x,y) -> (y,x,x) composeWith (x,x,y) -> (x,y)] then return [(x,x)
	 * -> (x,x)]
	 */
	@Test
	public void composeWithTest3() {
		Term[] body1 = { X, Y };
		Term[] head1 = { Y, X, X };
		IDCondition cond = createCondition(body1, head1);

		Term[] body2 = { X, X, Y };
		Term[] head2 = { X, Y };
		IDCondition cond2 = createCondition(body2, head2);

		Term[] bodyRes = { X, X };
		Term[] headRes = { X, X };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);

	}

	/**
	 * Given [(x,y) -> (y,x) composeWith (x,y) -> (x,y)] then return [(x,y) ->
	 * (y,x)]
	 */
	@Test
	public void composeWithTest4() {
		Term[] body1 = { X, Y };
		Term[] head1 = { Y, X };
		IDCondition cond = createCondition(body1, head1);

		Term[] body2 = { X, Y };
		Term[] head2 = { X, Y };
		IDCondition cond2 = createCondition(body2, head2);

		Term[] bodyRes = { X, Y };
		Term[] headRes = { Y, X };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);

	}

	/**
	 * Given [(x,y) -> (y,x) composeWith (x,y) -> (y,x)] then return [(x,y) ->
	 * (x,y)]
	 */
	@Test
	public void composeWithTest5() {
		Term[] body1 = { X, Y };
		Term[] head1 = { Y, X };
		IDCondition cond = createCondition(body1, head1);

		Term[] bodyRes = { X, Y };
		Term[] headRes = { X, Y };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond);
		Assert.assertEquals(expected, computed);

	}

	/**
	 * Given [(x,y) -> (y) composeWith (x) -> (x)] then return [(x,y) -> (y)]
	 */
	@Test
	public void composeWithTest6() {
		Term[] body1 = { X, Y };
		Term[] head1 = { Y };
		IDCondition cond = createCondition(body1, head1);

		Term[] body2 = { X };
		Term[] head2 = { X };
		IDCondition cond2 = createCondition(body2, head2);

		Term[] bodyRes = { X, Y };
		Term[] headRes = { Y };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);

	}

	/**
	 * Given an IDCondition[(x,y) -> (x,y,x)] then homomorphism([u,v,w], [a,b])
	 * return (u->a, v->b, w->a)
	 */
	@Test
	public void homomorphismIssue35Test() {
		// Assume.assumeTrue(false); // FIXME #35
		Term[] body = { X, Y };
		Term[] head = { X, Y, X };
		IDCondition cond = createCondition(body, head);

		Term[] query = { U, V, W };
		Term[] data = { A, B };

		Substitution h = cond.homomorphism(Arrays.asList(query), Arrays.asList(data));
		Assert.assertEquals(A, h.createImageOf(U));
		Assert.assertEquals(B, h.createImageOf(V));
		Assert.assertEquals(A, h.createImageOf(W));
	}

	/**
	 * Given an IDCondition[(x,y) -> (x,y,x)] then homomorphism([u,v,u], [a,a])
	 * return (u->a, v->a)
	 */
	@Test
	public void homomorphismTest1() {
		Term[] body = { X, Y };
		Term[] head = { X, Y, X };
		IDCondition cond = createCondition(body, head);

		Term[] query = { U, V, U };
		Term[] data = { A, A };

		Substitution h = cond.homomorphism(Arrays.asList(query), Arrays.asList(data));
		Assert.assertEquals(A, h.createImageOf(U));
		Assert.assertEquals(A, h.createImageOf(V));
	}

	/**
	 * Given an IDCondition[(x,y) -> (x,y)] then homomorphism([x,x], [a,b])
	 * return null
	 */
	@Test
	public void homomorphismFailTest1() {
		Term[] body = { X, Y };
		Term[] head = { X, Y };
		IDCondition cond = createCondition(body, head);

		Term[] query = { U, U };
		Term[] data = { A, B };

		Substitution h = cond.homomorphism(Arrays.asList(query), Arrays.asList(data));
		Assert.assertNull(h);
	}

	/**
	 * Given an IDCondition [(x,y) -> (x,y,x)] then homomorphism([x,y,y], [a,b])
	 * return null
	 */
	@Test
	public void homomorphismFailTest2() {
		Term[] body = { X, Y };
		Term[] head = { X, Y, X };
		IDCondition cond = createCondition(body, head);

		Term[] query = { U, V, V };
		Term[] data = { A, B };

		Substitution h = cond.homomorphism(Arrays.asList(query), Arrays.asList(data));
		Assert.assertNull(h);
	}

}
