package fr.lirmm.graphik.util.stream;


public class ArrayCloseableIterator<T> extends AbstractCloseableIterator<T> {
	
	private T[] array;
	private int indexIt = 0;
	
	public ArrayCloseableIterator(T... args) {
		array = args;
	}
	
	@Override
	public boolean hasNext() throws IteratorException {
		return indexIt < array.length;
	}

	@Override
	public T next() throws IteratorException {
		return array[indexIt++];
	}

	@Override
	public void close() {
		// do nothing
	}

}
