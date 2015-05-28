/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractTerm implements Term {

	private static final long serialVersionUID = 5255497469444828872L;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public int compareTo(Term o) {
		int res = this.getType().compareTo(o.getType());
		if (res == 0) {
			res = this.getIdentifier().toString()
					.compareTo(o.getIdentifier().toString());
		}
		return res;
	}

	@Override
	public String getLabel() {
		return this.getIdentifier().toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof AbstractTerm)) {
			return false;
		}
		AbstractTerm other = (AbstractTerm) obj;
		return this.compareTo(other) == 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.getType().toString().hashCode();
		result = prime * result + this.getIdentifier().hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.getIdentifier().toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

}
