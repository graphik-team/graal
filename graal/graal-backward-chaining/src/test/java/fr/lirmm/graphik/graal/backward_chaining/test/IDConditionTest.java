/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.IDCondition;
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

}
