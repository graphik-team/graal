/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.ConjunctiveQueriesUnion;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DefaultConjunctiveQueriesUnionSolver implements ConjunctiveQueriesUnionSolver<ReadOnlyAtomSet> {

	private static DefaultConjunctiveQueriesUnionSolver instance;
    
	/**
     * @param queries
     * @param atomSet
     */
    private DefaultConjunctiveQueriesUnionSolver() {
    }
    
    public static DefaultConjunctiveQueriesUnionSolver getInstance() {
    	if(instance == null)
    		instance = new DefaultConjunctiveQueriesUnionSolver();
    	
    	return instance;
    }

	@Override
	public SubstitutionReader execute(ConjunctiveQueriesUnion queries,
			ReadOnlyAtomSet atomset) throws SolverException {
        return new QueriesUnionSubstitutionReader(queries, atomset);
	}

}
