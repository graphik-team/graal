/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining;

import fr.lirmm.graphik.util.Profiler;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public abstract class AbstractBackwardChainer implements BackwardChainer {

	private Profiler profiler;

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}

}
