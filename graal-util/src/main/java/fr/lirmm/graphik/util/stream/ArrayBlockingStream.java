/**
 * 
 */
package fr.lirmm.graphik.util.stream;

import java.io.IOException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class ArrayBlockingStream<T> extends AbstractReader<T> implements
		ObjectWriter<T> {

	final int MIN_QUEUE;
	private final Object[] buffer;
	private final Object lock;
	private boolean isOpen = true;
	/**
	 * The next index to write
	 */
	private int writeIndex = 0;
	private int readIndex = 0;
	private int size = 0;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public ArrayBlockingStream(int bufferSize) {
		this.buffer = new Object[bufferSize];
		this.MIN_QUEUE = bufferSize/3;
		this.lock = new Object();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public void write(T object) {
		synchronized(lock) {
    		while (this.isOpen && this.size == this.buffer.length) {
    			try {
    				lock.wait();
    			} catch (InterruptedException e) {
    			}
    		}
    		this.buffer[this.writeIndex] = object;
    		++this.size;
    		writeIndex = (writeIndex + 1) % buffer.length;
    		lock.notifyAll();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.kb.stream.AtomReader#hasNext()
	 */
	public boolean hasNext() {
		synchronized(lock) {
    		while (this.isOpen && this.size == 0) {
    			try {
    				lock.wait();
    			} catch (InterruptedException e) {
    			}
    		}
    
    		return this.size > 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.kb.stream.AtomReader#next()
	 */
	@SuppressWarnings("unchecked")
	public T next() {
		synchronized(lock) {
    		this.hasNext();
    		Object object = this.buffer[this.readIndex];
    		--this.size;
    		this.readIndex = (this.readIndex + 1) % this.buffer.length;
    
    		if (this.size == MIN_QUEUE) {
    			lock.notifyAll();
    		}
    		
    		return (T) object;
		}
	}

	public void close() {
		synchronized(lock) {
    		this.isOpen = false;
    		lock.notifyAll();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * fr.lirmm.graphik.kb.stream.AtomWriter#write(fr.lirmm.graphik.kb.stream
	 * .AtomReader)
	 */
	@Override
	public void write(Iterable<T> inputStream) throws IOException {
		for (T object : inputStream)
			this.write(object);
	}
	

}
