/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.io.IOException;

import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Writer {

	void flush() throws IOException;

	void close() throws IOException;
	
	void write(Prefix prefix) throws IOException;

	void writeComment(String string) throws IOException;

}
