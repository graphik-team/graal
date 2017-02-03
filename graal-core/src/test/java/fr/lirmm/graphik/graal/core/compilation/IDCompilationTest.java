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
package fr.lirmm.graphik.graal.core.compilation;

import java.util.Collection;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class IDCompilationTest {

	private static final Term X = DefaultTermFactory.instance().createVariable("X");
	private static final Term Y = DefaultTermFactory.instance().createVariable("Y");

	private static final Term U = DefaultTermFactory.instance().createVariable("U");
	private static final Term V = DefaultTermFactory.instance().createVariable("V");
	private static final Term W = DefaultTermFactory.instance().createVariable("W");

	private static final Term A = DefaultTermFactory.instance().createConstant("a");
	private static final Term B = DefaultTermFactory.instance().createConstant("b");

	/**
	 * Given p(X,Y) -> q(X,Y,X) <br>
	 * Then rew(q(U,V,W)) <br>
	 * Return q(U,V,W)-{} AND (p(U,V)-{W->U} || p(W,V)-{U->W})
	 */
	@Test
	public void test() {
		Predicate predicateQ = new Predicate("q", 3);
		Predicate predicateP = new Predicate("p", 2);

		Atom body = new DefaultAtom(predicateP, X, Y);
		Atom head = new DefaultAtom(predicateQ, X, Y, X);
		Atom query = new DefaultAtom(predicateQ, U, V, W);

		RuleSet rules = new LinkedListRuleSet();
		rules.add(DefaultRuleFactory.instance().create(body, head));

		RulesCompilation comp = new IDCompilation();
		comp.compile(rules.iterator());

		Collection<Pair<Atom, Substitution>> rewritingOf = comp.getRewritingOf(query);
		boolean rew1 = false;
		boolean rew2 = false;
		for (Pair<Atom, Substitution> p : rewritingOf) {
			Atom a = p.getLeft();
			Substitution s = p.getRight();
			if (a.getPredicate().equals(predicateQ)) {
				rew1 = true;
				Assert.assertEquals(U, a.getTerm(0));
				Assert.assertEquals(V, a.getTerm(1));
				Assert.assertEquals(W, a.getTerm(2));
				Assert.assertEquals(0, s.getTerms().size());
			} else {
				rew2 = true;
				Assert.assertEquals(predicateP, a.getPredicate());
				Assert.assertTrue(a.getTerm(0).equals(U) || a.getTerm(0).equals(W));
				Assert.assertEquals(V, a.getTerm(1));
				Assert.assertEquals(1, s.getTerms().size());
			}
		}
		Assert.assertTrue(rew1 && rew2);
		Assert.assertEquals(2, rewritingOf.size());

	}

	/**
	 * Given p(X,Y) -> q(X,Y,X) <br>
	 * Then rew(q(U,U,U)) <br>
	 * Return q(U,U,U)-{} AND p(U,U)-{})
	 */
	@Test
	public void test2() {
		Predicate predicateQ = new Predicate("q", 3);
		Predicate predicateP = new Predicate("p", 2);

		Atom body = new DefaultAtom(predicateP, X, Y);
		Atom head = new DefaultAtom(predicateQ, X, Y, X);
		Atom query = new DefaultAtom(predicateQ, U, U, U);

		RuleSet rules = new LinkedListRuleSet();
		rules.add(DefaultRuleFactory.instance().create(body, head));

		RulesCompilation comp = new IDCompilation();
		comp.compile(rules.iterator());

		Collection<Pair<Atom, Substitution>> rewritingOf = comp.getRewritingOf(query);
		boolean rew1 = false;
		boolean rew2 = false;
		for (Pair<Atom, Substitution> p : rewritingOf) {
			Atom a = p.getLeft();
			Substitution s = p.getRight();
			if (a.getPredicate().equals(predicateQ)) {
				rew1 = true;
				Assert.assertEquals(U, a.getTerm(0));
				Assert.assertEquals(U, a.getTerm(1));
				Assert.assertEquals(U, a.getTerm(2));
				Assert.assertEquals(0, s.getTerms().size());
			} else {
				rew2 = true;
				Assert.assertEquals(predicateP, a.getPredicate());
				Assert.assertEquals(U, a.getTerm(0));
				Assert.assertEquals(U, a.getTerm(1));
				Assert.assertEquals(0, s.getTerms().size());
			}
		}
		Assert.assertTrue(rew1 && rew2);
		Assert.assertEquals(2, rewritingOf.size());

	}

	/**
	 * Given p(X,Y) -> q(X,Y,X,Y,X) <br>
	 * Then rew(q(U,V,A,B,V)) <br>
	 * Return q(U,V,A,B,V)-{}
	 */
	@Test
	public void test3() {
		Predicate predicateQ = new Predicate("q", 5);
		Predicate predicateP = new Predicate("p", 2);

		Atom body = new DefaultAtom(predicateP, X, Y);
		Atom head = new DefaultAtom(predicateQ, X, Y, X, Y, X);
		Atom query = new DefaultAtom(predicateQ, U, V, A, B, V);

		RuleSet rules = new LinkedListRuleSet();
		rules.add(DefaultRuleFactory.instance().create(body, head));

		RulesCompilation comp = new IDCompilation();
		comp.compile(rules.iterator());

		Collection<Pair<Atom, Substitution>> rewritingOf = comp.getRewritingOf(query);
		Assert.assertEquals(1, rewritingOf.size());

		Pair<Atom, Substitution> p = rewritingOf.iterator().next();
		Atom a = p.getLeft();
		Substitution s = p.getRight();
		Assert.assertEquals(predicateQ, a.getPredicate());
		Assert.assertEquals(U, a.getTerm(0));
		Assert.assertEquals(V, a.getTerm(1));
		Assert.assertEquals(A, a.getTerm(2));
		Assert.assertEquals(B, a.getTerm(3));
		Assert.assertEquals(V, a.getTerm(4));
		Assert.assertEquals(0, s.getTerms().size());

	}

}
