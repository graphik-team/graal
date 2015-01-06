/**
 * 
 */
package fr.lirmm.graphik.util.stream;

import java.io.IOException;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public abstract class AbstractWriter<T> implements ObjectWriter<T> {

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.AtomWriter#write(fr.lirmm.graphik.kb.stream.AtomReader)
     */
    @Override
    public void write(Iterable<T> inputStream) throws IOException {
        for(T object : inputStream)
            this.write(object);
    }

}
