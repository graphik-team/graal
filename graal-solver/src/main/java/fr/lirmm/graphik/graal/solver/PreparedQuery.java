/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class PreparedQuery {
    
    
    private Solver solver;
    
    public PreparedQuery(Solver solver) {
        this.solver = solver;
    }
    
    public SubstitutionReader execute() throws SolverException {
        return this.solver.execute();
    }
}
