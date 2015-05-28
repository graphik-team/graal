/**
 * 
 */
package fr.lirmm.graphik.util;



/**
 * Immutable
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class DefaultURI implements URI {

	private Prefix prefix;
	private String localname;

	public DefaultURI(Prefix prefix, String localname) {
		this.prefix = prefix;
		this.localname = localname;
	}
	
	public DefaultURI(String prefix, String localname) {
		this.prefix = PrefixManager.getInstance().getPrefix(prefix);
		this.localname = localname;
	}
	
	public DefaultURI(String uri) {
		this.prefix = PrefixManager.getInstance().getPrefix(
				URIUtils.getPrefix(uri));
		this.localname = URIUtils.getLocalName(uri);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public Prefix getPrefix() {
		return this.prefix;
	}

	@Override
	public String getLocalname() {
		return this.localname;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof DefaultURI)) {
			return false;
		}
		DefaultURI other = (DefaultURI) obj;
		return this.toString().equals(other.toString());
	}
	
	@Override
	public String toString() {
		return this.prefix.getPrefix() + localname;
	}
}
