/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.io.IOException;

import fr.lirmm.graphik.graal.core.Atom;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface AtomWriter extends Writer {

	public void write(Atom atom) throws IOException;

	@Override
	void flush() throws IOException;

	@Override
	void close() throws IOException;
}
