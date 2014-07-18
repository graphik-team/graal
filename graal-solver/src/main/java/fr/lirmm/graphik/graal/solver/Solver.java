package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.query;



/**
 * Interface for a solver.
 * @author Bruno Paiva Lima da Silva {@litteral <bplsilva@lirmm.fr>}
 */

public interface Solver {

    /**
     * @throws AtomSetException 
     * @throws SolverException 
     * 
     */
    public SubstitutionReader execute() throws SolverException;

	/*public void setQuery(Query);
	public void setAtomSet(AtomSet);*/

}
