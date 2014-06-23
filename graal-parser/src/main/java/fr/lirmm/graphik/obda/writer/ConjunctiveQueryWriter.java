/**
 * 
 */
package fr.lirmm.graphik.obda.writer;

import fr.lirmm.graphik.kb.core.DefaultConjunctiveQuery;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface ConjunctiveQueryWriter {

	void write(DefaultConjunctiveQuery query) throws WriterException;
}
