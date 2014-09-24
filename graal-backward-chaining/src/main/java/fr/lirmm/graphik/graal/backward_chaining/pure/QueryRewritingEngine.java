package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.AtomicHeadRule;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.IDCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.PredicateOrder;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.Homomorphism;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.Misc;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.QueryUnifier;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.RewritingSet;
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
import fr.lirmm.graphik.graal.core.ruleset.RuleSet;

/**
 * @author Mélanie KÖNIG Query Rewriting Engine that rewrites query using only
 *         most general single-piece unifiers not prunable
 */
public class QueryRewritingEngine {

	private PureQuery query;
	private IndexedByHeadPredicatesRuleSet ruleSet;
	private RuleSet compiledRules;

	protected RulesCompilation compilation;

	protected LinkedList<ConjunctiveQuery> unfoldingRewritingSet;
	protected LinkedList<ConjunctiveQuery> pivotRewritingSet;

	// attributs temporaires
	public boolean testInclu = true;
	public boolean atomic = false;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public QueryRewritingEngine(PureQuery query) {
		this.query = query;
		ruleSet = new IndexedByHeadPredicatesRuleSet();
		compilation = new PredicateOrder();
	}

	public QueryRewritingEngine(PureQuery query, Iterable<Rule> rules) {
		this.query = query;
		this.ruleSet = new IndexedByHeadPredicatesRuleSet(rules);
	}

