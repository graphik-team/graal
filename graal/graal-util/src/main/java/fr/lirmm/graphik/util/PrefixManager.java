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
	private static final Map<String, Prefix> PREFIX_MAP = new TreeMap<String, Prefix>();
	private static final Map<String, Prefix> INVERSE_PREFIX_MAP = new TreeMap<String, Prefix>();
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

	private PrefixManager() {
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
	 * @return the prefix that represents the specified string as key or value.
	 *         Create a new Prefix if not exists.
	 */
	public Prefix getPrefix(String string) {
		Prefix p = PREFIX_MAP.get(string);
		if (p == null) {
			p = INVERSE_PREFIX_MAP.get(string);
			if (p == null) {
				p = genPrefix(string);
			}
		}
		return p;
	}
	
	@Override
	public Iterator<Prefix> iterator() {
		return PREFIX_MAP.values().iterator();
	}

	////////////////////////////////////////////////////////////////////////////
	// PRIVATE
	////////////////////////////////////////////////////////////////////////////
	
	private static Prefix genPrefix(String value) {
		return new Prefix(prefixString + ++prefixIndex, value);
	}
}
