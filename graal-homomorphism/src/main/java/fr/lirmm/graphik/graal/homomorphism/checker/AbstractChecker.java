/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism.checker;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractChecker implements HomomorphismChecker {

	private int priority = this.getDefaultPriority();
	
	public abstract int getDefaultPriority();
	
	@Override
	public int compareTo(HomomorphismChecker o) {
		int val = o.getPriority() - this.getPriority();
		if(val == 0) {
			val = this.getClass().hashCode() - o.getClass().hashCode();
		}
		return val;
	}
	
	@Override
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	@Override
	public int getPriority() {
		return this.priority;
	}
	

}
