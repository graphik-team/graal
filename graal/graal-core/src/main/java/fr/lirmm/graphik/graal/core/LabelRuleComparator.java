/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Comparator;

import fr.lirmm.graphik.util.string.NumberStringComparator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class LabelRuleComparator implements Comparator<Rule> {

	private static final NumberStringComparator COMPARATOR = new NumberStringComparator();
	
	@Override
	public int compare(Rule o1, Rule o2) {
		return COMPARATOR.compare(o1.getLabel(), o2.getLabel());
	}
}
