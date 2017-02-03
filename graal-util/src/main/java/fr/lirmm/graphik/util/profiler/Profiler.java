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
package fr.lirmm.graphik.util.profiler;

import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import fr.lirmm.graphik.util.TimeUnit;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Profiler {
	/**
	 * Defines the date format used for displaying when the output stream is
	 * set.
	 */
	void setDateFormat(String pattern);

	/**
	 * Sets the output stream.
	 * 
	 * @param out
	 */
	void setOutputStream(PrintStream out);

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public boolean isProfilingEnabled();

	/**
	 * Start a timer with a specified key/identifier. If you recall this method
	 * with the same key, you will erase the start time for the key.
	 * 
	 * @param key
	 */
	public void start(String key);

	/**
	 * Stop the timer with the specified key. The get method will return the
	 * elapsed time between start and stop calls. You should called the start
	 * method with the same key before.
	 * 
	 * @param key
	 */
	public void stop(String key);

	/**
	 * Map miscellaneous data on the specified key. You can retrieve the data
	 * using the get method.
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value);

	/**
	 * Increment an integer attached to the specified key.
	 * 
	 * @param key
	 * @param value
	 */
	public void incr(String key, int value);

	/**
	 * Get data/time attached to the specified key.
	 * 
	 * @param key
	 * @return data/time attached to the specified key.
	 */
	public Object get(String key);

	public Set<Map.Entry<String, Object>> entrySet();

	/**
	 * Clear data attached to the specfied key.
	 * 
	 * @param key
	 */
	public void clear(String key);

	/**
	 * Clear all data.
	 */
	public void clear();

	/**
	 * If the output stream is set, print this strings.
	 * 
	 * @param strings
	 */
	public void trace(String... strings);

	/**
	 * 
	 * @return a Set of all keys used.
	 */
	public Set<String> keySet();

	/**
	 * @return the map behind this profiler.
	 */
	Map<String, Object> getMap();

	/**
	 * @return the {@link TimeUnit} in used.
	 */
	TimeUnit getTimeUnit();
}
