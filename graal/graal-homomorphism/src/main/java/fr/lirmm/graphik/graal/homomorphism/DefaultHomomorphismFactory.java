/**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

import java.util.SortedSet;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.homomorphism.checker.DefaultUnionConjunctiveQueriesChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.HomomorphismChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.RecursiveBacktrackChecker;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class DefaultHomomorphismFactory implements HomomorphismFactory {
	
	private SortedSet<HomomorphismChecker> elements;
	
	private static DefaultHomomorphismFactory instance = null;
	private static ConjunctiveQuery emptyConjunctiveQuery = new DefaultConjunctiveQuery();
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	private DefaultHomomorphismFactory(){
		this.elements = new TreeSet<HomomorphismChecker>();
		this.elements.add(new RecursiveBacktrackChecker());
		this.elements.add(new DefaultUnionConjunctiveQueriesChecker());
	}
	
	public static synchronized final DefaultHomomorphismFactory getInstance() {
		if(instance == null)
			instance = new DefaultHomomorphismFactory();
		
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
	public boolean addChecker(HomomorphismChecker checker) {
		return this.elements.add(checker);
	}

	@Override
	public Homomorphism<? extends Query, ? extends AtomSet> getConjunctiveQuerySolver(
			AtomSet atomset) {
		Homomorphism<? extends Query, ? extends AtomSet> solver = null;
		for (HomomorphismChecker e : elements) {
			if (e.check(emptyConjunctiveQuery, atomset)) {
				solver = e.getSolver();
				break;
			}
		}
		return solver;
	}
 	
    @Override
    public Homomorphism<? extends Query, ? extends AtomSet> getSolver(Query query, AtomSet atomset) {
    	Homomorphism<? extends Query, ? extends AtomSet> solver = null;
    	for(HomomorphismChecker e : elements) {
    		if(e.check(query, atomset)) {
    			solver = e.getSolver();
    			break;
    		}
    	}
    	return solver;
    }
    
}
