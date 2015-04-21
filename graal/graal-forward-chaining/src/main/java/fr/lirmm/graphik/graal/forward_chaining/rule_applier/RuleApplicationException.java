/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining.rule_applier;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RuleApplicationException extends Exception {

	private static final long serialVersionUID = 5691481969125077695L;

	public RuleApplicationException(String message, Throwable e) {
		super(message, e);
	}
}
