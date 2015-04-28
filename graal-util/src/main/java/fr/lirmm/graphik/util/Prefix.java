/**
 * 
 */
package fr.lirmm.graphik.util;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class Prefix {

	public static final Prefix EMPTY = new Prefix("", "");
	public static final Prefix CONSTANT = new Prefix("literal",
			"graal:constant#");
	public static final Prefix LITERAL = new Prefix("literal", "graal:literal#");
	public static final Prefix VARIABLE = new Prefix("variable", "graal:variable#");
	public static final Prefix PREDICATE = new Prefix("predicate",
			"graal:predicate#");
	
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
