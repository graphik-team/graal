/**
 * 
 */
package fr.lirmm.graphik.obda.writer;

import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ConjunctiveQueryWriter {

	void write(DefaultConjunctiveQuery query) throws WriterException;
}
