/**
 * 
 */
package fr.lirmm.graphik.graal.core.ruleset;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public abstract class AbstractRuleSet implements RuleSet {

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.toString(sb);
		return sb.toString();
	}

	public void toString(StringBuilder sb) {
		sb.append('[');
		boolean isFirst = true;
		for (Rule r : this) {
			if (!isFirst) {
				sb.append(',');
			}
			sb.append(r.toString());
			isFirst = false;
		}
		sb.append(']');
	}

}
