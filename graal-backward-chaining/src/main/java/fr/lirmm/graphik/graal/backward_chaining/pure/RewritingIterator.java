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

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.core.Rules;
import fr.lirmm.graphik.graal.core.compilation.NoCompilation;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByHeadPredicatesRuleSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.util.profiler.NoProfiler;
import fr.lirmm.graphik.util.profiler.Profilable;
import fr.lirmm.graphik.util.profiler.Profiler;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
class RewritinCloseableIterator implements CloseableIteratorWithoutException<ConjunctiveQuery>, Profilable {

	private PureQuery                   pquery;
	private LinkedListRuleSet           ruleset;
	private RulesCompilation            compilation;
	private CloseableIteratorWithoutException<ConjunctiveQuery> rewrites = null;

	private boolean                     unfolding = true;
	private RewritingOperator           operator;
	private Profiler                    profiler  = NoProfiler.instance();

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public RewritinCloseableIterator(boolean unfolding, ConjunctiveQuery query, Iterable<Rule> rules) {
		this(unfolding, query, rules, NoCompilation.instance());
	}

	public RewritinCloseableIterator(boolean unfolding, ConjunctiveQuery query, Iterable<Rule> rules, RewritingOperator operator) {
		this(unfolding, query, rules, NoCompilation.instance(), operator);
	}

	public RewritinCloseableIterator(boolean unfolding, ConjunctiveQuery query, Iterable<Rule> rules,
	    RulesCompilation compilation) {
		this(unfolding, query, rules, compilation, new AggregSingleRuleOperator());
	}

	public RewritinCloseableIterator(boolean unfolding, ConjunctiveQuery query, Iterable<Rule> rules,
	    RulesCompilation compilation, RewritingOperator operator) {
		this.unfolding = unfolding;
		this.pquery = new PureQuery(query);
		this.ruleset = new LinkedListRuleSet(Rules.computeSinglePiece(rules.iterator()));
		this.compilation = compilation;
		this.operator = operator;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean hasNext() {
		if (this.rewrites == null) {
			this.compute();
		}
		return this.rewrites.hasNext();
	}

	@Override
	public void close() {
	}

	@Override
	public ConjunctiveQuery next() {
		if (this.rewrites == null) {
			this.compute();
		}
		ConjunctiveQuery query = this.rewrites.next();
		PureQuery.removeAnswerPredicate(query);
		return query;
	}

	public boolean isUnfoldingEnable() {
		return this.unfolding;
	}

	// /////////////////////////////////////////////////////////////////////////
	//
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
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void compute() {
		if (this.getProfiler() != null && this.getProfiler().isProfilingEnabled()) {
			this.getProfiler().trace(this.pquery.getLabel());
		}
		IndexedByHeadPredicatesRuleSet indexedRuleSet = new IndexedByHeadPredicatesRuleSet(this.ruleset);

		// rewriting
		RewritingAlgorithm algo = new RewritingAlgorithm(this.operator);

		operator.setProfiler(this.getProfiler());
		algo.setProfiler(this.getProfiler());

		Iterable<ConjunctiveQuery> queries = algo.execute(pquery, indexedRuleSet, compilation);

		if (this.unfolding) {
			queries = Utils.unfold(queries, this.compilation, this.getProfiler());
		}

		this.rewrites = new CloseableIteratorAdapter<ConjunctiveQuery>(queries.iterator());
	}


}
