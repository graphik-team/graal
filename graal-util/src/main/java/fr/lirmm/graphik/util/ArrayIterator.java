package fr.lirmm.graphik.util;

import java.util.Iterator;


public class ArrayIterator<T> implements Iterator<T> {
	public T[] array;
	int i = 0;
	
	@SafeVarargs
	public ArrayIterator(T... array) {
		this.array = array;
	}

	@Override
	public boolean hasNext() {
		return i < array.length;
	}

	@Override
	public T next() {
		return array[i++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
