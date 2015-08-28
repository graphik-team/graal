/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RuleSetTest {
	
	private static Predicate predicate = new Predicate("pred", 2);

	private static Rule rule1, rule2;
	static {
		Atom atom1, atom2;

		Term[] terms = new Term[2];
		terms[0] = DefaultTermFactory.instance().createVariable("X");
		terms[1] = DefaultTermFactory.instance().createConstant("a");
		atom1 = new DefaultAtom(predicate, Arrays.asList(terms));
		terms = new Term[2];
		terms[0] = DefaultTermFactory.instance().createVariable("X");
		terms[1] = DefaultTermFactory.instance().createVariable("Y");
		atom2 = new DefaultAtom(predicate, Arrays.asList(terms));

		
		rule1 = RuleFactory.instance().create();
		rule1.getBody().add(atom1);
		rule1.getHead().add(atom2);
		
		rule2 = RuleFactory.instance().create();
		rule2.getBody().add(atom2);
		rule2.getHead().add(atom1);
	}


	@Test
	public void test() {
		RuleSet rs = new LinkedListRuleSet();
		rs.add(rule1);
		rs.add(rule2);
		Iterator<Rule> it = rs.iterator();
		Assert.assertTrue(it.hasNext());
		it.next();
		Assert.assertTrue(it.hasNext());
	}
}
