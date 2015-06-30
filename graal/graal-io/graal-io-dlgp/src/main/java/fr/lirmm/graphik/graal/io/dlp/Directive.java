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
	private Object value;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public Directive(Type type, Object value) {
		this.type = type;
		this.value = value;
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
	public Object getValue() {
		return value;
	}

}
