package fr.lirmm.graphik.alaska.solver;

import fr.lirmm.graphik.kb.exception.AtomSetException;
import fr.lirmm.graphik.kb.stream.SubstitutionReader;



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
