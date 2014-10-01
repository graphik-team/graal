/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultUnionConjunctiveQueriesHomomorphism implements UnionConjunctiveQueriesHomomorphism<ReadOnlyAtomSet> {

	private static DefaultUnionConjunctiveQueriesHomomorphism instance;
    
	/**
     * @param queries
     * @param atomSet
     */
    private DefaultUnionConjunctiveQueriesHomomorphism() {
    }
    
    public static synchronized DefaultUnionConjunctiveQueriesHomomorphism getInstance() {
    	if(instance == null)
    		instance = new DefaultUnionConjunctiveQueriesHomomorphism();
    	
    	return instance;
    }

	@Override
	public SubstitutionReader execute(UnionConjunctiveQueries queries,
			ReadOnlyAtomSet atomset) throws HomomorphismException {
        return new UnionConjunctiveQueriesSubstitutionReader(queries, atomset);
	}

}
