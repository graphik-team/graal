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
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;
import fr.lirmm.graphik.graal.core.unifier.AtomicHeadRule;
import fr.lirmm.graphik.graal.core.unifier.QueryUnifier;
import fr.lirmm.graphik.graal.core.unifier.UnifierUtils;
import fr.lirmm.graphik.util.profiler.Profilable;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Mélanie KÖNIG Query Rewriting Engine that rewrites query using only
 *         most general single-piece unifiers not prunable
 */
public abstract class AbstractRewritingOperator implements RewritingOperator, Profilable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AbstractRewritingOperator.class);

	private Profiler profiler;

	// attributs temporaires
	public boolean atomic = false;


	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE AND PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	protected List<QueryUnifier> getSinglePieceUnifiers(ConjunctiveQuery q,
			Rule r, RulesCompilation compilation) {
		if (atomic)
			if (!(compilation instanceof IDCompilation))
				return UnifierUtils.getSinglePieceUnifiersAHR(q, (AtomicHeadRule) r, compilation);
			else {
				if(LOGGER.isWarnEnabled()) {
					LOGGER.warn("IDCompilation is not compatible with atomic unification");
				}
				return UnifierUtils.getSinglePieceUnifiersNAHR(q, r, compilation);
			}
		else {
			return UnifierUtils.getSinglePieceUnifiersNAHR(q, r, compilation);
		}
	}



	protected Collection<Rule> getUnifiableRules(CloseableIteratorWithoutException<Predicate> preds,
			IndexedByHeadPredicatesRuleSet ruleSet, RulesCompilation compilation) {
		TreeSet<Rule> res = new TreeSet<Rule>(RuleOrder.instance());
		TreeSet<Predicate> unifiablePreds = new TreeSet<Predicate>();
		
		while (preds.hasNext()) {
			unifiablePreds.addAll(compilation.getUnifiablePredicate(preds.next()));
		}
		for (Predicate pred : unifiablePreds) {
			for (Rule r : ruleSet.getRulesByHeadPredicate(pred)) {
				res.add(r);
			}
		}

		return res;
	}

	/**
	 * Aggregated Mono piece unifier
	 * 
	 * @param q
	 * @param r
	 * @param compilation
	 * @return a List of unifiers between q and the head of r.
	 */
	protected List<QueryUnifier> getSRUnifier(ConjunctiveQuery q, Rule r, RulesCompilation compilation) {

		/** compute the simple unifiers **/
		List<QueryUnifier> unifiers =  getSinglePieceUnifiers(q, r, compilation);

		/** compute the aggregated unifier by rule **/
		if (!unifiers.isEmpty())
			unifiers = getAggregatedUnifiers(unifiers);

		return unifiers;
	}

	/**
	 * Returns all the aggregated unifiers compute from the given unifiers
	 */
	protected LinkedList<QueryUnifier> getAggregatedUnifiers(
			List<QueryUnifier> unifToAggregate) {
		LinkedList<QueryUnifier> unifAggregated = new LinkedList<QueryUnifier>();
		LinkedList<QueryUnifier> restOfUnifToAggregate = new LinkedList<QueryUnifier>(unifToAggregate);
		Iterator<QueryUnifier> itr = unifToAggregate.iterator();
		QueryUnifier u;
		while (itr.hasNext()) {
			u = itr.next();
			restOfUnifToAggregate.remove(u);

			// add to the result all the aggregated piece-unifier build from u
			// and the rest of the mgu single piece-unifiers
			unifAggregated.addAll(aggregate(u, restOfUnifToAggregate));
		}
		return unifAggregated;
	}
	
	/**
	 * Returns the list of all the aggregated unifiers that can be build from u
	 * and others unifiers of l. recursive function
	 * 
	 * @param u
	 *            the unifier whose we want aggregate with the unifiers of l
	 * @param l
	 *            list of unifiers
	 * @return the list of all aggregated unifier build from u and unifiers of
	 *         l
	 */
	@SuppressWarnings({ "unchecked" })
	private LinkedList<QueryUnifier> aggregate(QueryUnifier u,
			LinkedList<QueryUnifier> l) {
		LinkedList<QueryUnifier> lu = (LinkedList<QueryUnifier>) l.clone();
		// if there is no more unifier to aggregate
		if (lu.isEmpty()) {
			LinkedList<QueryUnifier> res = new LinkedList<QueryUnifier>();
			res.add(u);
			return res;
		} else {
			QueryUnifier first = lu.getFirst(); // take the first one
			lu.removeFirst(); // remove first one from lu
			// if first can be aggregated with u
			LinkedList<QueryUnifier> res = aggregate(u, lu);
			if (u.isCompatible(first)) {
				// System.out.println("oui");
				// compute the others aggregation from the aggregation of u and
				// first and the rest of lu
				res.addAll(aggregate(u.aggregate(first), lu));
				// concatenate this result and the others aggregations from u
				// and the rest of lu

			}
			return res;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
	// /////////////////////////////////////////////////////////////////////////
	
	private static class RuleOrder implements Comparator<Rule> {
		
		private static RuleOrder instance;

		private RuleOrder() {
		}

		public static synchronized RuleOrder instance() {
			if (instance == null)
				instance = new RuleOrder();

			return instance;
		}
		
		@Override
		public int compare(Rule o1, Rule o2) {
			return o1.toString().compareTo(o2.toString());
		}
	}
}
