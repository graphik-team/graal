/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.IDCondition;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.TermPartition;
import fr.lirmm.graphik.graal.core.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class IDConditionTest {

	/**
	 * c(X) :- b(X,Y,Y).
	 *
	 * getBody([X]) => b(X, Y, Y).
	 */
	@Test
	public void getBody1() {
		Term x = new Term("X", Term.Type.VARIABLE);
		Term y = new Term("Y", Term.Type.VARIABLE);

		Term[] body = { x, y, y };
		Term[] head = { x };
		List<Term> headList = Arrays.asList(head);

		IDCondition cond = new IDCondition(Arrays.asList(body),
				Arrays.asList(head));

		List<Term> newBody = cond.getBody(headList);
		Assert.assertTrue(newBody.get(1).equals(newBody.get(2)));
	}

	/**
	 * c(X) :- b(X,X).
	 *
	 * getBody([X]) => b(X, X).
	 */
	@Test
	public void getBody2() {
		Term x = new Term("X", Term.Type.VARIABLE);

		Term[] body = { x, x };
		Term[] head = { x };
		List<Term> headList = Arrays.asList(head);

		IDCondition cond = new IDCondition(Arrays.asList(body),
				Arrays.asList(head));

		List<Term> newBody = cond.getBody(headList);
		Assert.assertTrue(newBody.get(0).equals(newBody.get(1)));
	}

	/**
	 * c(X) :- b(X,X).
	 *
	 * getBody([X]) => b(X, X).
	 */
	@Test
	public void getUnification() {
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

		IDCondition cond = new IDCondition(Arrays.asList(body),
				Arrays.asList(head));

		List<Term> newHeadList = Arrays.asList(newHead);
		List<Term> newBodyList = Arrays.asList(newBody);

		TermPartition partition = cond.getUnification(newBodyList,
				newHeadList);
		System.out.println(partition);
		boolean isFound = false;
		for (ArrayList<Term> cl : partition) {
			if (cl.contains(a) && cl.contains(b) && cl.contains(d)) {
				isFound = true;
			}
		}

		Assert.assertTrue("Good partition not found", isFound);
	}

}
