/**
 * 
 */
package fr.lirmm.graphik.util;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Profilable {
	
	void setProfiler(Profiler profiler);

	Profiler getProfiler();
}
