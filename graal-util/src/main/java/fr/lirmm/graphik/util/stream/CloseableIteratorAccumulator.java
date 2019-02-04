package fr.lirmm.graphik.util.stream;

import java.util.ArrayList;
import java.util.List;

/**
 * This Iterator stores the items passed in a buffer memory.
 * 
 * @author Olivier Rodriguez
 */
public class CloseableIteratorAccumulator<E> extends AbstractCloseableIterator<E> {
	private CloseableIterator<E> it;
	List<E> accu = new ArrayList<>();
	boolean hasNext = false;
	boolean nextIsStore = false;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public CloseableIteratorAccumulator(CloseableIterator<E> it) throws IteratorException {
		this.it = it;
		hasNext = it.hasNext();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() throws IteratorException {

		if (nextIsStore) {
			hasNext = it.hasNext();
			nextIsStore = false;
		}
		return hasNext;
	}

	@Override
	public E next() throws IteratorException {
		E ret = it.next();

		if (!nextIsStore) {
			accu.add(ret);
			nextIsStore = true;
		}
		return ret;
	}

	@Override
	public void close() {
		it.close();
	}

	/**
	 * Consume the iterator (iterate until the last element).
	 * 
	 * @return
	 * @throws IteratorException
	 */
	public CloseableIteratorAccumulator<E> consumeAll() throws IteratorException {

		while (hasNext())
			next();

		return this;
	}

	@SuppressWarnings("unchecked")
	public E[] toArray() {
		return (E[]) accu.toArray();
	}

	public List<E> getList() {
		// Get a copy for not being able to modify the $accu outside.
		return new ArrayList<>(accu);
	}
}
