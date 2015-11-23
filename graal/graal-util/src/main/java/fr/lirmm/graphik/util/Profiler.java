/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
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
 * This class is a profiler with a timer feature (ms)
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

	/**
	 * Start a timer with a specified key/identifier. If you recall this method
	 * with the same key, you will erase the start time for the key.
	 * 
	 * @param key
	 */
	public void start(String key) {
		this.tmpMap.put(key, this.getTime());
	}

	/**
	 * Stop the timer with the specified key. The get method will return the
	 * elapsed time between start and stop calls. You should called the start
	 * method with the same key before.
	 * 
	 * @param key
	 */
	public void stop(String key) {
		Long oldTime = (Long) this.map.get(key);
		if(oldTime == null) {
			oldTime = 0L;
		}
		Long newTime = oldTime + (((this.getTime() - this.tmpMap.get(key)) + 500000) / 1000000);
		this.map.put(key, newTime);
		if (this.out != null) {
			this.out.println("Profiler - " + key + ": " + newTime + "ns");
		}
	}

	/**
	 * Map miscellaneous data on the specified key. You can retrieve the data
	 * using the get method.
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		this.map.put(key, value);
		if (this.out != null) {
			this.out.println("Profiler - " + key + ": " + value.toString());
		}
	}

	/**
	 * Get data/time attached to the specified key.
	 * 
	 * @param key
	 * @return
	 */
	public Object get(String key) {
		return this.map.get(key);
	}

	/**
	 * Clear data attached to the specfied key.
	 * 
	 * @param key
	 */
	public void clear(String key) {
		this.map.remove(key);
	}

	/**
	 * Clear all data.
	 */
	public void clear() {
		this.map.clear();
	}

	/**
	 * 
	 * @return a Set of all keys used.
	 */
	public Set<String> keySet() {
		return this.map.keySet();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE
	// /////////////////////////////////////////////////////////////////////////

	private long getTime() {
		return bean.getCurrentThreadCpuTime();
	}

}
