/**
 * 
 */
package fr.lirmm.graphik.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class Profiler {

	private final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
	private final Map<String, Long> tmpMap = new TreeMap<String, Long>();
	private final Map<String, Long> map = new TreeMap<String, Long>();

	public Profiler() {
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public void start(String key) {
		this.tmpMap.put(key, this.getTime());
	}

	public void stop(String key) {
		Long old = this.map.get(key);
		old = (old == null) ? 0 : old;
		this.map.put(key, old + this.getTime() - this.tmpMap.get(key));
	}

	public long get(String key) {
		return this.map.get(key);
	}

	public void clear(String key) {
		this.map.remove(key);
	}
	
	public Set<String> keySet() {
		return this.map.keySet();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE
	// /////////////////////////////////////////////////////////////////////////

	private long getTime() {
		return bean.getCurrentThreadCpuTime() / 1000000;
	}

}
