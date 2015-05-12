/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;

import fr.lirmm.graphik.util.Prefix;

/**
 * Immutable object
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class DefaultConstant extends AbstractTerm implements Constant {

	private static final long serialVersionUID = 3531038070349085454L;

	private final String label;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultConstant(Constant cst) {
		this.label = cst.getLabel();
	}

	public DefaultConstant(String label) {
		this.label = label;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public boolean isConstant() {
		return true;
	}
	
	@Override
	public Term.Type getType() {
		return Term.Type.CONSTANT;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public String getIdentifier() {
		return Prefix.CONSTANT.getPrefix() + this.label;
	}

}
