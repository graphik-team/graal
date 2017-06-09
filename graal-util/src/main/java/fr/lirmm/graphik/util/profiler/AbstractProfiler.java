/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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
package fr.lirmm.graphik.util.profiler;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.lirmm.graphik.util.TimeUnit;

/**
 * This class is a profiler with a timer feature (ms)
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public abstract class AbstractProfiler implements Profiler {

	private PrintStream out = null;
	private SimpleDateFormat dateFormat = new SimpleDateFormat();
	private TimeUnit timeUnit;

	private final Map<String, Long> startTimeMap = new TreeMap<String, Long>();
	private final Map<String, Object> map = new TreeMap<String, Object>();

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public AbstractProfiler() {
		this.timeUnit = TimeUnit.MILLISECONDS;
	}

	public AbstractProfiler(PrintStream out) {
		this();
		this.setOutputStream(out);
	}

	public AbstractProfiler(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public AbstractProfiler(PrintStream out, TimeUnit timeUnit) {
		this(timeUnit);
		this.setOutputStream(out);
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isProfilingEnabled() {
		return true;
	}

	/**
	 * Defines the date format used for displaying when the output stream is
	 * set.
	 */
	@Override
	public void setDateFormat(String pattern) {
		dateFormat.applyPattern(pattern);
	}

	/**
	 * Sets the output stream.
	 * 
	 * @param out
	 */
	@Override
	public void setOutputStream(PrintStream out) {
		this.out = out;
	}

	@Override
	public TimeUnit getTimeUnit() {
		return this.timeUnit;
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
	@Override
	public void start(String key) {
		this.startTimeMap.put(key, this.getTime());
	}

	/**
	 * Stop the timer with the specified key. The get method will return the
	 * elapsed time between start and stop calls. You should called the start
	 * method with the same key before.
	 * 
	 * @param key
	 */
	@Override
	public void stop(String key) {
		Long oldTime = (Long) this.map.get(key);
		if (oldTime == null) {
			oldTime = 0L;
		}
		long elapsedTimeNano = this.getTime() - this.startTimeMap.get(key);
		Long newTime = oldTime + timeUnit.round(elapsedTimeNano, TimeUnit.NANOSECONDS);
		this.map.put(key, newTime);
		if (this.out != null) {
			this.printPrefix();
			this.out.print(key);
			this.out.print(": ");
			this.out.print(newTime);
			this.out.print(timeUnit.getAbbreviation());
			this.out.print("\n");
		}
	}

	/**
	 * Map miscellaneous data on the specified key. You can retrieve the data
	 * using the get method.
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public void put(String key, Object value) {
		this.map.put(key, value);
		if (this.out != null) {
			this.printPrefix();
			this.out.print(key);
			this.out.print(": ");
			this.out.print(value.toString());
			this.out.println();
		}
	}

	/**
	 * Increment an integer attached to the specified key.
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public void incr(String key, int value) {
		Object o = this.map.get(key);
		if (o == null) {
			o = 0;
		}
		Integer i = (Integer) o;
		i += value;
		this.map.put(key, i);
	}

	@Override
	public Object get(String key) {
		return this.map.get(key);
	}

	@Override
	public Set<Map.Entry<String, Object>> entrySet() {
		return this.map.entrySet();
	}

	@Override
	public Map<String, Object> getMap() {
		return Collections.unmodifiableMap(this.map);
	}

	/**
	 * Clear data attached to the specfied key.
	 * 
	 * @param key
	 */
	@Override
	public void clear(String key) {
		this.startTimeMap.remove(key);
		this.map.remove(key);
	}

	/**
	 * Clear all data.
	 */
	@Override
	public void clear() {
		this.map.clear();
	}

	/**
	 * If the output stream is set, print this strings.
	 */
	@Override
	public void trace(String... strings) {
		if (this.out != null) {
			this.printPrefix();
			for (String s : strings) {
				this.out.print(s);
			}
			this.out.println();
		}
	}

	/**
	 * 
	 * @return a Set of all keys used.
	 */
	@Override
	public Set<String> keySet() {
		return this.map.keySet();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\n");
		for (Map.Entry<String, Object> e : map.entrySet()) {
			sb.append("\t").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
		}
		sb.append("}\n");
		return sb.toString();

	}
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Return time in nanoseconds
	 * 
	 * @return time in nanoseconds.
	 */
	protected abstract long getTime();

	private void printPrefix() {
		this.out.print("Profiler [");
		this.out.print(dateFormat.format(new Date()));
		this.out.print("] ");
	}

	


}
