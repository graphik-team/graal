/**
 * 
 */
package fr.lirmm.graphik.util.stream;

import java.io.IOException;
import java.util.Iterator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
@Deprecated
public abstract class AbstractReader<T> implements ObjectReader<T> {

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.IAtomReader#iterator()
     */
    @Override
    public Iterator<T> iterator() {
        return this;
    }

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.AtomReader#read(fr.lirmm.graphik.kb.stream.AtomWriter)
     */
    @Override
    public void read(ObjectWriter<T> writer) throws IOException {
        for(T object : this)
            writer.write(object);
    }
}
