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
	
	private static AtomFilter instance;

	protected AtomFilter() {
		super();
	}

	public static synchronized AtomFilter getInstance() {
		if (instance == null)
			instance = new AtomFilter();

		return instance;
	}

	@Override
	public boolean filter(Object o) {
		return o instanceof Atom;
	}
};
