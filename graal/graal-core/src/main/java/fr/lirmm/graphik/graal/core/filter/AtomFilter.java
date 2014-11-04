/**
 * 
 */
package fr.lirmm.graphik.graal.core.filter;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.util.stream.Filter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class AtomFilter implements Filter {
		
		@Override
		public boolean filter(Object o) {
			return o instanceof Atom;
		}
};
