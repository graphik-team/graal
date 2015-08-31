/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetUtils;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.util.Profilable;
import fr.lirmm.graphik.util.Profiler;

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
	
	protected LinkedList<QueryUnifier> getSinglePieceUnifiers(ConjunctiveQuery q,
			Rule r, RulesCompilation compilation) {
		if (atomic)
			if (!(compilation instanceof IDCompilation))
				return getSinglePieceUnifiersAHR(q, (AtomicHeadRule) r, compilation);
			else {
				if(LOGGER.isWarnEnabled()) {
					LOGGER.warn("IDCompilation is not compatible with atomic unification");
				}
				return getSinglePieceUnifiersNAHR(q, r, compilation);
			}
		else {
			return getSinglePieceUnifiersNAHR(q, r, compilation);
		}
	}

	/**
	 * Returns the list of all single-piece unifier between the given query and
	 * the given atomic-head rule cannot work with IDCompilation ( have to
	 * conserv the fact that an atom of the query can only been associated by a
	 * single unification with an atom of the head
	 * 
	 * @param query
	 *            the query that we want unify
	 * @param r
	 *            the atomic-head rule that we want unify
	 * @return the ArrayList of all single-piece unifier between the query of
	 *         the receiving object and R an atomic-head rule
	 * @throws Exception
	 */
	private LinkedList<QueryUnifier> getSinglePieceUnifiersAHR(
			ConjunctiveQuery q, AtomicHeadRule r, RulesCompilation compilation) {
		LinkedList<Atom> unifiableAtoms = getUnifiableAtoms(q, r, compilation);
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();

		Iterator<Atom> i = unifiableAtoms.iterator();
		while (i.hasNext()) {
			InMemoryAtomSet p = new LinkedListAtomSet();
			Rule tmpRule = Utils.getSafeCopy(r);
			AtomicHeadRule copy = new AtomicHeadRule(tmpRule.getBody(), tmpRule
					.getHead().iterator().next());
			Atom toUnif = i.next();
			p.add(toUnif);
			TermPartition partition = TermPartition.getPartitionByPosition(
					toUnif, copy.getHead().getAtom());
			// compute separating variable
			LinkedList<Term> sep = AtomSetUtils.sep(p, q.getAtomSet());
			// compute sticky variable
			LinkedList<Term> sticky = partition.getStickyVariable(sep, copy);
			AtomSet pBar = AtomSetUtils.minus(q.getAtomSet(), p);
			while (partition != null && !sticky.isEmpty()) {

				Iterator<Atom> ia = pBar.iterator();
				while (partition != null && ia.hasNext()) {

					Atom a = ia.next();
					Iterator<Term> ix = sticky.iterator();
					while (partition != null && ix.hasNext()) {

						Term x = ix.next();
						// all the atoms of Q/P which contain x must be add to P
						if (a.getTerms().contains(x)) {
							if (compilation.isUnifiable(a, copy.getHead().getAtom())) {
								p.add(a);
								TermPartition part = partition.join(TermPartition
										.getPartitionByPosition(a, copy
												.getHead().getAtom()));
								if (part.isAdmissible(copy)) {
									partition = part;
								} else
									partition = null;
							} else
								partition = null;
						}
					}
				}
				if (partition != null) {
					sep = AtomSetUtils.sep(p, q.getAtomSet());
					pBar = AtomSetUtils.minus(q.getAtomSet(), p);
					sticky = partition.getStickyVariable(sep, copy);
				}
			}
			i.remove();
			if (partition != null) {
				QueryUnifier u = new QueryUnifier(p, partition, copy, q);
				unifiers.add(u);
			}
		}

		return unifiers;
	}

	/**
	 * Returns the list of all single-piece unifier between the given query and
	 * the given rule
	 * 
	 * @param query
	 *            the query that we want unify
	 * @param r
	 *            the atomic-head rule that we want unify
	 * @return the ArrayList of all single-piece unifier between the query of
	 *         the receiving object and R an atomic-head rule
	 * @throws Exception
	 */
	private LinkedList<QueryUnifier> getSinglePieceUnifiersNAHR(
			ConjunctiveQuery q, Rule r, RulesCompilation compilation) {
		LinkedList<QueryUnifier> u = new LinkedList<QueryUnifier>();
		Rule ruleCopy = Utils.getSafeCopy(r);
		HashMap<Atom, LinkedList<TermPartition>> possibleUnification = new HashMap<Atom, LinkedList<TermPartition>>();
		// compute possible unification between atoms of Q and head(R)
		for (Atom a : q) {
			for (Atom b : ruleCopy.getHead()) {
				if (compilation.isUnifiable(a, b)) {
					Collection<? extends TermPartition> unification = compilation.getUnification(a, b);
					for(TermPartition partition : unification) {
						if(partition.isAdmissible(ruleCopy) ) {
							if(possibleUnification.get(a) == null)
								possibleUnification.put(a, new LinkedList<TermPartition>());
							possibleUnification.get(a).add(partition);
						}
					}
				}
			}
		}

		LinkedList<Atom> atoms = getUnifiableAtoms(q, r, compilation);
		for (Atom a : atoms) {
			LinkedList<TermPartition> partitionList = possibleUnification.get(a);
			if (partitionList != null) {
				Iterator<TermPartition> i = partitionList.iterator();
				while (i.hasNext()) {
					TermPartition unif = i.next();
					InMemoryAtomSet p = new LinkedListAtomSet();
					p.add(a);
					u.addAll(extend(p, unif, possibleUnification, q, ruleCopy));
					i.remove();
				}
			}
		}

		return u;
	}

	protected Collection<Rule> getUnifiableRules(Iterator<Predicate> preds,
			IndexedByHeadPredicatesRuleSet ruleSet, RulesCompilation compilation) {
		TreeSet<Rule> res = new TreeSet<Rule>(RuleOrder.getInstance());
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
	 * Returns the list of the atoms of the query that can be unify with the
	 * head of R
	 * 
	 * @param query
	 *            the query to unify
	 * @param r
	 *            the rule whose has the head to unify
	 * @return the list of the atoms of the query that have the same predicate
	 *         as the head atom of R
	 */
	protected LinkedList<Atom> getUnifiableAtoms(ConjunctiveQuery query, Rule r, RulesCompilation compilation) {
		LinkedList<Atom> answer = new LinkedList<Atom>();
		for (Atom a : query)
			for (Atom b : r.getHead())
				if (compilation.isUnifiable(a, b))
					answer.add(a);
		return answer;
	}

	/**
	 * Aggregated Mono piece unifier
	 * 
	 * @param q
	 * @param r
	 * @param compilation
	 * @return
	 */
	protected LinkedList<QueryUnifier> getSRUnifier(ConjunctiveQuery q, Rule r, RulesCompilation compilation) {
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();

		/** compute the simple unifiers **/
		unifiers = getSinglePieceUnifiers(q, r, compilation);

		/** compute the aggregated unifier by rule **/
		if (!unifiers.isEmpty())
			unifiers = getAggregatedUnifiers(unifiers);

		return unifiers;
	}

	/**
	 * Returns all the aggregated unifiers compute from the given unifiers
	 */
	@SuppressWarnings({ "unchecked" })
	protected LinkedList<QueryUnifier> getAggregatedUnifiers(
			LinkedList<QueryUnifier> unifToAggregate) {
		LinkedList<QueryUnifier> unifAggregated = new LinkedList<QueryUnifier>();
		LinkedList<QueryUnifier> restOfUnifToAggregate = (LinkedList<QueryUnifier>) unifToAggregate.clone();
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
	 * and others unifiers of lu. recursive function
	 * 
	 * @param u
	 *            the unifier whose we want aggregate with the unifiers of lu
	 * @param lu
	 *            list of unifiers
	 * @return the list of all aggregated unifier build from u and unifiers of
	 *         lu
	 * @throws Exception
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
	// 
	// /////////////////////////////////////////////////////////////////////////

	private Collection<? extends QueryUnifier> extend(InMemoryAtomSet p,
			TermPartition unif,
			HashMap<Atom, LinkedList<TermPartition>> possibleUnification,
			ConjunctiveQuery q, Rule r) {
		LinkedList<QueryUnifier> u = new LinkedList<QueryUnifier>();

		// compute separating variable
		LinkedList<Term> sep = AtomSetUtils.sep(p, q.getAtomSet());
		// compute sticky variable
		LinkedList<Term> sticky = unif.getStickyVariable(sep, r);
		if (sticky.isEmpty()) {
			u.add(new QueryUnifier(p, unif, r, q));
		} else {
			// compute Pext the atoms of Pbar linked to P by the sticky
			// variables
			InMemoryAtomSet pBar = AtomSetUtils.minus(q.getAtomSet(), p);
			InMemoryAtomSet pExt = new LinkedListAtomSet();
			for (Term t : sticky) {
				Iterator<Atom> ib = pBar.iterator();
				while (ib.hasNext()) {
					Atom b = ib.next();
					if (b.getTerms().contains(t)) {
						pExt.add(b);
						ib.remove();
					}
				}
			}
			TermPartition part;
			for (TermPartition uExt : preUnifier(pExt, r, possibleUnification)) {
				part = unif.join(uExt);
				if (part != null && part.isAdmissible(r)) {
					u.addAll(extend(AtomSetUtils.union(p, pExt), part,
							possibleUnification, q, r));
				}
			}

		}

		return u;
	}

	private LinkedList<TermPartition> preUnifier(InMemoryAtomSet p, Rule r,
			HashMap<Atom, LinkedList<TermPartition>> possibleUnification) {
		LinkedList<TermPartition> res = new LinkedList<TermPartition>();
		for (Atom a : p) {
			if (possibleUnification.get(a) != null)
				for (TermPartition ua : possibleUnification.get(a)) {
					InMemoryAtomSet fa = new LinkedListAtomSet();
					fa.add(a);
					InMemoryAtomSet aBar = null;
					aBar = AtomSetUtils.minus(p, fa);

					if (!aBar.iterator().hasNext())
						res.add(ua);
					else {
						TermPartition part;
						for (TermPartition u : preUnifier(aBar, r,
								possibleUnification)) {
							part = ua.join(u);
							if (part != null && part.isAdmissible(r)) {
								res.add(part);
							}
						}
					}
				}
			else
				return res;
		}
		return res;
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

		public static synchronized RuleOrder getInstance() {
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
