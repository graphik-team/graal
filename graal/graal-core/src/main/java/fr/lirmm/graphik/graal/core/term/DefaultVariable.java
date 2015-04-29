/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;



/**
 * Immutable Object
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class DefaultVariable extends AbstractTerm implements Variable {
	
	private static final long serialVersionUID = -8985351967341123126L;

	private final String label;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultVariable(Variable var) {
		this.label = var.getLabel();
	}

	public DefaultVariable(String label) {
		this.label = label;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public Term.Type getType() {
		return Term.Type.VARIABLE;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public String getIdentifier() {
		return this.label;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		return this.label;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
