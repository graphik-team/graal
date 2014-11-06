/**
 * 
 */
package fr.lirmm.graphik.util;

import java.io.PrintStream;
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

	private PrintStream out = null;

	private final ThreadMXBean bean = ManagementFactory.getThreadMXBean();
	private final Map<String, Long> tmpMap = new TreeMap<String, Long>();
	private final Map<String, Object> map = new TreeMap<String, Object>();

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public Profiler() {
	}

	public Profiler(PrintStream out) {
		this.out = out;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public void start(String key) {
		this.tmpMap.put(key, this.getTime());
	}

	public void stop(String key) {
		Long oldTime = (Long) this.map.get(key);
		oldTime = (oldTime == null) ? 0 : oldTime;
		Long newTime = oldTime + this.getTime() - this.tmpMap.get(key);
		this.map.put(key, newTime);
		if (this.out != null) {
			this.out.println("Profiler - " + key + ": " + newTime + "ms");
		}
	}

	public void add(String key, Object value) {
		this.map.put(key, value);
		if (this.out != null) {
			this.out.println("Profiler - " + key + ": " + value.toString());
		}
	}

	public Object get(String key) {
		return this.map.get(key);
	}

	public void clear(String key) {
		this.map.remove(key);
	}

	public void clear() {
		this.map.clear();
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
