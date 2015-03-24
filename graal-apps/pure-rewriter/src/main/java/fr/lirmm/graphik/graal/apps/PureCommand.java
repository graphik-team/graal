/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import fr.lirmm.graphik.util.Profilable;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.Verbosable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class PureCommand implements Verbosable, Profilable {

	private Profiler profiler = null;
	private boolean isVerbose = false;

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}

	@Override
	public void enableVerbose(boolean enable) {
		this.isVerbose = enable;
	}
	
	public boolean isVerbose() {
		return this.isVerbose;
	}
}
