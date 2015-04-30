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
