/**
 * 
 */
package fr.lirmm.graphik.alaska.solver;

import fr.lirmm.graphik.kb.stream.SubstitutionReader;

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
