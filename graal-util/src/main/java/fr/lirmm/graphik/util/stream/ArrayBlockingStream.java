/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.util.stream;

import java.util.Iterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class ArrayBlockingStream<T> extends AbstractCloseableIterator<T> implements InMemoryStream<T> {

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

	@Override
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
	@Override
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
	@Override
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

	@Override
    public void close() {
		synchronized(lock) {
    		this.isOpen = false;
    		lock.notifyAll();
		}
	}

	@Override
	public void write(Iterator<T> it) {
		while (it.hasNext())
			this.write(it.next());
	}
	

}
