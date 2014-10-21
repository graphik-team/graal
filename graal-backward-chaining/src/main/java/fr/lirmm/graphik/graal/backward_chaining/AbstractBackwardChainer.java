/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining;

import javax.swing.event.EventListenerList;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public abstract class AbstractBackwardChainer implements BackwardChainer {

	private final EventListenerList listeners = new EventListenerList();

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBSERVABLE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void addListener(BackwardChainerListener listener) {
		this.listeners.add(BackwardChainerListener.class, listener);
	}

	@Override
	public void deleteListener(BackwardChainerListener listener) {
		this.listeners.remove(BackwardChainerListener.class, listener);
	}

	protected BackwardChainerListener[] getListeners() {
		return listeners.getListeners(BackwardChainerListener.class);
	}

}
