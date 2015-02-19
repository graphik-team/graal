/**
 * 
 */
package fr.lirmm.graphik.util;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class Prefix {

	public static final Prefix DEFAULT = new Prefix("graal", "graal:");
	public static final Prefix EMPTY = new Prefix("", ":");
	public static final Prefix LITERAL = new Prefix("literal", "literal:");
	public static final Prefix VARIABLE = new Prefix("variable", "variable:");
	
	public static final Map<String, Prefix> PREFIX_MAP = new TreeMap<String, Prefix>();
	public static final Map<String, Prefix> INVERSE_PREFIX_MAP = new TreeMap<String, Prefix>();
	static {
		PREFIX_MAP.put(DEFAULT.getPrefixName(), DEFAULT);
		INVERSE_PREFIX_MAP.put(DEFAULT.getPrefix(), DEFAULT);
	}
	
	////////////////////////////////////////////////////////////////////////////
	// 
	////////////////////////////////////////////////////////////////////////////
	
	private String prefixName;
	private String prefix;
	
	public Prefix(String prefixName, String prefix) {
		this.prefixName = prefixName;
		this.prefix = prefix;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param string
	 * @return the prefix that represents the specified string as key or value. 
	 * Create a new Prefix if not exists.
	 */
	public static Prefix getPrefix(String string) {
		Prefix p = PREFIX_MAP.get(string);
		if (p == null) {
			p = INVERSE_PREFIX_MAP.get(string);
			if (p == null) {
				p = new Prefix(string, string);
			}
		}
		return p;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public String getPrefixName() {
		return this.prefixName;
	}
	
	public String getPrefix() {
		return prefix;
	}
		
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String toString() {
		return "prefix[" + this.prefixName + ", " + this.prefix + "]";
	}
}
