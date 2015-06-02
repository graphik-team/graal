/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;

import fr.lirmm.graphik.util.URI;



/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface TermFactory {
	
	// /////////////////////////////////////////////////////////////////////////
	//
	// /////////////////////////////////////////////////////////////////////////
	
	Term createTerm(Term term);

	Term createTerm(Object o, Term.Type type);

	Variable createVariable(Object identifier);

	Constant createConstant(Object identifier);

	Literal createLiteral(Object value);

	Literal createLiteral(URI datatype, Object value);

}
