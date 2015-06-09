/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.core.ruleset;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public abstract class AbstractRuleSet implements RuleSet {

	@Override
	public boolean addAll(Iterator<Rule> ruleIterator) {
		boolean isChanged = false;
		while(ruleIterator.hasNext()) {
			isChanged = this.add(ruleIterator.next()) || isChanged;
		}
		return isChanged;
	}
	
	@Override
	public boolean removeAll(Iterator<Rule> ruleIterator) {
		boolean isChanged = false;
		while(ruleIterator.hasNext()) {
			isChanged = this.remove(ruleIterator.next()) || isChanged;
		}
		return isChanged;
	}
	
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
