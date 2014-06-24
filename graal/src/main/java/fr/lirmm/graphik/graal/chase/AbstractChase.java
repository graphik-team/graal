/**
 * 
 */
package fr.lirmm.graphik.graal.chase;

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
