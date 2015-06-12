/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
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

	private static final PrefixManager instance = new PrefixManager();
	private final Map<String, Prefix> PREFIX_MAP = new TreeMap<String, Prefix>();
	private final Map<String, Prefix> INVERSE_PREFIX_MAP = new TreeMap<String, Prefix>();
	private static final String prefixString = "graal";
	private static int prefixIndex = 0;
	
	static {
		instance.putPrefix(Prefix.EMPTY);
		instance.putPrefix(Prefix.LITERAL);
		instance.putPrefix(Prefix.VARIABLE);
		instance.putPrefix(Prefix.CONSTANT);
		instance.putPrefix(Prefix.PREDICATE);
		instance.putPrefix(Prefix.XSD);
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	public PrefixManager() {
		super();
	}

	public static PrefixManager getInstance() {
		return instance;
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

	////////////////////////////////////////////////////////////////////////////
	// PRIVATE
	////////////////////////////////////////////////////////////////////////////
	
	public Prefix genAndPutPrefix(String value) {
		Prefix p = new Prefix(prefixString + ++prefixIndex, value);
		this.putPrefix(p);
		return p;
	}
}
