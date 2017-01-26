/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

import java.util.SortedSet;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismChecker;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.homomorphism.checker.AtomicQueryHomomorphismChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.BacktrackChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.DefaultUnionConjunctiveQueriesChecker;
import fr.lirmm.graphik.graal.homomorphism.checker.FullyInstantiatedQueryHomomorphismChecker;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class DefaultHomomorphismFactory implements HomomorphismFactory {
	
	private SortedSet<HomomorphismChecker> elements;
	
	private static DefaultHomomorphismFactory instance = null;
	private static ConjunctiveQuery emptyConjunctiveQuery = DefaultConjunctiveQueryFactory.instance().create();
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	private DefaultHomomorphismFactory(){
		this.elements = new TreeSet<HomomorphismChecker>();
		this.elements.add(BacktrackChecker.instance());
		this.elements.add(DefaultUnionConjunctiveQueriesChecker.instance());
		this.elements.add(FullyInstantiatedQueryHomomorphismChecker.instance());
		this.elements.add(AtomicQueryHomomorphismChecker.instance());
	}
	
	public static synchronized final DefaultHomomorphismFactory instance() {
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
