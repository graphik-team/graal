/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining;

import java.util.EventListener;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface BackwardChainerListener extends EventListener {
	
	void startPreprocessing();
	void endPreprocessing();
	
	void startRewriting();
	void endRewriting();
	
	long getPreprocessingTime();
	long getRewritingTime();
	long getTotalTime();
	
}
