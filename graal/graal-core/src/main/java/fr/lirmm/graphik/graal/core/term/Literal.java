/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;

import fr.lirmm.graphik.util.URI;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Literal extends Term {

	Object getValue();

	URI getDatatype();

	@Override
	String getIdentifier();

}
