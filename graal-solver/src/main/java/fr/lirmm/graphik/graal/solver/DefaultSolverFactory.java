/**
 * 
 */
package fr.lirmm.graphik.graal.solver;

import java.util.SortedSet;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.solver.checker.DefaultUnionConjunctiveQueriesSolverChecker;
import fr.lirmm.graphik.graal.solver.checker.RecursiveBacktrackSolverChecker;
import fr.lirmm.graphik.graal.solver.checker.SolverFactoryChecker;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class DefaultSolverFactory implements SolverFactory {
	
	private SortedSet<SolverFactoryChecker> elements;
	
	private static DefaultSolverFactory instance = null;

	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	private DefaultSolverFactory(){
		this.elements = new TreeSet<SolverFactoryChecker>();
		this.elements.add(new RecursiveBacktrackSolverChecker());
		this.elements.add(new DefaultUnionConjunctiveQueriesSolverChecker());
	}
	
	public static final DefaultSolverFactory getInstance() {
		if(instance == null)
			instance = new DefaultSolverFactory();
		
		return instance;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 * @param checker
	 * @return true if this checker is not already added, false otherwise.
	 */
	public boolean addChecker(SolverFactoryChecker checker) {
		return this.elements.add(checker);
	}
 	
    @Override
    public Solver<? extends Query, ? extends ReadOnlyAtomSet> getSolver(Query query, ReadOnlyAtomSet atomset) {
    	Solver<? extends Query, ? extends ReadOnlyAtomSet> solver = null;
    	for(SolverFactoryChecker e : elements) {
    		if(e.check(query, atomset)) {
    			solver = e.getSolver();
    			break;
    		}
    	}
    	return solver;
    }
    
}
