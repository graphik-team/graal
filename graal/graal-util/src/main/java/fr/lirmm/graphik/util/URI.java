/**
 * 
 */
package fr.lirmm.graphik.util;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface URI {
	
	/**
	 * @return the string representation of this URI.
	 */
	String toString();
	
	/**
	 * Get the prefix of this URI.
	 * 
	 * {@literal (.*)[:/#]([^:/#]*)}
	 * 
	 * @return the first group of the regex pattern above.
	 */
	Prefix getPrefix();
	
	/**
	 * Get the localname of this URI.
	 * 
	 * {@literal (.*)[:/#]([^:/#]*)}
	 * 
	 * @return the second group of the regex pattern above.
	 */
	String getLocalname();

}
