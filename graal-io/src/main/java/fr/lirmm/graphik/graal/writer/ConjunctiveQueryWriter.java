/**
 * 
 */
package fr.lirmm.graphik.graal.writer;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ConjunctiveQueryWriter {

	void write(ConjunctiveQuery query) throws WriterException;
}
