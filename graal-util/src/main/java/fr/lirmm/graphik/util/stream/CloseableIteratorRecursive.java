package fr.lirmm.graphik.util.stream;

import java.util.Stack;

/**
 * This Iterator iterate recursively on Iterator or CloseableIterable results
 * from a primary iterator.
 * 
 * @author Olivier Rodriguez
 */
public class CloseableIteratorRecursive<E> extends AbstractCloseableIterator<E> {

	private Stack<CloseableIterator<E>> stackIterator;
	private CloseableIterator<E> currentIterator;
	private E next;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public CloseableIteratorRecursive(CloseableIterator<E> primaryIterator) {
		stackIterator = new Stack<>();
		currentIterator = primaryIterator;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasNext() throws IteratorException {

		if (next != null)
			return true;

		if (currentIterator == null)
			return false;

		while (!currentIterator.hasNext()) {
			currentIterator.close();

			if (stackIterator.isEmpty()) {
				currentIterator = null;
				return false;
			}
			currentIterator = stackIterator.pop();
		}
		E next = currentIterator.next();

		if (next instanceof CloseableIterator) {
			stackIterator.push(currentIterator);
			currentIterator = (CloseableIterator<E>) next;
			return currentIterator.hasNext();
		} else if (next instanceof CloseableIterable) {
			stackIterator.push(currentIterator);
			currentIterator = ((CloseableIterable<E>) next).iterator();
			return currentIterator.hasNext();
		}
		this.next = next;
		return true;
	}

	@Override
	public E next() throws IteratorException {

		if (next == null)
			this.hasNext();

		E ret = next;
		next = null;
		return ret;
	}

	@Override
	public void close() {

		if (currentIterator == null)
			return;

		currentIterator.close();

		while (!stackIterator.isEmpty()) {
			stackIterator.pop().close();
		}
	}
}
