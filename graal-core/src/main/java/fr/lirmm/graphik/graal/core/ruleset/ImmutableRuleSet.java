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
public interface ImmutableRuleSet extends Iterable<Rule> {
	
    public boolean contains(Rule rule);

	@Override
	public Iterator<Rule> iterator();

}
