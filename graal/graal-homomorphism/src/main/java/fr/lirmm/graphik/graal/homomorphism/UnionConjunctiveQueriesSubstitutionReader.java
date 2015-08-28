/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.homomorphism;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.impl.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class UnionConjunctiveQueriesSubstitutionReader implements SubstitutionReader {

    private AtomSet atomSet;
    private Iterator<ConjunctiveQuery> cqueryIterator;
    private SubstitutionReader tmpReader;
    private boolean hasNextCallDone = false;
    
    /**
     * @param queries
     * @param atomSet
     */
    public UnionConjunctiveQueriesSubstitutionReader(UnionConjunctiveQueries queries,
            AtomSet atomSet) {
        this.cqueryIterator = queries.iterator();
        this.atomSet = atomSet;
        this.tmpReader = null;
    }

    @Override
    public boolean hasNext() {
        if (!this.hasNextCallDone) {
            this.hasNextCallDone = true;

            while ((this.tmpReader == null || !this.tmpReader.hasNext())
                    && this.cqueryIterator.hasNext()) {
                Query q = this.cqueryIterator.next();
                Homomorphism solver;
                try {
                    solver = DefaultHomomorphismFactory.getInstance().getSolver(q, this.atomSet);
                    if(solver == null) {
                    	return false;
                    } else {
                    	this.tmpReader = solver.execute(q, this.atomSet);
                    }
                } catch (HomomorphismException e) {
                    return false;
                }
            }
        }
        return this.tmpReader != null && this.tmpReader.hasNext();
    }

    @Override
    public Substitution next() {
        if (!this.hasNextCallDone)
            this.hasNext();

        this.hasNextCallDone = false;

        return this.tmpReader.next();
    }


    @Override
    public Iterator<Substitution> iterator() {
        return this;
    }

    @Override
    public void close() {
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    

}
