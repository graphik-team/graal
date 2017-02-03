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
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class MiscTest {
	
	private static ThreadMXBean timer = ManagementFactory.getThreadMXBean();

	private static final Rule RULE_LIN1 = DefaultRuleFactory.instance().create();
	private static final Rule RULE_LIN2 = DefaultRuleFactory.instance().create();
	private static final Rule RULE_LIN3 = DefaultRuleFactory.instance().create();
	private static final Rule RULE_LIN4 = DefaultRuleFactory.instance().create();
	private static final Rule RULE_LIN5 = DefaultRuleFactory.instance().create();
	private static final Rule RULE_LIN6 = DefaultRuleFactory.instance().create();
	private static final Rule RULE_LIN7 = DefaultRuleFactory.instance().create();
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