	public QueryRewritingEngine(PureQuery query, Iterable<Rule> rules,
			RulesCompilation comp) {
		this.query = query;
		this.compilation = comp;
		this.ruleSet = new IndexedByHeadPredicatesRuleSet(rules);
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

	public LinkedList<ConjunctiveQuery> getUnfoldingRewritingSet() {
		return unfoldingRewritingSet;
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
	public Collection<ConjunctiveQuery> computeRewritings() throws Exception {
		int exploredRewrites = 0;
		int generatedRewrites = 0;

		initialiseQRE();

		// TODO
		// RewritingSet currentRewriteSet = new RewritingSet(); // the rewrites
		// that we just computed
		LinkedList<ConjunctiveQuery> currentRewriteSet = new LinkedList<ConjunctiveQuery>();

		// the rewrites that are the most general at the moment and that we have
		// not yet used to generated another rewrites
		LinkedList<ConjunctiveQuery> rewriteSetToExplore = new LinkedList<ConjunctiveQuery>();
		Collection<ConjunctiveQuery> rewriteToAdd;

		this.query.addAnswerPredicate();
		ConjunctiveQuery q = this.query;
		
		rewriteSetToExplore.add(q);
		pivotRewritingSet.add(q);

		while (!rewriteSetToExplore.isEmpty()) {

			currentRewriteSet.clear();

			/** take the first query to rewrite **/
			q = rewriteSetToExplore.removeFirst();
			exploredRewrites++;
			/** compute all the rewrite from it **/

			rewriteToAdd = getRewritesFrom(q);
			generatedRewrites = generatedRewrites + rewriteToAdd.size();
			currentRewriteSet.addAll(rewriteToAdd);

			// Start homomorphism
			/** keep only the most general among query just computed **/
			computeCover(currentRewriteSet, this.compilation);

			/**
			 * keep only the query just computed that are more general than
			 * query already compute
			 **/
			selectMostGeneralFromRelativeTo(currentRewriteSet,
					pivotRewritingSet, compilation);

			/** keep to explore only most general query **/
			selectMostGeneralFromRelativeTo(rewriteSetToExplore,
					currentRewriteSet, compilation);

			/** add to explore the query just computed that we keep **/
			addAll(rewriteSetToExplore, currentRewriteSet);

			/**
			 * keep in final rewrite set only query more general than query just
			 * computed
			 **/
			selectMostGeneralFromRelativeTo(pivotRewritingSet,
					currentRewriteSet, compilation);

			/** add in final rewrite set the query just compute that we keep **/
			pivotRewritingSet.addAll(currentRewriteSet);

			// Stop homomorphism

		}
		
		// remove ans predicate from queries
		for(ConjunctiveQuery query : pivotRewritingSet) {
			PureQuery.removeAnswerPredicate(query);
		}

		if (compilation != null)
			develop();

		return unfoldingRewritingSet;
	}

	public Collection<ConjunctiveQuery> develop() throws Exception {

		int estim_nb = estimateNbDevelopedRew();

		developpRewriting(pivotRewritingSet);

		/** clean the rewrites to return **/
		removeUselessRewrites(unfoldingRewritingSet);

		computeCover(unfoldingRewritingSet, null);

		removeAnswerPredicate(unfoldingRewritingSet);
		return unfoldingRewritingSet;
	}

	public int estimateNbDevelopedRew() {
		int res = 0;
		int nb;
		for (ConjunctiveQuery query : unfoldingRewritingSet) {
			nb = 1;
			for (Atom a : query) {
				nb = nb * compilation.getRewritingOf(a).size();
			}
			res += nb;
		}
		return res;
	}

	/**
	 * Add in the given rewriting set the rewrites that can be entailed from the
	 * predicate order ex: the rewrite A(x) can be entailed from the rewrite
	 * B(x) and the predicate order A > B
	 */
	private void developpRewriting(Collection<ConjunctiveQuery> rewritingSet) {
		if (compilation != null) {
			LinkedList<ConjunctiveQuery> newq = new LinkedList<ConjunctiveQuery>();
			LinkedList<ConjunctiveQuery> sauv = new LinkedList<ConjunctiveQuery>();
			ConjunctiveQuery cop;
			ConjunctiveQuery copy;
			// ConjunctiveQuery q;
			LinkedList<Collection<Atom>> atoms = new LinkedList<Collection<Atom>>();
			for (ConjunctiveQuery q : rewritingSet) {
				// q = query.getIrredondant(compilation);
				// for all atom of the query we will build a list of all the
				// rewriting
				atoms.clear();
				for (Atom a : q) {
					atoms.add(compilation.getRewritingOf(a));
				}

				newq.clear();
				ConjunctiveQuery query = new DefaultConjunctiveQuery();
				query.getAnswerVariables().addAll(q.getAnswerVariables());
				newq.add(query);
				
				// we will build all the possible fact from the rewriting of the
				// atoms
				Iterator<ConjunctiveQuery> i;
				for (Collection<Atom> pos : atoms) {
					sauv.clear(); // the set of query build from cop by adding
									// an atom of the next position
					i = newq.iterator();
					while (i.hasNext()) {
						cop = i.next();
						i.remove();
						for (Atom atom : pos) {// for each possible atom at the
												// next position clone the query
												// and add the atom
							copy = new DefaultConjunctiveQuery(cop);
							copy.getAtomSet().add(atom);
							sauv.add(copy);
						}
					}
					newq.addAll(sauv);
				}
				unfoldingRewritingSet.addAll(newq);
			}

		}
	}

	protected void initialiseQRE() {
		unfoldingRewritingSet = new LinkedList<ConjunctiveQuery>();
		pivotRewritingSet = new LinkedList<ConjunctiveQuery>();
		query = query.getIrredondant(compilation);
	}

	/**
	 * Returns true if AtomSet h is more general than AtomSet f, and mark all
	 * the atom of h if h is a marked fact; else return false
	 * 
	 * @param comp
	 */
	public boolean isMoreGeneral(AtomSet h, AtomSet f, RulesCompilation comp) {

		boolean moreGen;
		if (testInclu && h.isSubSetOf(f)) {
			moreGen = true;
		} else if (new Homomorphism(h, f, comp).existHomomorphism()) {
			moreGen = true;
		} else {
			moreGen = false;
		}

		return moreGen;
	}

	/**
	 * Remove the fact that are not the most general (taking account of compiled
	 * rules) in the given facts
	 * 
	 * @param comp
	 */
	public void computeCover(RewritingSet set, RulesCompilation comp) {
		Iterator<ConjunctiveQuery> i = set.iterator();
		boolean foundMoreGen;
		ConjunctiveQuery q;
		AtomSet o;
		Collection<ConjunctiveQuery> comparable;
		Iterator<ConjunctiveQuery> j;
		while (i.hasNext()) {
			q = i.next();
			foundMoreGen = false;
			comparable = set.getComparableQueries(q, comp);
			j = comparable.iterator();
			while (!foundMoreGen && j.hasNext()) {
				o = j.next().getAtomSet();
				if (q != o && isMoreGeneral(o, q.getAtomSet(), comp)) {
					foundMoreGen = true;
					i.remove();
				}
			}
		}
	}

	/**
	 * Remove the fact that are not the most general (taking account of compiled
	 * rules) in the given facts
	 * 
	 * @param comp
	 * @throws Exception
	 */
	public void computeCover(LinkedList<ConjunctiveQuery> set,
			RulesCompilation comp) throws Exception {

		Iterator<ConjunctiveQuery> beg = set.iterator();
		Iterator<ConjunctiveQuery> end;
		AtomSet q;
		AtomSet o;
		boolean finished;
		while (beg.hasNext()) {
			q = beg.next().getAtomSet();
			finished = false;
			end = set.iterator();
			while (!finished && end.hasNext()) {
				o = end.next().getAtomSet();
				if (o != q && isMoreGeneral(o, q, comp)) {
					finished = true;
					beg.remove();
				}
			}
		}
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
			Collection<ConjunctiveQuery> rewritingSet, RulesCompilation comp) {
		Iterator<? extends ConjunctiveQuery> i = toSelect.iterator();
		while (i.hasNext()) {
			AtomSet f = i.next().getAtomSet();
			if (containMoreGeneral(f, rewritingSet, comp))
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
			Collection<ConjunctiveQuery> rewriteSet, RulesCompilation comp) {
		boolean foundMoreGen = false;
		Iterator<? extends ConjunctiveQuery> i = rewriteSet.iterator();
		while (i.hasNext() && !foundMoreGen) {
			AtomSet q = i.next().getAtomSet();
			if (isMoreGeneral(q, f, comp))
				foundMoreGen = true;
		}
		return foundMoreGen;
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
	 * Remove the useless rewrites ( containting temporary predicates like
	 * "aux_" ) of the given lists
	 * 
	 * @param rewriteSet
	 *            the list of rewrite that we want clean
	 * @return the list of rewrite without the rewrite containing temporary
	 *         predicates
	 */
	protected void removeUselessRewrites(Collection<ConjunctiveQuery> rewriteSet) {
		Iterator<? extends ConjunctiveQuery> i = rewriteSet.iterator();
		while (i.hasNext()) {
			ConjunctiveQuery q = i.next();
			// if contains auxiliary predicate that can be match in the facts
			if (containAuxiliaryPredicate(q))
				i.remove();
		}
	}

	/**
	 * Remove the answer predicate Ans_ of queries in the given list
	 * 
	 * @param rewriteSet
	 *            the list of rewrite that we want clean
	 * @return the list of rewrite without the rewrite containing temporary
	 *         predicates
	 */
	protected void removeAnswerPredicate(Collection<ConjunctiveQuery> rewriteSet) {
		for (ConjunctiveQuery q : rewriteSet) {
			removeAnswerPredicate(q);
		}
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
		AtomSet res = AtomSets.union(ajout, restant);

		ArrayList<Term> ansVar = new ArrayList<Term>();
		ansVar.addAll(q.getAnswerVariables());
		return new DefaultConjunctiveQuery(res, ansVar);
	}

	public LinkedList<QueryUnifier> getSinglePieceUnifiers(ConjunctiveQuery Q,
			Rule R) {
		if (atomic)
			if (!(compilation instanceof IDCompilation))
				return getSinglePieceUnifiersAHR(Q, (AtomicHeadRule) R);
			else {
				System.err
						.println("IDCompilation is not compatible with atomic unification");
				return getSinglePieceUnifiersNAHR(Q, R);
			}
		else {
			return getSinglePieceUnifiersNAHR(Q, R);
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
			ConjunctiveQuery Q, AtomicHeadRule R) {
		LinkedList<Atom> UnifiableAtoms = getUnifiableAtoms(Q, R);
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();

		Iterator<Atom> i = UnifiableAtoms.iterator();
		while (i.hasNext()) {
			AtomSet P = new LinkedListAtomSet();
			Rule tmpRule = Misc.getSafeCopy(R);
			AtomicHeadRule copy = new AtomicHeadRule(tmpRule.getBody(), tmpRule
					.getHead().iterator().next());
			Atom toUnif = i.next();
			P.add(toUnif);
			TermPartition partition = TermPartition.getPartitionByPosition(
					toUnif, copy.getHead().getAtom());
			// compute separating variable
			LinkedList<Term> sep = AtomSets.sep(P, Q.getAtomSet());
			// compute sticky variable
			LinkedList<Term> sticky = partition.getStickyVariable(sep, copy);
			AtomSet Pbar = AtomSets.minus(Q.getAtomSet(), P);
			while (partition != null && !sticky.isEmpty()) {

				Iterator<Atom> ia = Pbar.iterator();
				while (partition != null && ia.hasNext()) {

					Atom a = ia.next();
					Iterator<Term> ix = sticky.iterator();
					while (partition != null && ix.hasNext()) {

						Term x = ix.next();
						// all the atoms of Q/P which contain x must be add to P
						if (a.getTerms().contains(x)) {
							if (isUnifiable(a, copy.getHead().getAtom())) {
								P.add(a);
								TermPartition p = partition.join(TermPartition
										.getPartitionByPosition(a, copy
												.getHead().getAtom()));
								if (p.isAdmissible(copy)) {
									partition = p;
								} else
									partition = null;
							} else
								partition = null;
						}
					}
				}
				if (partition != null) {
					sep = AtomSets.sep(P, Q.getAtomSet());
					Pbar = AtomSets.minus(Q.getAtomSet(), P);
					sticky = partition.getStickyVariable(sep, copy);
				}
			}
			i.remove();
			if (partition != null) {
				QueryUnifier u = new QueryUnifier(P, partition, copy, Q);
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
			ConjunctiveQuery Q, Rule R) {
		LinkedList<QueryUnifier> u = new LinkedList<QueryUnifier>();
		Rule copy = Misc.getSafeCopy(R);
		HashMap<Atom, LinkedList<TermPartition>> possibleUnification = new HashMap<Atom, LinkedList<TermPartition>>();
		// compute possible unification between atoms of Q and head(R)
		for (Atom a : Q) {
			for (Atom b : copy.getHead()) {
				if (isUnifiable(a, b)) {
					if (possibleUnification.get(a) == null)
						possibleUnification.put(a,
								new LinkedList<TermPartition>());
					possibleUnification.get(a).addAll(getUnification(a, b));
				}
			}
		}

		LinkedList<Atom> atoms = getUnifiableAtoms(Q, R);
		for (Atom a : atoms) {
			Iterator<TermPartition> i = possibleUnification.get(a).iterator();
			while (i.hasNext()) {
				TermPartition unif = i.next();
				AtomSet p = new LinkedListAtomSet();
				p.add(a);
				u.addAll(extend(p, unif, possibleUnification, Q, copy));
				i.remove();
			}
		}

		return u;
	}

	private Collection<? extends QueryUnifier> extend(AtomSet P,
			TermPartition unif,
			HashMap<Atom, LinkedList<TermPartition>> possibleUnification,
			ConjunctiveQuery Q, Rule R) {
		LinkedList<QueryUnifier> u = new LinkedList<QueryUnifier>();

		// compute separating variable
		LinkedList<Term> sep = AtomSets.sep(P, Q.getAtomSet());
		// compute sticky variable
		LinkedList<Term> sticky = unif.getStickyVariable(sep, R);
		if (sticky.isEmpty()) {
			u.add(new QueryUnifier(P, unif, R, Q));
		} else {
			// compute Pext the atoms of Pbar linked to P by the sticky
			// variables
			AtomSet Pbar = AtomSets.minus(Q.getAtomSet(), P);
			AtomSet Pext = new LinkedListAtomSet();
			for (Term t : sticky) {
				Iterator<Atom> ib = Pbar.iterator();
				while (ib.hasNext()) {
					Atom b = ib.next();
					if (b.getTerms().contains(t)) {
						Pext.add(b);
						ib.remove();
					}
				}
			}
			TermPartition part;
			for (TermPartition Uext : preUnifier(Pext, R, possibleUnification)) {
				part = unif.join(Uext);
				if (part != null && part.isAdmissible(R)) {
					u.addAll(extend(AtomSets.union(P, Pext), part,
							possibleUnification, Q, R));
				}
			}

		}

		return u;
	}

	private LinkedList<TermPartition> preUnifier(AtomSet P, Rule R,
			HashMap<Atom, LinkedList<TermPartition>> possibleUnification) {
		LinkedList<TermPartition> res = new LinkedList<TermPartition>();
		for (Atom a : P) {
			if (possibleUnification.get(a) != null)
				for (TermPartition ua : possibleUnification.get(a)) {
					AtomSet fa = new LinkedListAtomSet();
					fa.add(a);
					AtomSet aBar = null;
					aBar = AtomSets.minus(P, fa);

					if (!aBar.iterator().hasNext())
						res.add(ua);
					else {
						TermPartition part;
						for (TermPartition u : preUnifier(aBar, R,
								possibleUnification)) {
							part = ua.join(u);
							if (part != null && part.isAdmissible(R)) {
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

	private Collection<? extends TermPartition> getUnification(Atom a, Atom b) {
		if (compilation != null)
			return compilation.getUnification(a, b);
		else {
			LinkedList<TermPartition> res = new LinkedList<TermPartition>();
			TermPartition p = TermPartition.getPartitionByPosition(a, b);
			if (p != null)
				res.add(p);
			return res;
		}
	}

	private boolean isUnifiable(Atom a, Atom b) {
		if (compilation != null)
			return compilation.isUnifiable(a, b);
		else
			return a.getPredicate().equals(b.getPredicate());
	}

	/**
	 * Returns the list of the atoms of the query that can be unify with the
	 * head of R
	 * 
	 * @param query
	 *            the query to unify
	 * @param R
	 *            the rule whose has the head to unify
	 * @return the list of the atoms of the query that have the same predicate
	 *         as the head atom of R
	 */
	protected LinkedList<Atom> getUnifiableAtoms(ConjunctiveQuery query, Rule R) {
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
			for (Atom b : R.getHead())
				if (isUnifiable(a, b))
					answer.add(a);
		return answer;
	}

	private void addAll(Collection<ConjunctiveQuery> list,
			Collection<ConjunctiveQuery> other) {
		for (ConjunctiveQuery q : other)
			list.add(q);
	}

	public static Collection<Rule> getUnifiableRules(Iterable<Predicate> preds,
			IndexedByHeadPredicatesRuleSet ruleSet, RulesCompilation compilation) {
		TreeSet<Rule> res = new TreeSet<Rule>(new Comparator<Rule>() {
			@Override
			public int compare(Rule o1, Rule o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});
		TreeSet<Predicate> unifiable_preds = new TreeSet<Predicate>();
		if (compilation != null) {
			for (Predicate pred : preds) {
				unifiable_preds.addAll(compilation.getUnifiablePredicate(pred));
			}
			for (Predicate pred : unifiable_preds) {
				for (Rule r : ruleSet.getRulesByHeadPredicate(pred)) {
					res.add(r);
				}
			}
		} else {
			for (Predicate pred : preds) {
				for (Rule r : ruleSet.getRulesByHeadPredicate(pred)) {
					res.add(r);
				}
			}
		}

		return res;
	}

	// /////////////////////////////////////////////////////////////////////////
	// HELPER METHODS FOR QUERY
	// /////////////////////////////////////////////////////////////////////////

	private static boolean containAuxiliaryPredicate(ConjunctiveQuery q) {
		for (Atom a : q) {
			String label = (String) a.getPredicate().getLabel();
			if (label.length() > 3 && label.substring(0, 4).equals("aux_"))
				return true;
		}
		return false;
	}

	public static void removeAnswerPredicate(ConjunctiveQuery q) {
		Iterator<Atom> ita = q.iterator();
		while (ita.hasNext()) {
			Atom a = ita.next();
			String label = (String) a.getPredicate().getLabel();
			if (label.length() > 3 && label.substring(0, 4).equals("Ans_")) {
				ita.remove();
			}
		}
	}

}
