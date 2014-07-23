/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Iterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface ImmutableRuleSet extends Iterable<Rule> {
	
    public boolean contains(Rule rule);

	@Override
	public Iterator<Rule> iterator();

}
