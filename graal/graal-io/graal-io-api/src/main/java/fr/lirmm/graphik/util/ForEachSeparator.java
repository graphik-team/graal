/**
 * 
 */
package fr.lirmm.graphik.util;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ForEachSeparator {

	private String separator;
	private boolean first = true;
	/**
	 * @param separator
	 */
	public ForEachSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * 
	 */
	public String get() {
		if(!this.first )
			return separator;
		
		this.first = false;
		return "";
	}
	
	/**
	 * 
	 */
	public void reset() {
		this.first = true;
	}

}
