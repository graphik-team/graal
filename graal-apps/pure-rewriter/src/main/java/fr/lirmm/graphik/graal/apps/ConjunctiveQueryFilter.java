/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.util.stream.Filter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class ConjunctiveQueryFilter implements Filter {
	@Override
	public boolean filter(Object o) {
		return o instanceof ConjunctiveQuery;
	}
}

