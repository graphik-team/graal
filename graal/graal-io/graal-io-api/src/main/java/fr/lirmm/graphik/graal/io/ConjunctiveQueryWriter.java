/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.io.IOException;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ConjunctiveQueryWriter extends Writer {

	void write(ConjunctiveQuery query) throws IOException;

	@Override
	void flush() throws IOException;

	@Override
	void close() throws IOException;

}

