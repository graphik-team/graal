/**
 * 
 */
package fr.lirmm.graphik.graal.io;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class Prefix {
	
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
}
