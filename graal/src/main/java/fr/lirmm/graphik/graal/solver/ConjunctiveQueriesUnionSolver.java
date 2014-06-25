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
public class ConjunctiveQueriesUnionSolver implements Solver {

    private ReadOnlyAtomSet atomSet;
    private ConjunctiveQueriesUnion queries;

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.alaska.solver.ISolver#execute()
     */
    /**
     * @param queries
     * @param atomSet
     */
    public ConjunctiveQueriesUnionSolver(ConjunctiveQueriesUnion queries,
            ReadOnlyAtomSet atomSet) {
        this.queries = queries;
        this.atomSet = atomSet;
    }

    @Override
    public SubstitutionReader execute() throws SolverException {
        return new QueriesUnionSubstitutionReader(queries, atomSet);
    }

}
