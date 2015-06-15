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
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.backward_chaining.pure.Utils;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class MiscTest {
	
	private static ThreadMXBean timer = ManagementFactory.getThreadMXBean();

	private static final Rule RULE_LIN1 = new DefaultRule();
	private static final Rule RULE_LIN2 = new DefaultRule();
	private static final Rule RULE_LIN3 = new DefaultRule();
	private static final Rule RULE_LIN4 = new DefaultRule();
	private static final Rule RULE_LIN5 = new DefaultRule();
	private static final Rule RULE_LIN6 = new DefaultRule();
	private static final Rule RULE_LIN7 = new DefaultRule();
	// private static final Rule RULE_LIN8 = new DefaultRule();
	// private static final Rule RULE_LIN9 = new DefaultRule();
	// private static final Rule RULE_LIN10 = new DefaultRule();
	// private static final Rule RULE_LIN11 = new DefaultRule();
	// private static final Rule RULE_LIN12 = new DefaultRule();
	// private static final Rule RULE_LIN13 = new DefaultRule();
	// private static final Rule RULE_LIN14 = new DefaultRule();
	// private static final Rule RULE_LIN15 = new DefaultRule();
	// private static final Rule RULE_LIN16 = new DefaultRule();
	// private static final Rule RULE_LIN17 = new DefaultRule();
	// private static final Rule RULE_LIN18 = new DefaultRule();
	// private static final Rule RULE_LIN19 = new DefaultRule();

	static {
		RULE_LIN1.getBody().add(TestUtils.PXX);
		RULE_LIN1.getHead().add(TestUtils.QXX);

		RULE_LIN2.getBody().add(TestUtils.PYX);
		RULE_LIN2.getHead().add(TestUtils.QYX);

		RULE_LIN3.getBody().add(TestUtils.PZX);
		RULE_LIN3.getHead().add(TestUtils.QZX);

		RULE_LIN4.getBody().add(TestUtils.PYX);
		RULE_LIN4.getHead().add(TestUtils.QXY);

		RULE_LIN5.getBody().add(TestUtils.PYX);
		RULE_LIN5.getHead().add(TestUtils.PXY);

		RULE_LIN6.getBody().add(TestUtils.PYX);
		RULE_LIN6.getHead().add(TestUtils.PXX);

		RULE_LIN7.getBody().add(TestUtils.PZX);
		RULE_LIN7.getHead().add(TestUtils.PXZ);
	}

	@Test
	public void equalsLinearRulesTest() {		
		Assert.assertTrue(Utils.imply(RULE_LIN1, RULE_LIN1));
		Assert.assertFalse(Utils.imply(RULE_LIN1, RULE_LIN2));
		Assert.assertFalse(Utils.imply(RULE_LIN1, RULE_LIN3));
		Assert.assertFalse(Utils.imply(RULE_LIN1, RULE_LIN4));
		Assert.assertFalse(Utils.imply(RULE_LIN1, RULE_LIN5));
		Assert.assertFalse(Utils.imply(RULE_LIN1, RULE_LIN6));
		Assert.assertFalse(Utils.imply(RULE_LIN1, RULE_LIN7));
		
		Assert.assertTrue(Utils.imply(RULE_LIN2, RULE_LIN1));
		Assert.assertTrue(Utils.imply(RULE_LIN2, RULE_LIN2));
		Assert.assertTrue(Utils.imply(RULE_LIN2, RULE_LIN3));
		Assert.assertFalse(Utils.imply(RULE_LIN2, RULE_LIN4));
		Assert.assertFalse(Utils.imply(RULE_LIN2, RULE_LIN5));
		Assert.assertFalse(Utils.imply(RULE_LIN2, RULE_LIN6));
		Assert.assertFalse(Utils.imply(RULE_LIN2, RULE_LIN7));
		
		Assert.assertTrue(Utils.imply(RULE_LIN3, RULE_LIN1));
		Assert.assertTrue(Utils.imply(RULE_LIN3, RULE_LIN2));
		Assert.assertTrue(Utils.imply(RULE_LIN3, RULE_LIN3));
		Assert.assertFalse(Utils.imply(RULE_LIN3, RULE_LIN4));
		Assert.assertFalse(Utils.imply(RULE_LIN3, RULE_LIN5));
		Assert.assertFalse(Utils.imply(RULE_LIN3, RULE_LIN6));
		Assert.assertFalse(Utils.imply(RULE_LIN3, RULE_LIN7));
		
		Assert.assertTrue(Utils.imply(RULE_LIN4, RULE_LIN1));
		Assert.assertFalse(Utils.imply(RULE_LIN4, RULE_LIN2));
		Assert.assertFalse(Utils.imply(RULE_LIN4, RULE_LIN3));
		Assert.assertTrue(Utils.imply(RULE_LIN4, RULE_LIN4));
		Assert.assertFalse(Utils.imply(RULE_LIN4, RULE_LIN5));
		Assert.assertFalse(Utils.imply(RULE_LIN4, RULE_LIN6));
		Assert.assertFalse(Utils.imply(RULE_LIN4, RULE_LIN7));
		
		Assert.assertFalse(Utils.imply(RULE_LIN5, RULE_LIN1));
		Assert.assertFalse(Utils.imply(RULE_LIN5, RULE_LIN2));
		Assert.assertFalse(Utils.imply(RULE_LIN5, RULE_LIN3));
		Assert.assertFalse(Utils.imply(RULE_LIN5, RULE_LIN4));
		Assert.assertTrue(Utils.imply(RULE_LIN5, RULE_LIN5));
		Assert.assertFalse(Utils.imply(RULE_LIN5, RULE_LIN6));
		Assert.assertTrue(Utils.imply(RULE_LIN5, RULE_LIN7));
		
		Assert.assertFalse(Utils.imply(RULE_LIN6, RULE_LIN1));
		Assert.assertFalse(Utils.imply(RULE_LIN6, RULE_LIN2));
		Assert.assertFalse(Utils.imply(RULE_LIN6, RULE_LIN3));
		Assert.assertFalse(Utils.imply(RULE_LIN6, RULE_LIN4));
		Assert.assertFalse(Utils.imply(RULE_LIN6, RULE_LIN5));
		Assert.assertTrue(Utils.imply(RULE_LIN6, RULE_LIN6));
		Assert.assertFalse(Utils.imply(RULE_LIN6, RULE_LIN7));
		
		Assert.assertFalse(Utils.imply(RULE_LIN7, RULE_LIN1));
		Assert.assertFalse(Utils.imply(RULE_LIN7, RULE_LIN2));
		Assert.assertFalse(Utils.imply(RULE_LIN7, RULE_LIN3));
		Assert.assertFalse(Utils.imply(RULE_LIN7, RULE_LIN4));
		Assert.assertTrue(Utils.imply(RULE_LIN7, RULE_LIN5));
		Assert.assertFalse(Utils.imply(RULE_LIN7, RULE_LIN6));
		Assert.assertTrue(Utils.imply(RULE_LIN7, RULE_LIN7));
		
	}
	
	//private static final int NB_PROFILER = 99999;
	private static final int NB_TIMER    = 999999;
	/**
	 * 6580 6830 6620 6550 6660
	 * 5370 5540 5430 5520 5590
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String args[]) throws InterruptedException {
		//Thread.sleep(20000);
		long time = timer.getCurrentThreadCpuTime();
		MiscTest instance = new MiscTest();
		for(int i=NB_TIMER; i>0; i--) {
			instance.equalsLinearRulesTest();
		}
		System.out.println((timer.getCurrentThreadCpuTime() - time)/1000000);
		
	}
}
