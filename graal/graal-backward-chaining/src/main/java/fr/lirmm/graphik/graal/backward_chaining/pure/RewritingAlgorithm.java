/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.Misc;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;
import fr.lirmm.graphik.util.Profilable;
import fr.lirmm.graphik.util.Profiler;
import fr.lirmm.graphik.util.Verbosable;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RewritingAlgorithm implements Verbosable, Profilable {

	private boolean verbose;
	private Profiler profiler;
	
	private RewritingOperator operator;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////
	
	public RewritingAlgorithm(RewritingOperator operator) {
		this.operator = operator;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Compute and returns all the most general rewrites of the object's query
	 * 
	 * @return a list of the most general rewrite computed for the specified query and
	 *         the specified compilation
	 * @throws Exception
	 * 
	 * @author Mélanie KÖNIG
	 */
	public Collection<ConjunctiveQuery> execute(ConjunctiveQuery query, IndexedByHeadPredicatesRuleSet ruleSet, RulesCompilation compilation) {
		if(this.verbose) {
			this.profiler.add("CONFIG", operator.toString());
		}
		LinkedList<ConjunctiveQuery> finalRewritingSet = new LinkedList<ConjunctiveQuery>();
		Queue<ConjunctiveQuery> rewriteSetToExplore = new LinkedList<ConjunctiveQuery>();
		Collection<ConjunctiveQuery> currentRewriteSet;
		
		int exploredRewrites = 0;
		int generatedRewrites = 0;

		if(this.verbose) {
			this.profiler.start("rewriting time");
		}

		// remove some basic redundancy
		PureQuery pquery = new PureQuery(compilation.getIrredondant(query.getAtomSet()), query.getAnswerVariables());

		pquery.addAnswerPredicate();
		rewriteSetToExplore.add(pquery);
		finalRewritingSet.add(pquery);

		ConjunctiveQuery q;
		while (!rewriteSetToExplore.isEmpty()) {

			/* take the first query to rewrite */
			q = rewriteSetToExplore.poll();
			++exploredRewrites; // stats

			/* compute all the rewrite from it */
			currentRewriteSet = this.operator.getRewritesFrom(q, ruleSet, compilation);
			generatedRewrites += currentRewriteSet.size(); // stats

			/* keep only the most general among query just computed */
			Misc.computeCover(currentRewriteSet, compilation);

			/*
			 * keep only the query just computed that are more general than
			 * query already compute
			 */
			selectMostGeneralFromRelativeTo(currentRewriteSet,
					finalRewritingSet, compilation);

			
			// keep to explore only most general query
			selectMostGeneralFromRelativeTo(rewriteSetToExplore,
					currentRewriteSet, compilation);

			// add to explore the query just computed that we keep
			rewriteSetToExplore.addAll(currentRewriteSet);

			
			/*
			 * keep in final rewrite set only query more general than query just
			 * computed
			 */
			selectMostGeneralFromRelativeTo(finalRewritingSet,
					currentRewriteSet, compilation);
			
			// add in final rewrite set the query just compute that we keep
			finalRewritingSet.addAll(currentRewriteSet);

		}

		if(this.verbose) {
			this.profiler.add("Generated rewritings", generatedRewrites);
			this.profiler.add("Explored rewritings", exploredRewrites);
			this.profiler.add("Pivots rewritings", finalRewritingSet.size());
			this.profiler.stop("rewriting time");
		}

		return finalRewritingSet;
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
			Collection<ConjunctiveQuery> rewritingSet, RulesCompilation compilation) {
		Iterator<? extends ConjunctiveQuery> i = toSelect.iterator();
		while (i.hasNext()) {
			InMemoryAtomSet f = i.next().getAtomSet();
			if (containMoreGeneral(f, rewritingSet, compilation))
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
	public boolean containMoreGeneral(InMemoryAtomSet f,
			Collection<ConjunctiveQuery> rewriteSet, RulesCompilation compilation) {
		for(ConjunctiveQuery q : rewriteSet) {
			InMemoryAtomSet a = q.getAtomSet();
			if (Misc.isMoreGeneralThan(a, f, compilation))
				return true;
		}
		return false;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void enableVerbose(boolean enable) {
		this.verbose = enable;
	}

	@Override
	public void setProfiler(Profiler profiler) {
		this.profiler = profiler;
	}

	@Override
	public Profiler getProfiler() {
		return this.profiler;
	}
}
