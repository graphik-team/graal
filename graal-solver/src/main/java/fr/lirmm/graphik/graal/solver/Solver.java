package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;



/**
 * Interface for a solver.
 * @author Bruno Paiva Lima da Silva <bplsilva@lirmm.fr>
 */

public interface Solver {

    /**
     * @throws AtomSetException 
     * @throws SolverException 
     * 
     */
    SubstitutionReader execute() throws SolverException;

}
