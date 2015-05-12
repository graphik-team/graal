/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;

import org.apache.commons.lang3.StringUtils;

import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.URIUtils;

/**
 * Not immutable but it is not possible with an Object as constructor parameter
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
final class DefaultLiteral extends AbstractTerm implements Literal {

	private static final long serialVersionUID = -8168240181900479256L;

	private final Object value;
	private final URI datatype;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public DefaultLiteral(Literal lit) {
		this.value = lit.getValue();
		this.datatype = lit.getDatatype();
	}

	public DefaultLiteral(Object value) {
		this.value = value;
		this.datatype = URIUtils.createURI("java:"
				+ StringUtils
				.reverseDelimited(value.getClass().getCanonicalName(), '.'));
	}

	public DefaultLiteral(URI datatype, Object value) {
		this.datatype = datatype;
		this.value = value;
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
		return Term.Type.LITERAL;
	}

	@Override
	public Object getValue() {
		return this.value;
	}

	@Override
	public String getLabel() {
		return this.value.toString();
	}

	@Override
	public URI getDatatype() {
		return this.datatype;
	}

	@Override
	public String getIdentifier() {
		return this.getDatatype().toString() + this.value.toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////


}
