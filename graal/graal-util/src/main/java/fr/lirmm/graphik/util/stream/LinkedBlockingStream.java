/**
 * 
 */
package fr.lirmm.graphik.util.stream;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class LinkedBlockingStream<T> extends AbstractReader<T> implements ObjectWriter<T> {

    final int MAX_QUEUE;
    final int MIN_QUEUE;
    
    private final Object lock;
    private final Queue<T> queue = new LinkedList<T>();
    private boolean isOpen = true;

    // /////////////////////////////////////////////////////////////////////////
    //	CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    public LinkedBlockingStream() {
        this.MAX_QUEUE = 1024;
        this.MIN_QUEUE = 32;
        this.lock = new Object();
    }
    
    // /////////////////////////////////////////////////////////////////////////
    //	METHODS
    // /////////////////////////////////////////////////////////////////////////

    public  void write(T object) {
    	synchronized(lock) {
            while(this.isOpen && this.queue.size() >= MAX_QUEUE ) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
            this.queue.add(object);
            lock.notifyAll();
    	}
    }

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.AtomReader#hasNext()
     */
    public boolean hasNext() {
    	synchronized(lock) {
            while(this.isOpen && this.queue.size() == 0) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
                   
            return !this.queue.isEmpty();
    	}
    }
    
    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.AtomReader#next()
     */
    public T next() {
    	synchronized(lock) {
            this.hasNext();
            T object = this.queue.poll();
            
            if(this.queue.size() <= MIN_QUEUE)
                lock.notifyAll();
            
            return object;
    	}
    }

    public void close(){
    	synchronized(lock) {
    		this.isOpen = false;
    		lock.notifyAll();
    	}
    }

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.AtomWriter#write(fr.lirmm.graphik.kb.stream.AtomReader)
     */
    @Override
    public void write(Iterable<T> inputStream) throws IOException {
       for(T object : inputStream)
           this.write(object);
    }



}
