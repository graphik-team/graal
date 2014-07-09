/**
 * 
 */
package fr.lirmm.graphik.graal.writer;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import java.io.IOException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ConjunctiveQueryWriter {

	public void write(ConjunctiveQuery query) throws IOException;

}

