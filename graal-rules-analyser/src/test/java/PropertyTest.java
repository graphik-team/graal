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
public class PropertyTest {
	
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

	private static Rule NR0,NR1,R0,R1,R2,R3,R4,R5;
	private static List<Rule> RSet0,RSet1,RSet2,RSet3,RSetWAFalse;
	
	@BeforeClass
	public static void setUp() {
		try {

		R0 = DlgpParser.parseRule("r(X,Y) :- p(X), q(X,Y).");
		R1 = DlgpParser.parseRule("r(a,Z) :- p(X), q(a,Y).");
		R2 = DlgpParser.parseRule("q(X,X) :- p(X).");
		R3 = DlgpParser.parseRule("s(a,Z) :- p(X),q(X,Y).");
		R4 = DlgpParser.parseRule("t(a,Z,X) :- p(X),q(Y,Y),p(a).");
		R5 = DlgpParser.parseRule("s(X,Y) :- p(X),q(Y,a).");

		NR0 = DlgpParser.parseRule("r(X,Y,Z),s(a),s(b) :- p(X,Y),q(Z,Y).");
		NR1 = DlgpParser.parseRule("r(Y,Y,Z),s(a),s(X) :- p(X,Y),q(Z,Y).");

		RSet0 = new LinkedList<Rule>();
		RSet0.add(R0);
		RSet0.add(R1);
		RSet0.add(R2);
		RSet0.add(R3);
		RSet0.add(R4);
		RSet0.add(R5);
		
		RSet1 = new LinkedList<Rule>();
		RSet1.add((Rule)(DlgpParser.parseRule("emp(W,V,X,Y) :- dept(V,W).")));
		RSet1.add((Rule)(DlgpParser.parseRule("pR1(V,W,X,Y,Z) :- emp(V,W,X,Y).")));
		RSet1.add((Rule)(DlgpParser.parseRule("dept(W,Z) :- pR1(V,W,X,Y,Z).")));
		RSet1.add((Rule)(DlgpParser.parseRule("runs(W,Y) :- pR1(V,W,X,Y,Z).")));
		RSet1.add((Rule)(DlgpParser.parseRule("area(Y,X) :- pR1(V,W,X,Y,Z).")));
		RSet1.add((Rule)(DlgpParser.parseRule("eXt(Z,Y,X) :- runs(W,X),area(X,Y).")));
		RSet1.add((Rule)(DlgpParser.parseRule("emp(W,V,X,Y) :- dept(V,W).")));
		RSet1.add((Rule)(DlgpParser.parseRule(" pR1(V2,W2,X2,Y2,Z2) :- emp(V2,W2,X2,Y2).")));
		RSet1.add((Rule)(DlgpParser.parseRule("dept(W3,Z3) :- pR1(V3,W3,X3,Y3,Z3).")));
		RSet1.add((Rule)(DlgpParser.parseRule("runs(W4,Y4) :- pR1(V4,W4,X4,Y4,Z4).")));
		RSet1.add((Rule)(DlgpParser.parseRule("area(Y5,X5) :- pR1(V5,W5,X5,Y5,Z5). ")));
		RSet1.add((Rule)(DlgpParser.parseRule(" eXt(Z6,Y6,X6) :- runs(W6,X6),area(X6,Y6).")));


		RSet2 = new LinkedList<Rule>();
		RSet2.add((Rule)(DlgpParser.parseRule("dept(W,Z) :- emp(V,W,X,Y).")));
		RSet2.add((Rule)(DlgpParser.parseRule("pro(Y,X) :- runs(W,X),dept(W,Y).")));

		RSet3 = new LinkedList<Rule>();
		RSet3.add((Rule)(DlgpParser.parseRule("emp(W,V,X,Y) :- dept(V,W).")));
		RSet3.add((Rule)(DlgpParser.parseRule("pR1(V,W,X,Y,Z) :- emp(V,W,X,Y).")));
		RSet3.add((Rule)(DlgpParser.parseRule("dept(W,Z) :- pR1(V,W,X,Y,Z).")));
		RSet3.add((Rule)(DlgpParser.parseRule("runs(W,Y) :- pR1(V,W,X,Y,Z).")));
		RSet3.add((Rule)(DlgpParser.parseRule("pro(Y,X):- runs(W,X),dept(W,Y).")));

		RSetWAFalse = new LinkedList<Rule>();
		RSetWAFalse.add((Rule)(DlgpParser.parseRule("emp(W,V,X,Y) :- dept(V,W).")));
		RSetWAFalse.add((Rule)(DlgpParser.parseRule("pR1(V,W,X,Y,Z) :- emp(V,W,X,Y).")));
		RSetWAFalse.add((Rule)(DlgpParser.parseRule("dept(W,Z) :- pR1(V,W,X,Y,Z).")));
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
		assertFalse(ab.check(R0));
		assertFalse(ab.check(R1));
		assertTrue(ab.check(R2));
		assertFalse(ab.check(R3));
		assertFalse(ab.check(R4));
		assertFalse(ab.check(R5));
	}

	@Test
	public void disconnectedTest() {
		assertFalse(disc.check(R0));
		assertTrue(disc.check(R1));
		assertFalse(disc.check(R2));
		assertTrue(disc.check(R3));
		assertFalse(disc.check(R4));
		assertFalse(disc.check(R5));
	}

	@Test
	public void domainRestrictedTest() {
		assertTrue(dr.check(R0));
		assertTrue(dr.check(R1));
		assertTrue(dr.check(R2));
		assertTrue(dr.check(R3));
		assertFalse(dr.check(R4));
		assertTrue(dr.check(R5));
		assertTrue(dr.check(NR0));
		assertFalse(dr.check(NR1));
	}

	@Test
	public void frontierGuardedTest() {
		assertTrue(fg.check(R0));
		assertTrue(fg.check(R1));
		assertTrue(fg.check(R2));
		assertTrue(fg.check(R3));
		assertTrue(fg.check(R4));
		assertFalse(fg.check(R5));
	}
	
	@Test
	public void frontierOneTest() {
		assertFalse(fr1.check(R0));
		assertFalse(fr1.check(R1));
		assertTrue(fr1.check(R2));
		assertFalse(fr1.check(R3));
		assertTrue(fr1.check(R4));
		assertFalse(fr1.check(R5));
	}

	@Test
	public void guardedTest() {
		assertTrue(g.check(R0));
		assertFalse(g.check(R1));
		assertTrue(g.check(R2));
		assertTrue(g.check(R3));
		assertFalse(g.check(R4));
		assertFalse(g.check(R5));
	}

	@Test
	public void rangeRestrictedTest() {
		assertTrue(rr.check(R0));
		assertFalse(rr.check(R1));
		assertTrue(rr.check(R2));
		assertFalse(rr.check(R3));
		assertFalse(rr.check(R4));
		assertTrue(rr.check(R5));
	}

	@Test
	public void stickyTest() {
		assertTrue("R0",s.check(R0));
		assertTrue("R1",s.check(R1));
		assertTrue("R2",s.check(R2));
		assertFalse("R3",s.check(R3));
		assertFalse("R4",s.check(R4));
		assertTrue("R5",s.check(R5));
	}

	@Test
	public void weaklyAcyclicTest() {
		assertTrue(wa.check(R0));
		assertTrue(wa.check(R1));
		assertTrue(wa.check(R2));
		assertTrue(wa.check(R3));
		assertTrue(wa.check(R4));
		assertTrue(wa.check(R5));
	}

	@Test
	public void weaklyStickyTest() {
		assertTrue(ws.check(R0));
		assertTrue(ws.check(R1));
		assertTrue(ws.check(R2));
		assertTrue(ws.check(R3));
		assertTrue(ws.check(R4));
		assertTrue(ws.check(R5));
	}

	@Test
	public void weaklyGuardedTest() {
		assertTrue(wg.check(R0));
		assertTrue(wg.check(R1));
		assertTrue(wg.check(R2));
		assertTrue(wg.check(R3));
		assertTrue(wg.check(R4));
		assertTrue(wg.check(R5));
	}
	
	@Test
	public void weaklyFrontierGuardedTest() {
		assertTrue(wfg.check(R0));
		assertTrue(wfg.check(R1));
		assertTrue(wfg.check(R2));
		assertTrue(wfg.check(R3));
		assertTrue(wfg.check(R4));
		assertTrue(wfg.check(R5));
	}
	
	@Test
	public void setAtomicBodyTest() {
		assertFalse(ab.check(RSet0));
		assertFalse(ab.check(RSet1));
		assertFalse(ab.check(RSet2));
		assertFalse(ab.check(RSet3));
		assertTrue(ab.check(RSetWAFalse));
	}

	@Test
	public void setDisconnectedTest() {
		assertFalse(disc.check(RSet0));
		assertFalse(disc.check(RSet1));
		assertFalse(disc.check(RSet2));
		assertFalse(disc.check(RSet3));
	}

	@Test
	public void setDomainRestrictedTest() {
		assertFalse(dr.check(RSet0));
		assertFalse(dr.check(RSet1));
		assertFalse(dr.check(RSet2));
		assertFalse(dr.check(RSet3));
	}

	@Test
	public void setFrontierGuardedTest() {
		assertFalse(fg.check(RSet0));
		assertTrue(fg.check(RSet1));
		assertFalse(fg.check(RSet2));
		assertFalse(fg.check(RSet3));
		assertTrue(fg.check(RSetWAFalse));
	}
	
	@Test
	public void setFrontierOneTest() {
		assertFalse(fr1.check(RSet0));
		assertFalse(fr1.check(RSet1));
		assertFalse(fr1.check(RSet2));
		assertFalse(fr1.check(RSet3));
	}

	@Test
	public void setGuardedTest() {
		assertFalse(g.check(RSet0));
		assertFalse(g.check(RSet1));
		assertFalse(g.check(RSet2));
		assertFalse(g.check(RSet3));
	}

	@Test
	public void setRangeRestrictedTest() {
		assertFalse(rr.check(RSet0));
		assertFalse(rr.check(RSet1));
		assertFalse(rr.check(RSet2));
		assertFalse(rr.check(RSet3));
	}

	@Test
	public void setStickyTest() {
		assertFalse(s.check(RSet0));
		assertTrue(s.check(RSet1));
		assertFalse(s.check(RSet2));
		assertFalse(s.check(RSet3));
		assertTrue(s.check(RSetWAFalse));

	}

	@Test
	public void setWeaklyAcyclicTest() {
		assertTrue(wa.check(RSet0));
		assertFalse(wa.check(RSet1));
		assertTrue(wa.check(RSet2));
		assertFalse(wa.check(RSet3));
		assertFalse(wa.check(RSetWAFalse));
	}

	@Test
	public void setWeaklyStickyTest() {
		assertTrue(ws.check(RSet0));
		assertTrue(ws.check(RSet1));
		assertTrue(ws.check(RSet2));
		assertTrue(ws.check(RSet3));
	}
	
	@Test
	public void setWeaklyGuardedTest() {
		assertTrue(wg.check(RSet0));
		assertTrue(wg.check(RSet1));
		assertTrue(wg.check(RSet2));
		assertFalse(wg.check(RSet3));
	}
	
	@Test
	public void setWeaklyFrontierGuardedTest() {
		assertTrue(wfg.check(RSet0));
		assertTrue(wfg.check(RSet1));
		assertTrue(wfg.check(RSet2));
		assertFalse(wfg.check(RSet3));
	}
	
	@Test
	public void ruleAnalyserTest() {
		RuleAnalyser analyser;
		analyser = new RuleAnalyser(RSet0);
		analyser.checkAll();
		assertFalse(analyser.check(s));
		assertTrue(analyser.check(wa));
		assertTrue(analyser.check(ws));
		assertTrue(analyser.check(wg));
		assertTrue(analyser.check(wfg));
		
		analyser = new RuleAnalyser(RSet1);
		analyser.checkAll();
		assertTrue(analyser.check(s));
		assertFalse(analyser.check(wa));
		assertTrue(analyser.check(ws));
		assertTrue(analyser.check(wg));
		assertTrue(analyser.check(wfg));
		
		analyser = new RuleAnalyser(RSet3);
		analyser.checkAll();
		assertFalse(analyser.check(s));
		assertFalse(analyser.check(wa));
		assertTrue(analyser.check(ws));
		assertFalse(analyser.check(wg));
		assertFalse(analyser.check(wfg));
	}

};

