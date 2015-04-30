/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.io.IOException;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface AtomSetWriter extends Writer {

	public void write(AtomSet atomSet) throws IOException;

	@Override
	void flush() throws IOException;

	@Override
	void close() throws IOException;

}
