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
 package fr.lirmm.graphik.graal.homomorphism;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.stream.IteratorSubstitutionReader;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * Implementation of a backtrack solving algorithm.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class RecursiveBacktrackHomomorphism implements Homomorphism<ConjunctiveQuery, AtomSet> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(RecursiveBacktrackHomomorphism.class);

	private static RecursiveBacktrackHomomorphism instance;
	private Set<Term> domain;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    private RecursiveBacktrackHomomorphism(){}
    
    public static synchronized RecursiveBacktrackHomomorphism getInstance() {
    	if(instance == null)
    		instance = new RecursiveBacktrackHomomorphism();
    	
    	return instance;
    }

    // /////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS
    // /////////////////////////////////////////////////////////////////////////

    /***
     * 
     * @return A SubstitutionReader that enumerate all substitutions.
     * @throws AtomSetException
     */
    @Override
    public SubstitutionReader execute(ConjunctiveQuery query, AtomSet facts) throws HomomorphismException {
        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace(query.toString());
        }
        List<Term> orderedVars = order(query.getAtomSet().getTerms(
                Term.Type.VARIABLE));
        Collection<Atom>[] queryAtomRanked = getAtomRank(
                query.getAtomSet(), orderedVars);

        try {
			this.domain = facts.getTerms();

            if (isHomomorphism(queryAtomRanked[0], facts,
                    new HashMapSubstitution())) {
                return new IteratorSubstitutionReader(homomorphism(query,
                        queryAtomRanked, facts, new HashMapSubstitution(),
                        orderedVars, 1).iterator());
            } else {
                // return false
                return new IteratorSubstitutionReader(
                        new LinkedList<Substitution>().iterator());
            }
        } catch (Exception e) {
            throw new HomomorphismException(e.getMessage(), e);
        }
    }

    /**
     * 
     * @param atomSet1
     * @param atomSet2
     * @return
     * @throws HomomorphismException
     */
	public boolean exist(AtomSet atomSet1, AtomSet atomSet2)
			throws HomomorphismException {
		try {
			List<Term> orderedVars = order(atomSet1.getTerms(Term.Type.VARIABLE));
			Collection<Atom>[] queryAtomRanked = getAtomRank(atomSet1, orderedVars);
		
			if (isHomomorphism(queryAtomRanked[0], atomSet2,
					new HashMapSubstitution())) {
				return existHomomorphism(atomSet1, queryAtomRanked, atomSet2,
						new HashMapSubstitution(), orderedVars, 1);
			} else {
				return false;
			}
		} catch (Exception e) {
			throw new HomomorphismException(e.getMessage(), e);
		}
	}

    // /////////////////////////////////////////////////////////////////////////
    // PRIVATE METHODS
    // /////////////////////////////////////////////////////////////////////////

    /**
     * 
     * @param queryAtomRanked
     * @param facts
     * @param substitution
     * @param orderedVars
     * @param rank
     * @return
     * @throws Exception
     */
	private Collection<Substitution> homomorphism(ConjunctiveQuery query,
            Collection<Atom>[] queryAtomRanked, AtomSet facts,
            Substitution substitution, List<Term> orderedVars, int rank)
            throws Exception {
        Collection<Substitution> substitutionList = new LinkedList<Substitution>();
        if (orderedVars.size() == 0) {
            Substitution filteredSub = new HashMapSubstitution();
            for (Term var : query.getAnswerVariables()) {
                filteredSub.put(var, substitution.createImageOf(var));
            }
            substitutionList.add(filteredSub);
        } else {
			Term var = orderedVars.remove(0);
			for (Term substitut : domain) {
                Substitution tmpSubstitution = new HashMapSubstitution(
                        substitution);
                tmpSubstitution.put(var, substitut);
                // Test partial homomorphism
                if (isHomomorphism(queryAtomRanked[rank], facts,
                        tmpSubstitution))
                    substitutionList.addAll(homomorphism(query, queryAtomRanked,
                            facts, tmpSubstitution, new LinkedList<Term>(
                                    orderedVars), rank + 1));
            }

        }
        return substitutionList;
    }

	/**
	 * 
	 * @param queryAtomRanked
	 * @param facts
	 * @param substitution
	 * @param orderedVars
	 * @param rank
	 * @return
	 * @throws Exception
	 */
	private static boolean existHomomorphism(AtomSet atomSet1,
			Collection<Atom>[] queryAtomRanked, AtomSet atomSet2,
			Substitution substitution, List<Term> orderedVars, int rank)
			throws Exception {
		if (orderedVars.size() == 0) {
			return true;
		} else {
			Term var;
			Set<Term> domaine = atomSet2.getTerms();

			var = orderedVars.remove(0);
			for (Term substitut : domaine) {
				Substitution tmpSubstitution = new HashMapSubstitution(
						substitution);
				tmpSubstitution.put(var, substitut);
				// Test partial homomorphism
				if (isHomomorphism(queryAtomRanked[rank], atomSet2,
						tmpSubstitution))
					if (existHomomorphism(atomSet1, queryAtomRanked, atomSet2,
							tmpSubstitution, new LinkedList<Term>(orderedVars),
							rank + 1)) {
						return true;
					}
			}

		}
		return false;
	}

    private static boolean isHomomorphism(Collection<Atom> atomsFrom,
            AtomSet atomsTo, Substitution substitution) throws Exception {
        for (Atom atom : atomsFrom) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("contains? " + substitution.createImageOf(atom));

            if (!atomsTo.contains(substitution.createImageOf(atom)))
                return false;
        }
        return true;
    }

    // TODO use an external comparator
    private static List<Term> order(Collection<Term> vars) {
        LinkedList<Term> orderedList = new LinkedList<Term>();
        for (Term var : vars)
            if (!orderedList.contains(var))
                orderedList.add(var);

        return orderedList;
    }

    /**
     * The index 0 contains the fully instantiated atoms.
     * 
     * @param atomset
     * @param varsOrdered
     * @return
     */
    private static Collection<Atom>[] getAtomRank(Iterable<Atom> atomset,
            List<Term> varsOrdered) {
        int tmp, rank;

        Collection<Atom>[] atomRank = new LinkedList[varsOrdered.size() + 1];
        for (int i = 0; i < atomRank.length; ++i)
            atomRank[i] = new LinkedList<Atom>();

        //
        for (Atom a : atomset) {
            rank = 0;
            for (Term t : a.getTerms()) {
                tmp = varsOrdered.indexOf(t) + 1;
                if (rank < tmp)
                    rank = tmp;
            }
            atomRank[rank].add(a);
        }

        return atomRank;
    }

}
