/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
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
