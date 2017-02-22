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
package fr.lirmm.graphik.graal.store.triplestore.rdf4j;

import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URIUtils;

class URIzer {
	private static URIzer instance;

	protected URIzer() {
		super();
	}

	public static synchronized URIzer instance() {
		if (instance == null)
			instance = new URIzer();

		return instance;
	}

	Prefix defaultPrefix = new Prefix("sail", "file:///sail/");

	/**
	 * Add default prefix if necessary
	 * 
	 * @param s
	 * @return a String which represents an URI.
	 */
	String input(String s) {
		return URIUtils.createURI(s, defaultPrefix).toString();
	}

	/**
	 * Remove default prefix if it is present
	 * 
	 * @param s
	 * @return the String s without the default prefix, if it was present.
	 */
	String output(String s) {
		if (s.startsWith(defaultPrefix.getPrefix())) {
			return s.substring(defaultPrefix.getPrefix().length());
		}
		return s;
	}
}