/**
 * 
 */
package fr.lirmm.graphik.util;


/**
 * Immutable
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class Prefix {

	public static final Prefix EMPTY = new Prefix("", "");
	public static final Prefix CONSTANT = new Prefix("constant",
			"graal:constant#");
	public static final Prefix LITERAL = new Prefix("literal", "graal:literal#");
	public static final Prefix VARIABLE = new Prefix("variable", "graal:variable#");
	public static final Prefix PREDICATE = new Prefix("predicate",
			"graal:predicate#");
	public static final Prefix XSD = new Prefix("xsd",
			"http://www.w3.org/2001/XMLSchema#");
	
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
