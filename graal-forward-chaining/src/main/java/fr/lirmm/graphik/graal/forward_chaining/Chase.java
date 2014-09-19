/**
 * 
 */
package fr.lirmm.graphik.graal.forward_chaining;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public interface Chase {

	/**
	 * Sature the fact base
	 */
	public void execute() throws ChaseException;;
	
	/**
	 * Execute the next step of the saturation process
	 * @throws ChaseException 
	 */
	public void next() throws ChaseException;
	
	public boolean hasNext();
	
}
