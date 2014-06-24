package fr.lirmm.graphik.graal.solver;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.stream.IteratorSubstitutionReader;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;

/**
 * Implementation of a backtrack solving algorithm.
 * 
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class RecursiveBacktrackSolver implements Solver {

    private static final Logger logger = LoggerFactory
            .getLogger(RecursiveBacktrackSolver.class);
    private ConjunctiveQuery query;
    private ReadOnlyAtomSet facts;

    // /////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // /////////////////////////////////////////////////////////////////////////

    public RecursiveBacktrackSolver(ConjunctiveQuery query, ReadOnlyAtomSet facts) {
        this.query = query;
        this.facts = facts;
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
    public SubstitutionReader execute() throws SolverException {
        List<Term> orderedVars = this.order(this.query.getAtomSet().getTerms(
                Term.Type.VARIABLE));
        Collection<Atom>[] queryAtomRanked = getAtomRank(
                this.query.getAtomSet(), orderedVars);
        try {
            if (isHomomorphisme(queryAtomRanked[0], this.facts,
                    new HashMapSubstitution())) {
                return new IteratorSubstitutionReader(this.homomorphisme(
                        queryAtomRanked, facts, new HashMapSubstitution(),
                        orderedVars, 1).iterator());
            } else {
                // return false
                return new IteratorSubstitutionReader(
                        new LinkedList<Substitution>().iterator());
            }
        } catch (Exception e) {
            throw new SolverException(e.getMessage(), e);
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
    private Collection<Substitution> homomorphisme(
            Collection<Atom>[] queryAtomRanked, ReadOnlyAtomSet facts,
            Substitution substitution, List<Term> orderedVars, int rank)
            throws Exception {
        Collection<Substitution> substitutionList = new LinkedList<Substitution>();
        if (orderedVars.size() == 0) {
            Substitution filteredSub = new HashMapSubstitution();
            for (Term var : this.query.getResponseVariables()) {
                filteredSub.put(var, substitution.getSubstitut(var));
            }
            substitutionList.add(filteredSub);
        } else {
            Term var;
            Set<Term> domaine = facts.getTerms();

            var = orderedVars.remove(0);
            for (Term substitut : domaine) {
                Substitution tmpSubstitution = new HashMapSubstitution(
                        substitution);
                tmpSubstitution.put(var, substitut);
                // Test partial homomorphisme
                if (this.isHomomorphisme(queryAtomRanked[rank], facts,
                        tmpSubstitution))
                    substitutionList.addAll(this.homomorphisme(queryAtomRanked,
                            facts, tmpSubstitution, new LinkedList<Term>(
                                    orderedVars), rank + 1));
            }

        }
        return substitutionList;
    }

    private boolean isHomomorphisme(Collection<Atom> atomsFrom,
            ReadOnlyAtomSet atomsTo, Substitution substitution) throws Exception {
        for (Atom atom : atomsFrom) {
            if (logger.isDebugEnabled())
                logger.debug("contains? " + substitution.getSubstitut(atom));

            if (!atomsTo.contains(substitution.getSubstitut(atom)))
                return false;
        }
        return true;
    }

    // TODO use an external comparator
    private List<Term> order(Collection<Term> vars) {
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
