/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;

import fr.lirmm.graphik.util.URI;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Constant extends Term {

	@Override
	String getLabel();

	@Override
	URI getIdentifier();

}
