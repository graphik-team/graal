/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.io.IOException;

import fr.lirmm.graphik.graal.core.Rule;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface RuleWriter extends GraalWriter {

	public void write(Rule rule) throws IOException;
}
