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
package fr.lirmm.graphik;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.MemoryGraphAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public final class TestUtil {
	
	private TestUtil(){}
	
	
	public static AtomSet[] writeableStore() {

			return new AtomSet[] { new MemoryGraphAtomSet(),
				new LinkedListAtomSet() };

	}
}
