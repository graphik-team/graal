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
package fr.lirmm.graphik.graal.core.grd;

import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.unifier.checker.AtomErasingChecker;
import fr.lirmm.graphik.graal.core.unifier.checker.ProductivityChecker;
import fr.lirmm.graphik.graal.core.unifier.checker.RestrictedProductivityChecker;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class GRDTest {
	
	
	@Test
	public void simpleTest() {
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(DefaultRuleFactory.instance().create(TestUtils.sX, TestUtils.rX));
		rules.add(DefaultRuleFactory.instance().create(TestUtils.sX, TestUtils.pXY));

		DefaultGraphOfRuleDependencies grd = new DefaultGraphOfRuleDependencies(rules, false);
		Assert.assertFalse(grd.existUnifier(rules.get(0), rules.get(1)));
		Assert.assertFalse(grd.existUnifier(rules.get(1), rules.get(0)));
	}
	
	@Test
	public void simpleTest2() {
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(DefaultRuleFactory.instance().create(TestUtils.sX, TestUtils.pXY));
		rules.add(DefaultRuleFactory.instance().create(TestUtils.pXX, TestUtils.rX));

		DefaultGraphOfRuleDependencies grd = new DefaultGraphOfRuleDependencies(rules, false);
		Assert.assertFalse(grd.existUnifier(rules.get(0), rules.get(1)));
		Assert.assertFalse(grd.existUnifier(rules.get(1), rules.get(0)));
	}

	@Test
	public void AtomErasingFilterTest() {
		Rule r1 = DefaultRuleFactory.instance().create(DefaultAtomSetFactory.instance().create(TestUtils.pXZ), DefaultAtomSetFactory.instance().create(TestUtils.pXY,TestUtils.pYZ));
		Rule r2 = DefaultRuleFactory.instance().create(TestUtils.pUU, TestUtils.sU);

		Substitution s = new HashMapSubstitution();
		s.put(DefaultTermFactory.instance().createVariable("X"), DefaultTermFactory.instance().createVariable("U"));
		s.put(DefaultTermFactory.instance().createVariable("Y"), DefaultTermFactory.instance().createVariable("U"));
		s.put(DefaultTermFactory.instance().createVariable("Z"), DefaultTermFactory.instance().createVariable("U"));
	
		AtomErasingChecker filter = AtomErasingChecker.instance();
		Assert.assertFalse(filter.isValidDependency(r1, r2, s));
	}
	
	@Test
	public void ProductivityFilterTest() {
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(DefaultRuleFactory.instance().create(TestUtils.sX, TestUtils.rX));
		rules.add(DefaultRuleFactory.instance().create(TestUtils.rX, TestUtils.sX));
		rules.add(DefaultRuleFactory.instance().create(TestUtils.rX, TestUtils.pXY));

		DefaultGraphOfRuleDependencies grd = new DefaultGraphOfRuleDependencies(rules, true, ProductivityChecker.instance());
		Assert.assertFalse(grd.existUnifier(rules.get(0), rules.get(1)));
		Assert.assertFalse(grd.existUnifier(rules.get(1), rules.get(0)));
		Assert.assertTrue(grd.existUnifier(rules.get(0), rules.get(2)));
	}
	
	@Test
	public void test() throws ParseException {
		Rule r1 = DlgpParser.parseRule("wf(X0,Y0), o(Y0) :- e(X0).");
		Rule r2 = DlgpParser.parseRule("e(X1), wf(X1,Y1) :- o(Y1).");
		
		Substitution s = new HashMapSubstitution();
		s.put(DefaultTermFactory.instance().createVariable("Y1"), DefaultTermFactory.instance().createVariable("Y0"));
		RestrictedProductivityChecker filter = RestrictedProductivityChecker.instance();
		Assert.assertFalse(filter.isValidDependency(r1, r2, s));
	}
	
	@Test
	public void test2() throws ParseException {
		Rule r1 = DlgpParser.parseRule("wf(X0,Y0), o(Y0) :- e(X0).");
		Rule r2 = DlgpParser.parseRule("wf(Y1,X1) :- o(Y1).");
		
		Substitution s = new HashMapSubstitution();
		s.put(DefaultTermFactory.instance().createVariable("Y1"), DefaultTermFactory.instance().createVariable("Y0"));
		RestrictedProductivityChecker filter = RestrictedProductivityChecker.instance();
		Assert.assertTrue(filter.isValidDependency(r1, r2, s));
	}

}
