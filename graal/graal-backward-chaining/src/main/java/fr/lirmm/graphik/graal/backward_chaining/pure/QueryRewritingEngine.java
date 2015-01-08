package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.AtomicHeadRule;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.Misc;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.QueryUnifier;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.TermPartition;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.AtomSets;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;
import fr.lirmm.graphik.util.Profilable;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.Verbosable;

/**
 * @author Mélanie KÖNIG Query Rewriting Engine that rewrites query using only
 *         most general single-piece unifiers not prunable
 */
public class QueryRewritingEngine implements Verbosable, Profilable {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(QueryRewritingEngine.class);

	private boolean verbose = false;
	private Profiler profiler;

	private PureQuery query;
	private IndexedByHeadPredicatesRuleSet ruleSet;

	protected RulesCompilation compilation;

	protected LinkedList<ConjunctiveQuery> pivotRewritingSet;


	// attributs temporaires
	public boolean atomic = false;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public QueryRewritingEngine(PureQuery query, Iterable<Rule> rules,
			RulesCompilation comp) {
		this.query = query;
		this.ruleSet = new IndexedByHeadPredicatesRuleSet(rules);
		this.compilation = comp;
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS
	// /////////////////////////////////////////////////////////////////////////

	public ConjunctiveQuery getQuery() {
		return query;
	}

	public IndexedByHeadPredicatesRuleSet getRuleSet() {
		return this.ruleSet;
	}

	public RulesCompilation getRulesCompilation() {
		return this.compilation;
	}

	public LinkedList<ConjunctiveQuery> getPivotRewritingSet() {
		return pivotRewritingSet;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Compute and returns all the most general rewrites of the object's query
	 * 
	 * @return a list of the most general rewrite computed from the query and
	 *         set of rule stored in the object
	 * @throws Exception
	 */
	public Collection<ConjunctiveQuery> computeRewritings() {
		this.pivotRewritingSet = new LinkedList<ConjunctiveQuery>();
		int exploredRewrites = 0;
		int generatedRewrites = 0;

		if(this.verbose) {
			this.profiler.start("rewriting time");
		}

		this.query = new PureQuery(compilation.getIrredondant(query.getAtomSet()), query.getAnswerVariables());
		Collection<ConjunctiveQuery> currentRewriteSet;
		Queue<ConjunctiveQuery> rewriteSetToExplore = new LinkedList<ConjunctiveQuery>();

		this.query.addAnswerPredicate();
		rewriteSetToExplore.add(this.query);
		pivotRewritingSet.add(this.query);

		ConjunctiveQuery q;
		while (!rewriteSetToExplore.isEmpty()) {

			/* take the first query to rewrite */
			q = rewriteSetToExplore.poll();
			++exploredRewrites; // stats

			/* compute all the rewrite from it */
			currentRewriteSet = this.getRewritesFrom(q);
			generatedRewrites += currentRewriteSet.size(); // stats

			/* keep only the most general among query just computed */
			Misc.computeCover(currentRewriteSet, this.compilation);

			/*
			 * keep only the query just computed that are more general than
			 * query already compute
			 */
			selectMostGeneralFromRelativeTo(currentRewriteSet,
					pivotRewritingSet);

			/* keep to explore only most general query */
			selectMostGeneralFromRelativeTo(rewriteSetToExplore,
					currentRewriteSet);

			/*
			 * keep in final rewrite set only query more general than query just
			 * computed
			 */
			selectMostGeneralFromRelativeTo(pivotRewritingSet,
					currentRewriteSet);

			// add to explore the query just computed that we keep
			rewriteSetToExplore.addAll(currentRewriteSet);

			// add in final rewrite set the query just compute that we keep
			pivotRewritingSet.addAll(currentRewriteSet);

		}

		if(this.verbose) {
			this.profiler.add("Generated rewritings", generatedRewrites);
			this.profiler.add("Explored rewritings", exploredRewrites);
			this.profiler.add("Pivots rewritings", pivotRewritingSet.size());
			this.profiler.stop("rewriting time");
		}

		return pivotRewritingSet;
	}

	public static int estimateNbDevelopedRew(Collection<ConjunctiveQuery> pivotRewritingSet, RulesCompilation compilation) {
		int res = 0;
		int nb;
		for (ConjunctiveQuery query : pivotRewritingSet) {
			nb = 1;
			for (Atom a : query) {
				nb = nb * compilation.getRewritingOf(a).size();
			}
			res += nb;
		}
		return res;
	}

	/**
	 * Remove from toSelect the Fact that are not more general than all the fact
	 * of relativeTo
	 * 
	 * @param toSelect
	 * @param rewritingSet
	 */
	public void selectMostGeneralFromRelativeTo(
			Collection<ConjunctiveQuery> toSelect,
			Collection<ConjunctiveQuery> rewritingSet) {
		Iterator<? extends ConjunctiveQuery> i = toSelect.iterator();
		while (i.hasNext()) {
			AtomSet f = i.next().getAtomSet();
			if (containMoreGeneral(f, rewritingSet))
				i.remove();
		}
	}

	/**
	 * Returns true if rewriteSet contains a fact more general than f, else
	 * returns false
	 * 
	 * @param f
	 * @param rewriteSet
	 * @param comp
	 * @return
	 */
	public boolean containMoreGeneral(AtomSet f,
			Collection<ConjunctiveQuery> rewriteSet) {
		for(ConjunctiveQuery q : rewriteSet) {
			AtomSet a = q.getAtomSet();
			if (Misc.isMoreGeneralThan(a, f, this.compilation))
				return true;
		}
		return false;
	}

	/**
	 * Returns the rewrites compute from the given fact and the rule set of the
	 * receiving object.
	 * 
	 * @param q
	 *            A fact
	 * @return the ArrayList that contains the rewrites compute from the given
	 *         fact and the rule set of the receiving object.
	 * @throws Exception
	 */
	protected Collection<ConjunctiveQuery> getRewritesFrom(ConjunctiveQuery q) {
		LinkedList<ConjunctiveQuery> rewriteSet = new LinkedList<ConjunctiveQuery>();
		LinkedList<QueryUnifier> unifiers;
		try {
			for (Rule r : getUnifiableRules(q.getAtomSet().getAllPredicates(),
					ruleSet, compilation)) {
				unifiers = getSinglePieceUnifiers(q, r);
				for (QueryUnifier u : unifiers) {
					rewriteSet.add(rewrite(q, u));
				}
			}
		} catch (AtomSetException e) {
		}
		return rewriteSet;
	}

	/**
	 * Rewrite the fact q according to the unifier u between the head of r and q
	 * 
	 * @param q
	 *            the fact to rewrite
	 * @param r
	 *            the rule which is unified with q
	 * @param u
	 *            the unifier between q and r
	 * @return the rewrite of q according to the unifier u between the head of r
	 *         and q
	 */
	public ConjunctiveQuery rewrite(ConjunctiveQuery q, QueryUnifier u) {
		AtomSet ajout = u.getImageOf(u.getRule().getBody());
		AtomSet restant = u.getImageOf(AtomSets.minus(q.getAtomSet(),
				u.getPiece()));
		if(ajout != null && restant != null) { // FIXME
			AtomSet res = AtomSets.union(ajout, restant);
			ArrayList<Term> ansVar = new ArrayList<Term>();
			ansVar.addAll(q.getAnswerVariables());
			return new DefaultConjunctiveQuery(res, ansVar);
		}
		return null;
	}

	public LinkedList<QueryUnifier> getSinglePieceUnifiers(ConjunctiveQuery q,
			Rule r) {
		if (atomic)
			if (!(compilation instanceof IDCompilation))
				return getSinglePieceUnifiersAHR(q, (AtomicHeadRule) r);
			else {
				if(LOGGER.isWarnEnabled()) {
					LOGGER.warn("IDCompilation is not compatible with atomic unification");
				}
				return getSinglePieceUnifiersNAHR(q, r);
			}
		else {
			return getSinglePieceUnifiersNAHR(q, r);
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
	public LinkedList<QueryUnifier> getSinglePieceUnifiersAHR(
			ConjunctiveQuery q, AtomicHeadRule r) {
		LinkedList<Atom> unifiableAtoms = getUnifiableAtoms(q, r);
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();

		Iterator<Atom> i = unifiableAtoms.iterator();
		while (i.hasNext()) {
			AtomSet p = new LinkedListAtomSet();
			Rule tmpRule = Misc.getSafeCopy(r);
			AtomicHeadRule copy = new AtomicHeadRule(tmpRule.getBody(), tmpRule
					.getHead().iterator().next());
			Atom toUnif = i.next();
			p.add(toUnif);
			TermPartition partition = TermPartition.getPartitionByPosition(
					toUnif, copy.getHead().getAtom());
			// compute separating variable
			LinkedList<Term> sep = AtomSets.sep(p, q.getAtomSet());
			// compute sticky variable
			LinkedList<Term> sticky = partition.getStickyVariable(sep, copy);
			AtomSet pBar = AtomSets.minus(q.getAtomSet(), p);
			while (partition != null && !sticky.isEmpty()) {

				Iterator<Atom> ia = pBar.iterator();
				while (partition != null && ia.hasNext()) {

					Atom a = ia.next();
					Iterator<Term> ix = sticky.iterator();
					while (partition != null && ix.hasNext()) {

						Term x = ix.next();
						// all the atoms of Q/P which contain x must be add to P
						if (a.getTerms().contains(x)) {
							if (this.compilation.isUnifiable(a, copy.getHead().getAtom())) {
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
					sep = AtomSets.sep(p, q.getAtomSet());
					pBar = AtomSets.minus(q.getAtomSet(), p);
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
	public LinkedList<QueryUnifier> getSinglePieceUnifiersNAHR(
			ConjunctiveQuery q, Rule r) {
		LinkedList<QueryUnifier> u = new LinkedList<QueryUnifier>();
		Rule ruleCopy = Misc.getSafeCopy(r);
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

		LinkedList<Atom> atoms = getUnifiableAtoms(q, r);
		for (Atom a : atoms) {
			LinkedList<TermPartition> partitionList = possibleUnification.get(a);
			if (partitionList != null) {
				Iterator<TermPartition> i = partitionList.iterator();
				while (i.hasNext()) {
					TermPartition unif = i.next();
					AtomSet p = new LinkedListAtomSet();
					p.add(a);
					u.addAll(extend(p, unif, possibleUnification, q, ruleCopy));
					i.remove();
				}
			}
		}

		return u;
	}

	private Collection<? extends QueryUnifier> extend(AtomSet p,
			TermPartition unif,
			HashMap<Atom, LinkedList<TermPartition>> possibleUnification,
			ConjunctiveQuery q, Rule r) {
		LinkedList<QueryUnifier> u = new LinkedList<QueryUnifier>();

		// compute separating variable
		LinkedList<Term> sep = AtomSets.sep(p, q.getAtomSet());
		// compute sticky variable
		LinkedList<Term> sticky = unif.getStickyVariable(sep, r);
		if (sticky.isEmpty()) {
			u.add(new QueryUnifier(p, unif, r, q));
		} else {
			// compute Pext the atoms of Pbar linked to P by the sticky
			// variables
			AtomSet pBar = AtomSets.minus(q.getAtomSet(), p);
			AtomSet pExt = new LinkedListAtomSet();
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
					u.addAll(extend(AtomSets.union(p, pExt), part,
							possibleUnification, q, r));
				}
			}

		}

		return u;
	}

	private LinkedList<TermPartition> preUnifier(AtomSet p, Rule r,
			HashMap<Atom, LinkedList<TermPartition>> possibleUnification) {
		LinkedList<TermPartition> res = new LinkedList<TermPartition>();
		for (Atom a : p) {
			if (possibleUnification.get(a) != null)
				for (TermPartition ua : possibleUnification.get(a)) {
					AtomSet fa = new LinkedListAtomSet();
					fa.add(a);
					AtomSet aBar = null;
					aBar = AtomSets.minus(p, fa);

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
	protected LinkedList<Atom> getUnifiableAtoms(ConjunctiveQuery query, Rule r) {
		LinkedList<Atom> answer = new LinkedList<Atom>();
		// ArrayList<Predicate> predicate = new ArrayList<Predicate>();
		// for(Atom h : R.getHead().getAtoms())
		// for(Predicate p :
		// predicateOrder.getHighterPredicate(h.getPredicate()))
		// if(!predicate.contains(p))
		// predicate.add(p);
		//
		// for(Predicate p : predicate)
		// answer.addAll(query.getAtomsByPredicate(p));
		for (Atom a : query)
			for (Atom b : r.getHead())
				if (this.compilation.isUnifiable(a, b))
					answer.add(a);
		return answer;
	}

	public static Collection<Rule> getUnifiableRules(Iterable<Predicate> preds,
			IndexedByHeadPredicatesRuleSet ruleSet, RulesCompilation compilation) {
		TreeSet<Rule> res = new TreeSet<Rule>(RuleOrder.getInstance());
		TreeSet<Predicate> unifiablePreds = new TreeSet<Predicate>();
		for (Predicate pred : preds) {
			unifiablePreds.addAll(compilation.getUnifiablePredicate(pred));
		}
		for (Predicate pred : unifiablePreds) {
			for (Rule r : ruleSet.getRulesByHeadPredicate(pred)) {
				res.add(r);
			}
		}

		return res;
	}

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}

	@Override
	public void enableVerbose(boolean enable) {
		this.verbose = enable;
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
