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
package fr.lirmm.graphik.util;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public enum TimeUnit {

	HOURS(java.util.concurrent.TimeUnit.HOURS, "h"), MINUTES(java.util.concurrent.TimeUnit.MINUTES, "min"), SECONDS(
			java.util.concurrent.TimeUnit.SECONDS, "s"), MILLISECONDS(java.util.concurrent.TimeUnit.MILLISECONDS,
					"ms"), MICROSECONDS(java.util.concurrent.TimeUnit.MICROSECONDS,
							"μs"), NANOSECONDS(java.util.concurrent.TimeUnit.NANOSECONDS, "ns");

	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////

	private java.util.concurrent.TimeUnit encapsuled;
	private String abbrev;

	private TimeUnit(java.util.concurrent.TimeUnit encapsuled, String abbrev) {
		this.encapsuled = encapsuled;
		this.abbrev = abbrev;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Return the abbreviation of this TimeUnit, "h" for hours, "min" for
	 * minutes, "s" for seconds, "ms" for milliseconds, "μs" for microseconds
	 * and "ns" for nanoseconds.
	 * 
	 * @return the TimeUnit abbreviation.
	 */
	public String getAbbreviation() {
		return this.abbrev;
	}

	/**
	 * Convert the given time duration in the given unit to this unit.
	 * Conversions from finer to coarser granularities truncate, so lose
	 * precision. For example converting 999 milliseconds to seconds results in
	 * 0. Conversions from coarser to finer granularities with arguments that
	 * would numerically overflow saturate to Long.MIN_VALUE if negative or
	 * Long.MAX_VALUE if positive.
	 * 
	 * For example, to convert 10 minutes to milliseconds, use:
	 * TimeUnit.MILLISECONDS.convert(10L, TimeUnit.MINUTES)
	 * 
	 * @param sourceDuration
	 *            the time duration in the given sourceUnit
	 * @param sourceUnit
	 *            the unit of the sourceDuration argument
	 * @return the converted duration in this unit, or Long.MIN_VALUE if
	 *         conversion would negatively overflow, or Long.MAX_VALUE if it
	 *         would positively overflow.
	 */
	public long convert(long sourceDuration, TimeUnit sourceUnit) {
		return this.encapsuled.convert(sourceDuration, sourceUnit.encapsuled);
	}

	/**
	 * Round the given time duration in the given unit to this unit. Conversions
	 * from finer to coarser granularities are rounded. For example converting
	 * 999 milliseconds to seconds results in 1, 500 also results in 1, 499
	 * results in 0. Conversions from coarser to finer granularities is
	 * equivalent to this.convert(sourceDuration, timeUnit).
	 * 
	 * @param sourceDuration
	 *            the time duration in the given sourceUnit
	 * @param timeUnit
	 *            the unit of the sourceDuration argument
	 * @return the rounded duration in this unit, or Long.MIN_VALUE if
	 *         conversion would negatively overflow, or Long.MAX_VALUE if it
	 *         would positively overflow.
	 */
	public long round(long sourceDuration, TimeUnit timeUnit) {
		long i = timeUnit.convert(1, this);
		if (i == 0) {
			// convert to a finer granularity
			return this.convert(sourceDuration, timeUnit);
		}
		return (sourceDuration + i / 2) / i;
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONVERT HELPERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Equivalent to HOURS.convert(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration
	 */
	public long toHours(long duration) {
		return HOURS.convert(duration, this);
	}

	/**
	 * Equivalent to MICROSECONDS.convert(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration
	 */
	public long toMicros(long duration) {
		return MICROSECONDS.convert(duration, this);
	}

	/**
	 * Equivalent to MILLISECONDS.convert(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration
	 */
	public long toMillis(long duration) {
		return MILLISECONDS.convert(duration, this);
	}

	/**
	 * Equivalent to MINUTES.convert(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration
	 */
	public long toMinutes(long duration) {
		return MINUTES.convert(duration, this);
	}

	/**
	 * Equivalent to NANOSECONDS.convert(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration
	 */
	public long toNanos(long duration) {
		return NANOSECONDS.convert(duration, this);
	}

	/**
	 * Equivalent to SECONDS.convert(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the converted duration
	 */
	public long toSeconds(long duration) {
		return SECONDS.convert(duration, this);
	}

	// /////////////////////////////////////////////////////////////////////////
	// ROUND HELPERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Equivalent to HOURS.round(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the rounded duration
	 */
	public long roundToHours(long duration) {
		return HOURS.round(duration, this);
	}

	/**
	 * Equivalent to MICROSECONDS.round(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the rounded duration
	 */
	public long roundToMicros(long duration) {
		return MICROSECONDS.round(duration, this);
	}

	/**
	 * Equivalent to MILLISECONDS.round(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the rounded duration
	 */
	public long roundToMillis(long duration) {
		return MILLISECONDS.round(duration, this);
	}

	/**
	 * Equivalent to MINUTES.round(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the rounded duration
	 */
	public long roundToMinutes(long duration) {
		return MINUTES.round(duration, this);
	}

	/**
	 * Equivalent to NANOSECONDS.round(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the rounded duration
	 */
	public long roundToNanos(long duration) {
		return NANOSECONDS.round(duration, this);
	}

	/**
	 * Equivalent to SECONDS.round(duration, this).
	 * 
	 * @param duration
	 *            the duration
	 * @return the rounded duration
	 */
	public long roundToSeconds(long duration) {
		return SECONDS.round(duration, this);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDED METHODS
	// /////////////////////////////////////////////////////////////////////////

	public String toString() {
		return this.encapsuled.toString();
	}

}
