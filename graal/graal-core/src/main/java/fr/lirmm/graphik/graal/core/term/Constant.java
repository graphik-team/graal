/**
 * 
 */
package fr.lirmm.graphik.graal.core.term;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface Constant extends Term {

	@Override
	String getLabel();

	@Override
	Object getIdentifier();

}
