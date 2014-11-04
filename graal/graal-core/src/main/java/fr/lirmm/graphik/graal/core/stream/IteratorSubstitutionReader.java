/**
 * 
 */
package fr.lirmm.graphik.graal.core.stream;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Substitution;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class IteratorSubstitutionReader implements SubstitutionReader {
    
    private Iterator<Substitution> iterator;
    
    public IteratorSubstitutionReader(Iterator<Substitution>  iterator) {
        this.iterator = iterator;
    }

    @Override
    public void remove() {
        this.iterator.remove();
    }

    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }

    @Override
    public Substitution next() {
        return this.iterator.next();
    }

    @Override
    public Iterator<Substitution> iterator() {
        return this;
    }

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.ISubstitutionReader#close()
     */
    @Override
    public void close() {
    }
    
    

}
