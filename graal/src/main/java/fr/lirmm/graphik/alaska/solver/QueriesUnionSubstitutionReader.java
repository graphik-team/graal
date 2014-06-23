/**
 * 
 */
package fr.lirmm.graphik.alaska.solver;

import java.util.Iterator;

import fr.lirmm.graphik.kb.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.kb.core.ConjunctiveQuery;
import fr.lirmm.graphik.kb.core.Query;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.core.Substitution;
import fr.lirmm.graphik.kb.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class QueriesUnionSubstitutionReader implements SubstitutionReader {

    private ReadOnlyAtomSet atomSet;
    private Iterator<ConjunctiveQuery> cqueryIterator;
    private SubstitutionReader tmpReader;
    private boolean hasNextCallDone = false;
    
    /**
     * @param queries
     * @param atomSet
     */
    public QueriesUnionSubstitutionReader(ConjunctiveQueriesUnion queries,
            ReadOnlyAtomSet atomSet) {
        this.cqueryIterator = queries.iterator();
        this.atomSet = atomSet;
        this.tmpReader = null;
    }

    @Override
    public boolean hasNext() {
        if (!this.hasNextCallDone) {
            this.hasNextCallDone = true;

            while ((this.tmpReader == null || !this.tmpReader.hasNext())
                    && this.cqueryIterator.hasNext()) {
                Query q = this.cqueryIterator.next();
                Solver solver;
                try {
                    solver = SolverFactory.getFactory().getSolver(q, this.atomSet);
                    this.tmpReader = solver.execute();
                } catch (SolverFactoryException e) {
                    return false;
                } catch (SolverException e) {
                    return false;
                }
            }
        }
        return this.tmpReader != null && this.tmpReader.hasNext();
    }

    @Override
    public Substitution next() {
        if (!this.hasNextCallDone)
            this.hasNext();

        this.hasNextCallDone = false;

        return this.tmpReader.next();
    }


    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.ISubstitutionReader#iterator()
     */
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

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    

}
