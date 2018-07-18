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
package fr.lirmm.graphik.graal.rulesetanalyser;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.rulesetanalyser.Analyser;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DisconnectedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DomainRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierGuardedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierOneProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.GuardedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.JointlyFrontierGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.LinearProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.MFAProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.MSAProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RangeRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.StickyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyAcyclicProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyStickyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class PropertyTest {
	
	private static RuleSetProperty.Local ab = LinearProperty.instance();
	private static RuleSetProperty.Local disc = DisconnectedProperty.instance();
	private static RuleSetProperty.Local dr = DomainRestrictedProperty.instance();
	private static RuleSetProperty.Local fg = FrontierGuardedProperty.instance();
	private static RuleSetProperty.Local fr1 = FrontierOneProperty.instance();
	private static RuleSetProperty.Local g = GuardedProperty.instance();
	private static RuleSetProperty.Local rr = RangeRestrictedProperty.instance();
	private static RuleSetProperty s = StickyProperty.instance();
	private static RuleSetProperty wa = WeaklyAcyclicProperty.instance();
	private static RuleSetProperty ws = WeaklyStickyProperty.instance();
	private static RuleSetProperty wg = WeaklyGuardedSetProperty.instance();
	private static RuleSetProperty jfg = JointlyFrontierGuardedSetProperty.instance();
	private static RuleSetProperty msa = MSAProperty.instance();
	private static RuleSetProperty mfa = MFAProperty.instance();

	private static LinkedList<RuleSetProperty> properties = new LinkedList<RuleSetProperty>();

	private static Rule nr0,nr1,r0,r1,r2,r3,r4,r5;
	private static List<Rule> rSet0, rSet1, rSet2, rSet3, rSetMSA, rSetMFA, rSetSWA, rSetNotFes;
	
	@BeforeClass
	public static void setUp() {
		properties.add(ab);
		properties.add(disc);
		properties.add(dr);
		properties.add(fg);
		properties.add(fr1);
		properties.add(g);
		properties.add(rr);
		properties.add(s);
		properties.add(wa);
		properties.add(ws);
		properties.add(wg);
		properties.add(jfg);
		//properties.add(msa);
		//properties.add(mfa);

		try {

		r0 = DlgpParser.parseRule("r(X,Y) :- p(X), q(X,Y).");
		r1 = DlgpParser.parseRule("r(a,Z) :- p(X), q(a,Y).");
		r2 = DlgpParser.parseRule("q(X,X) :- p(X).");
		r3 = DlgpParser.parseRule("s(a,Z) :- p(X),q(X,Y).");
		r4 = DlgpParser.parseRule("t(a,Z,X) :- p(X),q(Y,Y),p(a).");
		r5 = DlgpParser.parseRule("s(X,Y) :- p(X),q(Y,a).");

		nr0 = DlgpParser.parseRule("r(X,Y,Z),s(a),s(b) :- p(X,Y),q(Z,Y).");
		nr1 = DlgpParser.parseRule("r(Y,Y,Z),s(a),s(X) :- p(X,Y),q(Z,Y).");

		rSet0 = new LinkedList<Rule>();
		rSet0.add(r0);
		rSet0.add(r1);
		rSet0.add(r2);
		rSet0.add(r3);
		rSet0.add(r4);
		rSet0.add(r5);
		
		rSet1 = new LinkedList<Rule>();
		rSet1.add((Rule)(DlgpParser.parseRule("emp(W,V,X,Y) :- dept(V,W).")));
		rSet1.add((Rule)(DlgpParser.parseRule("pR1(V,W,X,Y,Z) :- emp(V,W,X,Y).")));
		rSet1.add((Rule)(DlgpParser.parseRule("dept(W,Z) :- pR1(V,W,X,Y,Z).")));
		rSet1.add((Rule)(DlgpParser.parseRule("runs(W,Y) :- pR1(V,W,X,Y,Z).")));
		rSet1.add((Rule)(DlgpParser.parseRule("area(Y,X) :- pR1(V,W,X,Y,Z).")));
		rSet1.add((Rule)(DlgpParser.parseRule("eXt(Z,Y,X) :- runs(W,X),area(X,Y).")));
		rSet1.add((Rule)(DlgpParser.parseRule("emp(W,V,X,Y) :- dept(V,W).")));
		rSet1.add((Rule)(DlgpParser.parseRule(" pR1(V2,W2,X2,Y2,Z2) :- emp(V2,W2,X2,Y2).")));
		rSet1.add((Rule)(DlgpParser.parseRule("dept(W3,Z3) :- pR1(V3,W3,X3,Y3,Z3).")));
		rSet1.add((Rule)(DlgpParser.parseRule("runs(W4,Y4) :- pR1(V4,W4,X4,Y4,Z4).")));
		rSet1.add((Rule)(DlgpParser.parseRule("area(Y5,X5) :- pR1(V5,W5,X5,Y5,Z5). ")));
		rSet1.add((Rule)(DlgpParser.parseRule(" eXt(Z6,Y6,X6) :- runs(W6,X6),area(X6,Y6).")));


		rSet2 = new LinkedList<Rule>();
		rSet2.add((Rule)(DlgpParser.parseRule("dept(W,Z) :- emp(V,W,X,Y).")));
		rSet2.add((Rule)(DlgpParser.parseRule("pro(Y,X) :- runs(W,X),dept(W,Y).")));

		rSet3 = new LinkedList<Rule>();
		rSet3.add((Rule)(DlgpParser.parseRule("emp(W,V,X,Y) :- dept(V,W).")));
		rSet3.add((Rule)(DlgpParser.parseRule("pR1(V,W,X,Y,Z) :- emp(V,W,X,Y).")));
		rSet3.add((Rule)(DlgpParser.parseRule("dept(W,Z) :- pR1(V,W,X,Y,Z).")));
		rSet3.add((Rule)(DlgpParser.parseRule("runs(W,Y) :- pR1(V,W,X,Y,Z).")));
		rSet3.add((Rule)(DlgpParser.parseRule("pro(Y,X):- runs(W,X),dept(W,Y).")));
		
		// Exemple 1 from "Acyclicity Notions for Existential Rules and
		// Their Application to Query Answering in Ontologies" Grau, Horrocks, Krotzsch
		rSetMSA = new LinkedList<Rule>(); // this set is not JA
		rSetMSA.add(DlgpParser.parseRule("r(X1,Y1), b(Y1) :- a(X1)."));
		rSetMSA.add(DlgpParser.parseRule("a(X2) :- r(X2,Z1), b(Z1)."));
		rSetMSA.add(DlgpParser.parseRule("r(X3,Y2), c(Y2) :- b(X3)."));
		rSetMSA.add(DlgpParser.parseRule("d(X4) :- c(X4)."));
		rSetMSA.add(DlgpParser.parseRule("b(X5) :- r(X5,Z2), d(Z2)."));
		
		// Exemple 15 from "Acyclicity Notions for Existential Rules and
		// Their Application to Query Answering in Ontologies" Grau, Horrocks, Krotzsch
		rSetMFA = new LinkedList<Rule>(); // this set is not MSA
		rSetMFA.add(DlgpParser.parseRule("r(X,Y), b(Y) :- a(X)."));
		rSetMFA.add(DlgpParser.parseRule("s(X,Y), t(Y,X) :- b(X)."));
		rSetMFA.add(DlgpParser.parseRule("c(X) :- a(Z), s(Z,X)."));
		rSetMFA.add(DlgpParser.parseRule("a(X) :- c(Z), t(Z,X)."));
		
		// Exemple 20 from "Acyclicity Notions for Existential Rules and
		// Their Application to Query Answering in Ontologies" Grau, Horrocks, Krotzsch
		rSetSWA = new LinkedList<Rule>(); // this set is not JA
		rSetSWA.add(DlgpParser.parseRule("r(X1,Y), r(Y,X1), r(X1,X1) :- a(X1)."));
		rSetSWA.add(DlgpParser.parseRule("b(X2) :- r(X2,X2)."));
		rSetSWA.add(DlgpParser.parseRule("a(X3) :- b(X3)."));
				
		rSetNotFes = new LinkedList<Rule>();
		rSetNotFes.add(DlgpParser.parseRule("p(X,Y), h(Y) :- h(X)."));

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void free() {

	}

	@Test
	public void atomicBodyTest() {
		assertEquals(-1, ab.check(r0));
		assertEquals(-1, ab.check(r1));
		assertEquals(1, ab.check(r2));
		assertEquals(-1, ab.check(r3));
		assertEquals(-1, ab.check(r4));
		assertEquals(-1, ab.check(r5));
	}

	@Test
	public void disconnectedTest() {
		assertEquals(-1, disc.check(r0));
		assertEquals(1, disc.check(r1));
		assertEquals(-1, disc.check(r2));
		assertEquals(1, disc.check(r3));
		assertEquals(-1, disc.check(r4));
		assertEquals(-1, disc.check(r5));
	}

	@Test
	public void domainRestrictedTest() {
		assertEquals(1, dr.check(r0));
		assertEquals(1, dr.check(r1));
		assertEquals(1, dr.check(r2));
		assertEquals(1, dr.check(r3));
		assertEquals(-1, dr.check(r4));
		assertEquals(1, dr.check(r5));
		assertEquals(1, dr.check(nr0));
		assertEquals(-1, dr.check(nr1));
	}

	@Test
	public void frontierGuardedTest() {
		assertEquals(1, fg.check(r0));
		assertEquals(1, fg.check(r1));
		assertEquals(1, fg.check(r2));
		assertEquals(1, fg.check(r3));
		assertEquals(1, fg.check(r4));
		assertEquals(-1, fg.check(r5));
	}
	
	@Test
	public void frontierOneTest() {
		assertEquals(-1, fr1.check(r0));
		assertEquals(-1, fr1.check(r1));
		assertEquals(1, fr1.check(r2));
		assertEquals(-1, fr1.check(r3));
		assertEquals(1, fr1.check(r4));
		assertEquals(-1, fr1.check(r5));
	}

	@Test
	public void guardedTest() {
		assertEquals(1, g.check(r0));
		assertEquals(-1, g.check(r1));
		assertEquals(1, g.check(r2));
		assertEquals(1, g.check(r3));
		assertEquals(-1, g.check(r4));
		assertEquals(-1, g.check(r5));
	}

	@Test
	public void rangeRestrictedTest() {
		assertEquals(1, rr.check(r0));
		assertEquals(-1, rr.check(r1));
		assertEquals(1, rr.check(r2));
		assertEquals(-1, rr.check(r3));
		assertEquals(-1, rr.check(r4));
		assertEquals(1, rr.check(r5));
	}

	@Test
	public void stickyTest() {
		assertEquals(1, s.check(new AnalyserRuleSet(r0)));
		assertEquals(1, s.check(new AnalyserRuleSet(r1)));
		assertEquals(1, s.check(new AnalyserRuleSet(r2)));
		assertEquals(-1, s.check(new AnalyserRuleSet(r3)));
		assertEquals(-1, s.check(new AnalyserRuleSet(r4)));
		assertEquals(1, s.check(new AnalyserRuleSet(r5)));
	}

	@Test
	public void weaklyAcyclicTest() {
		assertEquals(1, wa.check(new AnalyserRuleSet(r0)));
		assertEquals(1, wa.check(new AnalyserRuleSet(r1)));
		assertEquals(1, wa.check(new AnalyserRuleSet(r2)));
		assertEquals(1, wa.check(new AnalyserRuleSet(r3)));
		assertEquals(1, wa.check(new AnalyserRuleSet(r4)));
		assertEquals(1, wa.check(new AnalyserRuleSet(r5)));
	}

	@Test
	public void weaklyStickyTest() {
		assertEquals(1, ws.check(new AnalyserRuleSet(r0)));
		assertEquals(1, ws.check(new AnalyserRuleSet(r1)));
		assertEquals(1, ws.check(new AnalyserRuleSet(r2)));
		assertEquals(1, ws.check(new AnalyserRuleSet(r3)));
		assertEquals(1, ws.check(new AnalyserRuleSet(r4)));
		assertEquals(1, ws.check(new AnalyserRuleSet(r5)));
	}

	@Test
	public void weaklyGuardedTest() {
		assertEquals(1, wg.check(new AnalyserRuleSet(r0)));
		assertEquals(1, wg.check(new AnalyserRuleSet(r1)));
		assertEquals(1, wg.check(new AnalyserRuleSet(r2)));
		assertEquals(1, wg.check(new AnalyserRuleSet(r3)));
		assertEquals(1, wg.check(new AnalyserRuleSet(r4)));
		assertEquals(1, wg.check(new AnalyserRuleSet(r5)));
	}
	
	@Test
	public void weaklyFrontierGuardedTest() {
		assertEquals(1, jfg.check(new AnalyserRuleSet(r0)));
		assertEquals(1, jfg.check(new AnalyserRuleSet(r1)));
		assertEquals(1, jfg.check(new AnalyserRuleSet(r2)));
		assertEquals(1, jfg.check(new AnalyserRuleSet(r3)));
		assertEquals(1, jfg.check(new AnalyserRuleSet(r4)));
		assertEquals(1, jfg.check(new AnalyserRuleSet(r5)));
	}
	
	@Test
	public void setAtomicBodyTest() {
		assertEquals(-1, ab.check(new AnalyserRuleSet(rSet0)));
		assertEquals(-1, ab.check(new AnalyserRuleSet(rSet1)));
		assertEquals(-1, ab.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, ab.check(new AnalyserRuleSet(rSet3)));
	}

	@Test
	public void setDisconnectedTest() {
		assertEquals(-1, disc.check(new AnalyserRuleSet(rSet0)));
		assertEquals(-1, disc.check(new AnalyserRuleSet(rSet1)));
		assertEquals(-1, disc.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, disc.check(new AnalyserRuleSet(rSet3)));
	}

	@Test
	public void setDomainRestrictedTest() {
		assertEquals(-1, dr.check(new AnalyserRuleSet(rSet0)));
		assertEquals(-1, dr.check(new AnalyserRuleSet(rSet1)));
		assertEquals(-1, dr.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, dr.check(new AnalyserRuleSet(rSet3)));
	}

	@Test
	public void setFrontierGuardedTest() {
		assertEquals(-1, fg.check(new AnalyserRuleSet(rSet0)));
		assertEquals(1, fg.check(new AnalyserRuleSet(rSet1)));
		assertEquals(-1, fg.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, fg.check(new AnalyserRuleSet(rSet3)));
	}
	
	@Test
	public void setFrontierOneTest() {
		assertEquals(-1, fr1.check(new AnalyserRuleSet(rSet0)));
		assertEquals(-1, fr1.check(new AnalyserRuleSet(rSet1)));
		assertEquals(-1, fr1.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, fr1.check(new AnalyserRuleSet(rSet3)));
	}

	@Test
	public void setGuardedTest() {
		assertEquals(-1, g.check(new AnalyserRuleSet(rSet0)));
		assertEquals(-1, g.check(new AnalyserRuleSet(rSet1)));
		assertEquals(-1, g.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, g.check(new AnalyserRuleSet(rSet3)));
	}

	@Test
	public void setRangeRestrictedTest() {
		assertEquals(-1, rr.check(new AnalyserRuleSet(rSet0)));
		assertEquals(-1, rr.check(new AnalyserRuleSet(rSet1)));
		assertEquals(-1, rr.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, rr.check(new AnalyserRuleSet(rSet3)));
	}

	@Test
	public void setStickyTest() {
		assertEquals(-1, s.check(new AnalyserRuleSet(rSet0)));
		assertEquals(1, s.check(new AnalyserRuleSet(rSet1)));
		assertEquals(-1, s.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, s.check(new AnalyserRuleSet(rSet3)));
	}

	@Test
	public void setWeaklyAcyclicTest() {
		assertEquals(1, wa.check(new AnalyserRuleSet(rSet0)));
		assertEquals(-1, wa.check(new AnalyserRuleSet(rSet1)));
		assertEquals(1, wa.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, wa.check(new AnalyserRuleSet(rSet3)));
		assertEquals(-1, wa.check(new AnalyserRuleSet(rSetMSA)));
		assertEquals(-1, wa.check(new AnalyserRuleSet(rSetMFA)));
		assertEquals(-1, wa.check(new AnalyserRuleSet(rSetNotFes)));
		assertEquals(-1, wa.check(new AnalyserRuleSet(rSetSWA)));
	}

	@Test
	public void setWeaklyStickyTest() {
		assertEquals(1, ws.check(new AnalyserRuleSet(rSet0)));
		assertEquals(1, ws.check(new AnalyserRuleSet(rSet1)));
		assertEquals(1, ws.check(new AnalyserRuleSet(rSet2)));
		assertEquals(1, ws.check(new AnalyserRuleSet(rSet3)));
	}
	
	@Test
	public void setWeaklyGuardedTest() {
		assertEquals(1, wg.check(new AnalyserRuleSet(rSet0)));
		assertEquals(1, wg.check(new AnalyserRuleSet(rSet1)));
		assertEquals(1, wg.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, wg.check(new AnalyserRuleSet(rSet3)));
	}
	
	@Test
	public void setWeaklyFrontierGuardedTest() {
		assertEquals(1, jfg.check(new AnalyserRuleSet(rSet0)));
		assertEquals(1, jfg.check(new AnalyserRuleSet(rSet1)));
		assertEquals(1, jfg.check(new AnalyserRuleSet(rSet2)));
		assertEquals(-1, jfg.check(new AnalyserRuleSet(rSet3)));
	}
	
	@Test
	public void setMSATest() {
		assertEquals(1, msa.check(new AnalyserRuleSet(rSetMSA)));
		assertEquals(-1, msa.check(new AnalyserRuleSet(rSetMFA)));
		assertEquals(-1, msa.check(new AnalyserRuleSet(rSetNotFes)));
		assertEquals(1, msa.check(new AnalyserRuleSet(rSetSWA)));
	}
	
	@Test
	public void setMFATest() {
		assertEquals(1, mfa.check(new AnalyserRuleSet(rSetMSA)));
		assertEquals(1, mfa.check(new AnalyserRuleSet(rSetMFA)));
		assertEquals(-1, mfa.check(new AnalyserRuleSet(rSetNotFes)));
		assertEquals(1, mfa.check(new AnalyserRuleSet(rSetSWA)));

	}
	
	@Test
	public void ruleAnalyserTest() {
		Analyser analyser;
		analyser = new Analyser();
		analyser.setRuleSet(rSet0);
		analyser.setProperties(properties);
		Map<String, Integer> p = analyser.ruleSetProperties();
		assertEquals(-1, p.get(s.getLabel()).intValue());
		assertEquals(1, p.get(wa.getLabel()).intValue());
		assertEquals(1, p.get(ws.getLabel()).intValue());
		assertEquals(1, p.get(wg.getLabel()).intValue());
		assertEquals(1, p.get(jfg.getLabel()).intValue());
		
		analyser = new Analyser();
		analyser.setRuleSet(rSet1);
		analyser.setProperties(properties);
		p = analyser.ruleSetProperties();
		assertEquals(1, p.get(s.getLabel()).intValue());
		assertEquals(-1, p.get(wa.getLabel()).intValue());
		assertEquals(1, p.get(ws.getLabel()).intValue());
		assertEquals(1, p.get(wg.getLabel()).intValue());
		assertEquals(1, p.get(jfg.getLabel()).intValue());
		
		analyser = new Analyser();
		analyser.setRuleSet(rSet3);
		analyser.setProperties(properties);
		p = analyser.ruleSetProperties();
		assertEquals(-1, p.get(s.getLabel()).intValue());
		assertEquals(-1, p.get(wa.getLabel()).intValue());
		assertEquals(1, p.get(ws.getLabel()).intValue());
		assertEquals(-1, p.get(wg.getLabel()).intValue());
		assertEquals(-1, p.get(jfg.getLabel()).intValue());
	}

};


