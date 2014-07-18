/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultUnionConjunctiveQueriesSolver implements UnionConjunctiveQueriesSolver<ReadOnlyAtomSet> {

	private static DefaultUnionConjunctiveQueriesSolver instance;
    
	/**
     * @param queries
     * @param atomSet
     */
    private DefaultUnionConjunctiveQueriesSolver() {
    }
    
    public static DefaultUnionConjunctiveQueriesSolver getInstance() {
    	if(instance == null)
    		instance = new DefaultUnionConjunctiveQueriesSolver();
    	
    	return instance;
    }

	@Override
	public SubstitutionReader execute(UnionConjunctiveQueries queries,
			ReadOnlyAtomSet atomset) throws SolverException {
        return new UnionConjunctiveQueriesSubstitutionReader(queries, atomset);
	}

}
