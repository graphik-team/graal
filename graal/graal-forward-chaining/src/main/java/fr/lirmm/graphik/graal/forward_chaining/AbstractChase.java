/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractChase implements Chase {

	public void execute() throws ChaseException {
		while(this.hasNext())
			this.next();
	}
};
