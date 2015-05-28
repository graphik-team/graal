/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;


/**
 * Immutable object
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class DefaultConstant extends AbstractTerm implements Constant {

	private static final long serialVersionUID = 3531038070349085454L;

	private final Object identifier;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultConstant(Constant cst) {
		this.identifier = cst.getIdentifier();
	}
	
	public DefaultConstant(Object identifier) {
		this.identifier = identifier;
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
	public Object getIdentifier() {
		return this.identifier;
	}

}
