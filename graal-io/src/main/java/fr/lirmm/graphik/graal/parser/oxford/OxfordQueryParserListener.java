/**
 * 
 */
package fr.lirmm.graphik.graal.parser.oxford;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public interface OxfordQueryParserListener {

	Object getQuery();
	
	/**
	 * 
	 */
	void startQuery();

	/**
		 * 
		 */
	void startBody();

	/**
	 * 
	 */
	void endOfQuery();

	/**
		 * 
		 */
	void endOfAtom();

	/**
		 * 
		 */
	void startAtom();

	/**
	 * 
	 */
	void constant(String label);

	/**
	 * @param label
	 */
	void variable(String label);

	/**
	 * @param label
	 */
	void predicate(String label);

}
