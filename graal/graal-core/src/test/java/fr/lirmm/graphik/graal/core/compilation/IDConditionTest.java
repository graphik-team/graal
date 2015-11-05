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
 /**
 * 
 */
package fr.lirmm.graphik.graal.core.compilation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.Partition;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class IDConditionTest {

	private static IDCondition createCondition(Term[] body, Term[] head) {
		return new IDConditionImpl(Arrays.asList(body),
				Arrays.asList(head));
	}

	/**
	 * c(X) :- b(X,Y,Y).
	 *
	 * getBody([X]) => b(X, Y, Y).
	 */
	@Test
	public void getBodyTest1() {
		Term x = DefaultTermFactory.instance().createVariable("X");
		Term y = DefaultTermFactory.instance().createVariable("Y");

		Term[] body = { x, y, y };
		Term[] head = { x };

		IDCondition cond = createCondition(body, head);

		List<Term> headList = Arrays.asList(head);
		List<Term> newBody = cond.generateBody(headList);

		Assert.assertTrue(newBody.get(1).equals(newBody.get(2)));
	}

	/**
	 * c(X) :- b(X,X).
	 *
	 * getBody([X]) => b(X, X).
	 */
	@Test
	public void getBodyTest2() {
		Term x = DefaultTermFactory.instance().createVariable("X");

		Term[] body = { x, x };
		Term[] head = { x };

		IDCondition cond = createCondition(body, head);

		List<Term> headList = Arrays.asList(head);
		List<Term> newBody = cond.generateBody(headList);
		Assert.assertTrue(newBody.get(0).equals(newBody.get(1)));
	}

	/**
	 * c(X) :- b(X,X).
	 *
	 * getBody([X]) => b(X, X).
	 */
	@Test
	public void getUnificationTest() {
		Term x = DefaultTermFactory.instance().createVariable("X");
		Term y = DefaultTermFactory.instance().createVariable("Y");

		Term a = DefaultTermFactory.instance().createVariable("a");
		Term b = DefaultTermFactory.instance().createVariable("b");
		Term c = DefaultTermFactory.instance().createVariable("c");
		Term d = DefaultTermFactory.instance().createVariable("d");

		Term[] body = { x, y, x };
		Term[] newBody = { b, c, d };

		Term[] head = { x };
		Term[] newHead = { a };

		IDCondition cond = createCondition(body, head);

		List<Term> newHeadList = Arrays.asList(newHead);
		List<Term> newBodyList = Arrays.asList(newBody);

		Partition<Term> partition = cond.generateUnification(newBodyList,
				newHeadList);
		System.out.println(partition);
		boolean isFound = false;
		for (Collection<Term> cl : partition) {
			if (cl.contains(a) && cl.contains(b) && cl.contains(d)) {
				isFound = true;
			}
		}

		Assert.assertTrue("Good partition not found", isFound);
	}

	/**
	 * Given [(x,y,x) -> (y,x) composeWith (x,y) -> (y)]
	 * then return [(x,y,x) -> (y)]
	 */
	@Test
	public void composeWithTest1() {
		Term x = DefaultTermFactory.instance().createVariable("X");
		Term y = DefaultTermFactory.instance().createVariable("Y");

		Term[] body1 = { x, y, x };
		Term[] head1 = { y, x };
		IDCondition cond = createCondition(body1, head1);
		
		Term[] body2 = { x, y };
		Term[] head2 = { x };
		IDCondition cond2 = createCondition(body2, head2);
		
		Term[] bodyRes = { x, y, x };
		Term[] headRes = { y };
		IDCondition expected = createCondition(bodyRes, headRes);
		
		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);
	}

	/**
	 * Given [(x,y,x) -> (y,x) composeWith (x,x) -> (x)] 
	 * then return [(x,x,x) -> (x)]
	 */
	@Test
	public void composeWithTest2() {
		Term x = DefaultTermFactory.instance().createVariable("X");
		Term y = DefaultTermFactory.instance().createVariable("Y");

		Term[] body1 = { x, y, x };
		Term[] head1 = { y, x };
		IDCondition cond = createCondition(body1, head1);

		Term[] body2 = { x, x };
		Term[] head2 = { x };
		IDCondition cond2 = createCondition(body2, head2);

		Term[] bodyRes = { x, x, x };
		Term[] headRes = { x };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);

	}

	/**
	 * Given [(x,y) -> (y,x,x) composeWith (x,x,y) -> (x,y)] 
	 * then return [(x,x) -> (x,x)]
	 */
	@Test
	public void composeWithTest3() {
		Term x = DefaultTermFactory.instance().createVariable("X");
		Term y = DefaultTermFactory.instance().createVariable("Y");

		Term[] body1 = { x, y };
		Term[] head1 = { y, x, x };
		IDCondition cond = createCondition(body1, head1);

		Term[] body2 = { x, x, y };
		Term[] head2 = { x, y };
		IDCondition cond2 = createCondition(body2, head2);

		Term[] bodyRes = { x, x };
		Term[] headRes = { x, x };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);

	}

	/**
	 * Given [(x,y) -> (y,x) composeWith (x,y) -> (x,y)] 
	 * then return [(x,y) -> (y,x)]
	 */
	@Test
	public void composeWithTest4() {
		Term x = DefaultTermFactory.instance().createVariable("X");
		Term y = DefaultTermFactory.instance().createVariable("Y");

		Term[] body1 = { x, y };
		Term[] head1 = { y, x };
		IDCondition cond = createCondition(body1, head1);

		Term[] body2 = { x, y };
		Term[] head2 = { x, y };
		IDCondition cond2 = createCondition(body2, head2);

		Term[] bodyRes = { x, y };
		Term[] headRes = { y, x };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);

	}
	
	/**
	 * Given [(x,y) -> (y,x) composeWith (x,y) -> (y,x)] 
	 * then return [(x,y) -> (x,y)]
	 */
	@Test
	public void composeWithTest5() {
		Term x = DefaultTermFactory.instance().createVariable("X");
		Term y = DefaultTermFactory.instance().createVariable("Y");

		Term[] body1 = { x, y };
		Term[] head1 = { y, x };
		IDCondition cond = createCondition(body1, head1);

		Term[] bodyRes = { x, y };
		Term[] headRes = { x, y };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond);
		Assert.assertEquals(expected, computed);

	}

	/**
	 * Given [(x,y) -> (y) composeWith (x) -> (x)] then return [(x,y) -> (y)]
	 */
	@Test
	public void composeWithTest6() {
		Term x = DefaultTermFactory.instance().createVariable("X");
		Term y = DefaultTermFactory.instance().createVariable("Y");

		Term[] body1 = { x, y };
		Term[] head1 = { y };
		IDCondition cond = createCondition(body1, head1);

		Term[] body2 = { x };
		Term[] head2 = { x };
		IDCondition cond2 = createCondition(body2, head2);

		Term[] bodyRes = { x, y };
		Term[] headRes = { y };
		IDCondition expected = createCondition(bodyRes, headRes);

		IDCondition computed = cond.composeWith(cond2);
		Assert.assertEquals(expected, computed);

	}

}
