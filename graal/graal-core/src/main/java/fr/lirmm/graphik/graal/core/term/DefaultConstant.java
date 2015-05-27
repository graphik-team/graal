/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;

import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URI;

/**
 * Immutable object
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class DefaultConstant extends AbstractTerm implements Constant {

	private static final long serialVersionUID = 3531038070349085454L;

	private final URI uri;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public DefaultConstant(Constant cst) {
		this.uri = cst.getIdentifier();
	}

	public DefaultConstant(String label) {
		this.uri = new DefaultURI(Prefix.CONSTANT, label);
	}
	
	public DefaultConstant(URI uri) {
		this.uri = uri;
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
		return this.uri.getLocalname();
	}

	@Override
	public URI getIdentifier() {
		return this.uri;
	}

}
