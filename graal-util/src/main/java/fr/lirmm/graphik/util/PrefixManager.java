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
package fr.lirmm.graphik.util;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class PrefixManager implements Iterable<Prefix> {

	private static final PrefixManager INSTANCE = new PrefixManager();
	private final Map<String, Prefix> PREFIX_MAP = new TreeMap<String, Prefix>();
	private final Map<String, Prefix> INVERSE_PREFIX_MAP = new TreeMap<String, Prefix>();
	
	static {
		INSTANCE.putPrefix(Prefix.XSD);
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	public PrefixManager() {
		super();
	}

	public static PrefixManager instance() {
		return INSTANCE;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public void putPrefix(Prefix prefix) {
		PREFIX_MAP.put(prefix.getPrefixName(), prefix);
		INVERSE_PREFIX_MAP.put(prefix.getPrefix(), prefix);
	}

	/**
	 * 
	 * @param string
	 * @return the prefix that represents the specified string as value. return
	 *         null if not exist.
	 */
	public Prefix getPrefixByValue(String string) {
		return INVERSE_PREFIX_MAP.get(string);
	}
	
	@Override
	public Iterator<Prefix> iterator() {
		return PREFIX_MAP.values().iterator();
	}

}
