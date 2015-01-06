import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.rulesetanalyser.RuleAnalyser;
import fr.lirmm.graphik.graal.rulesetanalyser.property.AtomicBodyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DisconnectedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.DomainRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierGuardedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FrontierOneProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.GuardedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RangeRestrictedProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.StickyProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyAcyclicProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyFrontierGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyGuardedSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.WeaklyStickyProperty;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SccTest {
	
	private static RuleProperty ab = AtomicBodyProperty.getInstance();
	private static RuleProperty disc = DisconnectedProperty.getInstance();
	private static RuleProperty dr = DomainRestrictedProperty.getInstance();
	private static RuleProperty fg = FrontierGuardedProperty.getInstance();
	private static RuleProperty fr1 = FrontierOneProperty.getInstance();
	private static RuleProperty g = GuardedProperty.getInstance();
	private static RuleProperty rr = RangeRestrictedProperty.getInstance();
 	private static RuleProperty s = StickyProperty.getInstance();
 	private static RuleProperty wa = WeaklyAcyclicProperty.getInstance();
	private static RuleProperty ws = WeaklyStickyProperty.getInstance();
	private static RuleProperty wg = WeaklyGuardedSetProperty.getInstance();
	private static RuleProperty wfg = WeaklyFrontierGuardedSetProperty.getInstance();

	private static Rule nr0,nr1,r0,r1,r2,r3,r4,r5;
 	private static List<Rule> rSet0,rSet1,rSet2,rSet3;
	
	@BeforeClass
	public static void setUp() {
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
		assertFalse(ab.check(r0));
		assertFalse(ab.check(r1));
		assertTrue(ab.check(r2));
		assertFalse(ab.check(r3));
		assertFalse(ab.check(r4));
		assertFalse(ab.check(r5));
	}

	@Test
	public void disconnectedTest() {
		assertFalse(disc.check(r0));
		assertTrue(disc.check(r1));
		assertFalse(disc.check(r2));
		assertTrue(disc.check(r3));
		assertFalse(disc.check(r4));
		assertFalse(disc.check(r5));
	}

	@Test
	public void domainRestrictedTest() {
		assertTrue(dr.check(r0));
		assertTrue(dr.check(r1));
		assertTrue(dr.check(r2));
		assertTrue(dr.check(r3));
		assertFalse(dr.check(r4));
		assertTrue(dr.check(r5));
		assertTrue(dr.check(nr0));
		assertFalse(dr.check(nr1));
	}

	@Test
	public void frontierGuardedTest() {
		assertTrue(fg.check(r0));
		assertTrue(fg.check(r1));
		assertTrue(fg.check(r2));
		assertTrue(fg.check(r3));
		assertTrue(fg.check(r4));
		assertFalse(fg.check(r5));
	}
	
	@Test
	public void frontierOneTest() {
		assertFalse(fr1.check(r0));
		assertFalse(fr1.check(r1));
		assertTrue(fr1.check(r2));
		assertFalse(fr1.check(r3));
		assertTrue(fr1.check(r4));
		assertFalse(fr1.check(r5));
	}

	@Test
	public void guardedTest() {
		assertTrue(g.check(r0));
		assertFalse(g.check(r1));
		assertTrue(g.check(r2));
		assertTrue(g.check(r3));
		assertFalse(g.check(r4));
		assertFalse(g.check(r5));
	}

	@Test
	public void rangeRestrictedTest() {
		assertTrue(rr.check(r0));
		assertFalse(rr.check(r1));
		assertTrue(rr.check(r2));
		assertFalse(rr.check(r3));
		assertFalse(rr.check(r4));
		assertTrue(rr.check(r5));
	}

	@Test
	public void stickyTest() {
		assertTrue("R0",s.check(r0));
		assertTrue("R1",s.check(r1));
		assertTrue("R2",s.check(r2));
		assertFalse("R3",s.check(r3));
		assertFalse("R4",s.check(r4));
		assertTrue("R5",s.check(r5));
	}

	@Test
	public void weaklyAcyclicTest() {
		assertTrue(wa.check(r0));
		assertTrue(wa.check(r1));
		assertTrue(wa.check(r2));
		assertTrue(wa.check(r3));
		assertTrue(wa.check(r4));
		assertTrue(wa.check(r5));
	}

	@Test
	public void weaklyStickyTest() {
		assertTrue(ws.check(r0));
		assertTrue(ws.check(r1));
		assertTrue(ws.check(r2));
		assertTrue(ws.check(r3));
		assertTrue(ws.check(r4));
		assertTrue(ws.check(r5));
	}

	@Test
	public void weaklyGuardedTest() {
		assertTrue(wg.check(r0));
		assertTrue(wg.check(r1));
		assertTrue(wg.check(r2));
		assertTrue(wg.check(r3));
		assertTrue(wg.check(r4));
		assertTrue(wg.check(r5));
	}
	
	@Test
	public void weaklyFrontierGuardedTest() {
		assertTrue(wfg.check(r0));
		assertTrue(wfg.check(r1));
		assertTrue(wfg.check(r2));
		assertTrue(wfg.check(r3));
		assertTrue(wfg.check(r4));
		assertTrue(wfg.check(r5));
	}
	
	@Test
	public void setAtomicBodyTest() {
		assertFalse(ab.check(rSet0));
		assertFalse(ab.check(rSet1));
		assertFalse(ab.check(rSet2));
		assertFalse(ab.check(rSet3));
	}

	@Test
	public void setDisconnectedTest() {
		assertFalse(disc.check(rSet0));
		assertFalse(disc.check(rSet1));
		assertFalse(disc.check(rSet2));
		assertFalse(disc.check(rSet3));
	}

	@Test
	public void setDomainRestrictedTest() {
		assertFalse(dr.check(rSet0));
		assertFalse(dr.check(rSet1));
		assertFalse(dr.check(rSet2));
		assertFalse(dr.check(rSet3));
	}

	@Test
	public void setFrontierGuardedTest() {
		assertFalse(fg.check(rSet0));
		assertTrue(fg.check(rSet1));
		assertFalse(fg.check(rSet2));
		assertFalse(fg.check(rSet3));
	}
	
	@Test
	public void setFrontierOneTest() {
		assertFalse(fr1.check(rSet0));
		assertFalse(fr1.check(rSet1));
		assertFalse(fr1.check(rSet2));
		assertFalse(fr1.check(rSet3));
	}

	@Test
	public void setGuardedTest() {
		assertFalse(g.check(rSet0));
		assertFalse(g.check(rSet1));
		assertFalse(g.check(rSet2));
		assertFalse(g.check(rSet3));
	}

	@Test
	public void setRangeRestrictedTest() {
		assertFalse(rr.check(rSet0));
		assertFalse(rr.check(rSet1));
		assertFalse(rr.check(rSet2));
		assertFalse(rr.check(rSet3));
	}

	@Test
	public void setStickyTest() {
		assertFalse(s.check(rSet0));
		assertTrue(s.check(rSet1));
		assertFalse(s.check(rSet2));
		assertFalse(s.check(rSet3));
	}

	@Test
	public void setWeaklyAcyclicTest() {
		assertTrue(wa.check(rSet0));
		assertFalse(wa.check(rSet1));
		assertTrue(wa.check(rSet2));
		assertFalse(wa.check(rSet3));
	}

	@Test
	public void setWeaklyStickyTest() {
		assertTrue(ws.check(rSet0));
		assertTrue(ws.check(rSet1));
		assertTrue(ws.check(rSet2));
		assertTrue(ws.check(rSet3));
	}
	
	@Test
	public void setWeaklyGuardedTest() {
		assertTrue(wg.check(rSet0));
		assertTrue(wg.check(rSet1));
		assertTrue(wg.check(rSet2));
		assertFalse(wg.check(rSet3));
	}
	
	@Test
	public void setWeaklyFrontierGuardedTest() {
		assertTrue(wfg.check(rSet0));
		assertTrue(wfg.check(rSet1));
		assertTrue(wfg.check(rSet2));
		assertFalse(wfg.check(rSet3));
	}
	
	@Test
	public void ruleAnalyserTest() {
		RuleAnalyser analyser;
		analyser = new RuleAnalyser(rSet0);
		analyser.checkAll();
		assertFalse(analyser.check(s));
		assertTrue(analyser.check(wa));
		assertTrue(analyser.check(ws));
		assertTrue(analyser.check(wg));
		assertTrue(analyser.check(wfg));
		
		analyser = new RuleAnalyser(rSet1);
		analyser.checkAll();
		assertTrue(analyser.check(s));
		assertFalse(analyser.check(wa));
		assertTrue(analyser.check(ws));
		assertTrue(analyser.check(wg));
		assertTrue(analyser.check(wfg));
		
		analyser = new RuleAnalyser(rSet3);
		analyser.checkAll();
		assertFalse(analyser.check(s));
		assertFalse(analyser.check(wa));
		assertTrue(analyser.check(ws));
		assertFalse(analyser.check(wg));
		assertFalse(analyser.check(wfg));
	}

};

