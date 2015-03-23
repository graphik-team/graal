/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import java.io.IOException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ConjunctiveQueryWriter extends GraalWriter {

	public void write(ConjunctiveQuery query) throws IOException;

}

