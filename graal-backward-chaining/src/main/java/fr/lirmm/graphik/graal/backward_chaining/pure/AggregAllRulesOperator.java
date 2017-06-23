/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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
 package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;
import fr.lirmm.graphik.graal.core.unifier.QueryUnifier;
import fr.lirmm.graphik.graal.core.unifier.UnifierUtils;

/**
 * Rewriting operator ARAM
 * rewriting engine that rewrite query using
 * aggregation all rule of most general single piece-unifiers, selecting
 * unifiers during the aggregation and using a marking system to avoid
 * redundant rewritings
 * 
 * @author Mélanie KÖNIG Query
 */
public class AggregAllRulesOperator extends BasicAggregAllRulesOperator {
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the rewrites compute from the given fact and the rule set of the
	 * receiving object.
	 * 
	 * @param q
	 *            A fact
	 * @return the ArrayList that contains the rewrites compute from the given
	 *         fact and the rule set of the receiving object.
	 */
	@Override
	public Collection<ConjunctiveQuery> getRewritesFrom(ConjunctiveQuery q, IndexedByHeadPredicatesRuleSet ruleSet, RulesCompilation compilation) {	
		LinkedList<ConjunctiveQuery> rewriteSet = new LinkedList<ConjunctiveQuery>();
		List<QueryUnifier> unifiers;
		for (Rule r : getUnifiableRules(q.getAtomSet().predicatesIterator(),
				ruleSet, compilation)) {
			unifiers = getSinglePieceUnifiers(q, r, compilation);
			for (QueryUnifier u : unifiers) {
				rewriteSet.add(Utils.rewriteWithMark(q, u));
			}
		}

		return rewriteSet;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	protected List<QueryUnifier> getSRUnifier(ConjunctiveQuery q, Rule r, RulesCompilation compilation) {
		List<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();
		List<QueryUnifier> simpleUnifiers = new LinkedList<QueryUnifier>();
		/** compute the simple unifiers **/
		simpleUnifiers = getSinglePieceUnifiers(q, r, compilation);
		if (!simpleUnifiers.isEmpty()) {
			if (q instanceof MarkedQuery) {
				MarkedQuery copy = (MarkedQuery) new MarkedQuery(
						(MarkedQuery) q);
				copy.markAll();
				simpleUnifiers = getSinglePieceUnifiers(copy, r, compilation);
			}
		}
		/** compute the aggregated unifier by rule **/
		unifiers.addAll(getAggregatedUnifiers(simpleUnifiers));

		return unifiers;
	}
	
	/**
	 * Returns the list of the atoms of the query that can be unify with the
	 * head of R
	 * 
	 * @param query
	 *            the query to unify
	 * @param rule
	 *            the rule whose has the head to unify
	 * @return the list of the atoms of the query that have the same predicate
	 *         as the head atom of R and that are recently created in query
	 */
	public LinkedList<Atom> getUnifiableAtoms(MarkedQuery query, Rule rule, RulesCompilation compilation) {
		LinkedList<Atom> atoms = UnifierUtils.getUnifiableAtoms((ConjunctiveQuery)query, rule, compilation);
		LinkedList<Atom> res = new LinkedList<Atom>();
		
		// keep only the recently created so marked in query
		for (Atom a : atoms) {
			if (query.isMarked(a)) {
				res.add(a);
			}
		}
		
		return res;
	}
	

}
