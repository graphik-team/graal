/**
 * 
 */
package fr.lirmm.graphik.graal.rulesetanalyser.util;

import fr.lirmm.graphik.graal.core.Predicate;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class PredicatePosition  implements Comparable<PredicatePosition> {
	
	public Predicate predicate;
	public int position;

	public PredicatePosition(Predicate predicate, int position) {
		this.predicate = predicate;
		this.position = position;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(PredicatePosition other) {
		int res = this.predicate.compareTo(other.predicate);
		if(res == 0) {
			res = this.position - other.position;
		}
		return res;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public int hashCode() {
//		final int prime = 433;
//		int result = 1;
//		result = prime * result + this.predicate.hashCode();
//		result = prime * result + this.position;
//		return result;
		return this.toString().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || !(o instanceof PredicatePosition)) {
			return false;
		}

		PredicatePosition other = (PredicatePosition) o;
		return this.predicate.equals(other.predicate) && this.position == other.position;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(this.predicate.getIdentifier());
		s.append('[');
		s.append(this.position);
		s.append(']');
		return s.toString();
	}
	
}
