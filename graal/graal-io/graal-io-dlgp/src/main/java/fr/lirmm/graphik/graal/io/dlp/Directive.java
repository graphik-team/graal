/**
 * 
 */
package fr.lirmm.graphik.graal.io.dlp;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class Directive {

	public enum Type {
		BASE, TOP, UNA, COMMENT
	}

	private Type type;
	private String text;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public Directive(Type type, String text) {
		this.type = type;
		this.text = text;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

}
