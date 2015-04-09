/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.IDCondition;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.IDConditionImpl3;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.TermPartition;
import fr.lirmm.graphik.graal.core.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class IDConditionTest {

	private static IDCondition createCondition(Term[] body, Term[] head) {
		return new IDConditionImpl3(Arrays.asList(body),
				Arrays.asList(head));
	}

	/**
	 * c(X) :- b(X,Y,Y).
	 *
	 * getBody([X]) => b(X, Y, Y).
	 */
	@Test
	public void getBodyTest1() {
		Term x = new Term("X", Term.Type.VARIABLE);
		Term y = new Term("Y", Term.Type.VARIABLE);

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
		Term x = new Term("X", Term.Type.VARIABLE);

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
		Term x = new Term("X", Term.Type.VARIABLE);
		Term y = new Term("Y", Term.Type.VARIABLE);

		Term a = new Term("a", Term.Type.VARIABLE);
		Term b = new Term("b", Term.Type.VARIABLE);
		Term c = new Term("c", Term.Type.VARIABLE);
		Term d = new Term("d", Term.Type.VARIABLE);

		Term[] body = { x, y, x };
		Term[] newBody = { b, c, d };

		Term[] head = { x };
		Term[] newHead = { a };

		IDCondition cond = createCondition(body, head);

		List<Term> newHeadList = Arrays.asList(newHead);
		List<Term> newBodyList = Arrays.asList(newBody);

		TermPartition partition = cond.generateUnification(newBodyList,
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
		Term x = new Term("X", Term.Type.VARIABLE);
		Term y = new Term("Y", Term.Type.VARIABLE);

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
		Term x = new Term("X", Term.Type.VARIABLE);
		Term y = new Term("Y", Term.Type.VARIABLE);

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
		Term x = new Term("X", Term.Type.VARIABLE);
		Term y = new Term("Y", Term.Type.VARIABLE);

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
		Term x = new Term("X", Term.Type.VARIABLE);
		Term y = new Term("Y", Term.Type.VARIABLE);

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
		Term x = new Term("X", Term.Type.VARIABLE);
		Term y = new Term("Y", Term.Type.VARIABLE);

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
		Term x = new Term("X", Term.Type.VARIABLE);
		Term y = new Term("Y", Term.Type.VARIABLE);

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
