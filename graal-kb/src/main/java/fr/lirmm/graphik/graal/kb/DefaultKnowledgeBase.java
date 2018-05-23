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
/**
* 
*/
package fr.lirmm.graphik.graal.kb;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.api.core.Ontology;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.QueryLabeler;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.UnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.api.kb.Approach;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBaseException;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.api.util.TimeoutException;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.DefaultQueryLabeler;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphStore;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.grd.DefaultGraphOfRuleDependencies;
import fr.lirmm.graphik.graal.core.ruleset.DefaultOntology;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.BreadthFirstChase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseWithGRD;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.graal.rulesetanalyser.Analyser;
import fr.lirmm.graphik.graal.rulesetanalyser.RuleSetPropertyHierarchy;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FESProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.FUSProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.property.RuleSetProperty;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;
import fr.lirmm.graphik.util.profiler.AbstractProfilable;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.filter.FilterIterator;
import fr.lirmm.graphik.util.stream.filter.UniqFilter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class DefaultKnowledgeBase extends AbstractProfilable implements KnowledgeBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultKnowledgeBase.class);

	private final QueryLabeler queryLabeler;

	private final Ontology ruleset;
	private final AtomSet store;
	private final Map<String, Query> queries = new HashMap<String, Query>();

	private RulesCompilation ruleCompilation;
	private AnalyserRuleSet analysedRuleSet;
	private Analyser analyse;

	private boolean isSaturated = false;
	private boolean isSemiSaturated = false;
	private boolean isCompiled = false;
	private boolean isAnalysed = false;
	private boolean isFESSaturated = false;
	private RuleSet fesRuleSet;
	private RuleSet fusRuleSet;
	private GraphOfRuleDependencies fesGRD;

	private Approach approach = Approach.REWRITING_FIRST;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	private DefaultKnowledgeBase() {
		this.ruleset = new DefaultOntology();
		this.store = new DefaultInMemoryGraphStore();
		this.queryLabeler = new DefaultQueryLabeler();
		this.analysedRuleSet = new AnalyserRuleSet(this.ruleset);
	}

	/**
	 * Constructs a DefaultKnowledgeBase with a
	 * {@link DefaultInMemoryGraphStore} as storage. Loads {@link Rule rules} and
	 * {@link Atom atoms} from the specified parser.
	 */
	public DefaultKnowledgeBase(Parser<Object> parser) throws AtomSetException {
		this(new DefaultInMemoryGraphStore(), parser);
	}

	/**
	 * Constructs a DefaultKnowledgeBase embedding the specified {@link Store}.
	 * Loads {@link Rule rules} and supplementary {@link Atom atoms} from the specified
	 * parser.
	 */
	public DefaultKnowledgeBase(AtomSet store, Parser<Object> parser) throws AtomSetException {
		this.ruleset = new DefaultOntology();
		this.store = store;
		this.load(parser);
		this.queryLabeler = new DefaultQueryLabeler();
		this.analysedRuleSet = new AnalyserRuleSet(this.ruleset);
	}

	/**
	 * Constructs a DefaultKnowledgeBase embedding the specified {@link Store} and .
	 * loads {@link Rule rules} from the specified ontology.
	 */
	public DefaultKnowledgeBase(AtomSet store, RuleSet ontology) {
		this.ruleset = new DefaultOntology(ontology);
		this.store = store;
		this.queryLabeler = new DefaultQueryLabeler();
		this.analysedRuleSet = new AnalyserRuleSet(this.ruleset);
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS/SETTERS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @return the ruleset
	 */
	@Override
	public RuleSet getOntology() {
		return ruleset;
	}

	/**
	 * @return the atomset
	 */
	@Override
	public AtomSet getFacts() {
		return store;
	}
	
	public GraphOfRuleDependencies getGraphOfRuleDependencies() {
		return this.analysedRuleSet.getGraphOfRuleDependencies();
	}

	@Override
	public boolean isConsistent() throws KnowledgeBaseException {
		try {
			CloseableIterator<Substitution> results = this
					.query(DefaultConjunctiveQueryFactory.instance().BOOLEAN_BOTTOM_QUERY);
			boolean res = !results.hasNext();
			results.close();
			return res;
		} catch (IteratorException e) {
			throw new KnowledgeBaseException(e);
		}
	}

	@Override
	public void saturate() throws KnowledgeBaseException {
		if(this.approach == Approach.REWRITING_ONLY) {
			throw new KnowledgeBaseException("Saturation is not allowed, this knowledge base is in rewriting only approach.");
		}
		if (!this.isSaturated) {
			boolean run = this.approach == Approach.SATURATION_ONLY;
			if(!run) {
				this.analyse();
				run = this.analyse.isFES();
			}
			if (run) {
				GraphOfRuleDependencies grd = this.getGraphOfRuleDependencies();
				ChaseWithGRD<AtomSet> chase = new ChaseWithGRD<>(grd, this.store);
				chase.setProfiler(this.getProfiler());
				try {
					chase.execute();
				} catch (ChaseException e) {
					throw new KnowledgeBaseException(e);
				}

				this.isSaturated = true;
				this.isFESSaturated = true;
				this.isSemiSaturated = true;
			} else {
				throw new KnowledgeBaseException("There is no proof for FES decidability");
			}
		}
	}

	@Override
	public void semiSaturate() throws KnowledgeBaseException {
		if (!this.isSemiSaturated) {
			this.compileRule();
			Chase chase = new BreadthFirstChase(this.ruleCompilation.getSaturation(), this.store);
			chase.setProfiler(this.getProfiler());
			try {
				chase.next();
			} catch (ChaseException e) {
				throw new KnowledgeBaseException(e);
			}

			this.isSemiSaturated = true;
		}
	}

	@Override
	public CloseableIterator<Substitution> homomorphism(Query query) throws KnowledgeBaseException {
		try {
			return SmartHomomorphism.instance().execute(query, this.store);
		} catch (HomomorphismException e) {
			throw new KnowledgeBaseException(e);
		}
	}

	@Override
	public CloseableIterator<Substitution> query(Query query) throws KnowledgeBaseException {
		if (this.isSaturated) {
			try {
				return SmartHomomorphism.instance().execute(query, this.store);
			} catch (HomomorphismException e) {
				throw new KnowledgeBaseException(e);
			}
		} else if (query instanceof ConjunctiveQuery) {
			ConjunctiveQuery cq = (ConjunctiveQuery) query;

			this.analyse();
			if (this.analyse.isDecidable()
					&& (!this.getApproach().equals(Approach.REWRITING_ONLY) || this.getFESRuleSet().isEmpty())
					&& (!this.getApproach().equals(Approach.SATURATION_ONLY) || this.getFUSRuleSet().isEmpty())) {
				try {
					this.fesSaturate();
					this.compileRule();
					RuleSet fusRuleSet = this.getFUSRuleSet();

					PureRewriter pure = new PureRewriter(false);
					CloseableIteratorWithoutException<ConjunctiveQuery> it = pure.execute(cq, fusRuleSet,
							this.ruleCompilation);
					UnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(cq.getAnswerVariables(), it);

					CloseableIterator<Substitution> resultIt = null;
					try {
						if(this.isSemiSaturated) {
							resultIt = SmartHomomorphism.instance().execute(query, this.store);
						} else {
							resultIt = SmartHomomorphism.instance().execute(ucq, this.store, this.ruleCompilation);
						}
					} catch (HomomorphismException e) {
						if (this.getApproach().equals(Approach.REWRITING_FIRST)) {
							it = PureRewriter.unfold(ucq, this.ruleCompilation);
							ucq = new DefaultUnionOfConjunctiveQueries(cq.getAnswerVariables(), it);
						} else {
							this.semiSaturate();
						}
						resultIt = SmartHomomorphism.instance().execute(ucq, this.store);
					}
					return new FilterIterator<Substitution, Substitution>(resultIt, new UniqFilter<Substitution>());
				} catch (ChaseException e) {
					throw new KnowledgeBaseException(e);
				} catch (HomomorphismException e) {
					throw new KnowledgeBaseException(e);
				}
			} else {
				throw new KnowledgeBaseException(
						"No decidable combinaison found with the defined approach: " + this.getApproach());
			}

		} else {
			throw new KnowledgeBaseException("No implementation found for this kind of query: " + query.getClass());
		}
	}

	@Override
	public CloseableIterator<Substitution> query(Query query, long timeout) throws KnowledgeBaseException, TimeoutException {
		long remainingTime = timeout;
		if (this.isSaturated) {
			try {
				return SmartHomomorphism.instance().execute(query, this.store);
			} catch (HomomorphismException e) {
				throw new KnowledgeBaseException(e);
			}
		} else if (query instanceof ConjunctiveQuery) {
			
			ConjunctiveQuery cq = (ConjunctiveQuery) query;

			if(this.approach != Approach.REWRITING_ONLY && this.approach != Approach.SATURATION_ONLY) {
				this.analyse();
			}
			if (this.approach == Approach.REWRITING_ONLY || this.approach == Approach.SATURATION_ONLY || this.analyse.isDecidable()) {
				try {
					long time = System.currentTimeMillis();
					this.fesSaturate(remainingTime);
					if (timeout > 0) {
						remainingTime -= (System.currentTimeMillis() - time);
						if (remainingTime <= 0) {
							throw new TimeoutException(timeout);
						}
					}
					this.compileRule();
					RuleSet fusRuleSet = this.getFUSRuleSet();

					PureRewriter pure = new PureRewriter(false);
					CloseableIteratorWithoutException<ConjunctiveQuery> it = pure.execute(cq, fusRuleSet,
							this.ruleCompilation, remainingTime);
					UnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(cq.getAnswerVariables(), it);

					CloseableIterator<Substitution> resultIt = null;
					try {
						resultIt = SmartHomomorphism.instance().execute(ucq, this.store, this.ruleCompilation);
					} catch (HomomorphismException e) {
						if (this.getApproach().equals(Approach.REWRITING_FIRST)) {
							it = PureRewriter.unfold(ucq, this.ruleCompilation);
							ucq = new DefaultUnionOfConjunctiveQueries(cq.getAnswerVariables(), it);
						} else {
							this.semiSaturate();
						}
						resultIt = SmartHomomorphism.instance().execute(ucq, this.store);
					}
					return new FilterIterator<Substitution, Substitution>(resultIt, new UniqFilter<Substitution>());
				} catch (ChaseException e) {
					throw new KnowledgeBaseException(e);
				} catch (HomomorphismException e) {
					throw new KnowledgeBaseException(e);
				}
			} else {
				throw new KnowledgeBaseException(
						"No decidable combinaison found with the defined approach: " + this.getApproach());
			}

		} else {
			throw new KnowledgeBaseException("No implementation found for this kind of query: " + query.getClass());
		}
	}

	@Override
	public void close() {
		if (this.store instanceof Closeable) {
			try {
				((Closeable) this.store).close();
			} catch (IOException e) {
				LOGGER.warn("Error while closing KnowledgeBase: ", e);
			}
		}
	}

	@Override
	public Set<String> getRuleNames() {
		return this.ruleset.getRuleNames();
	}

	@Override
	public Rule getRule(String name) {
		return this.ruleset.getRule(name);
	}

	@Override
	public boolean addQuery(Query query) {
		if (query == null) {
			return false;
		}
		if (query.equals(this.queries.get(query.getLabel()))) {
			return false;
		}
		this.queryLabeler.setLabel(query);
		return this.queries.put(query.getLabel(), query) != null;
	}

	@Override
	public Set<String> getQueryNames() {
		return this.queries.keySet();
	}

	@Override
	public Query getQuery(String name) {
		return this.queries.get(name);
	}

	@Override
	public Approach getApproach() {
		return this.approach;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append('\n');
		sb.append(this.ruleset.toString());
		sb.append('\n');
		sb.append(this.store.toString());
		return sb.toString();
	}

	// /////////////////////////////////////////////////////////////////////////
	// PACKAGE METHODS
	// /////////////////////////////////////////////////////////////////////////

	void setPriority(Approach p) {
		this.approach = p;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Run saturation with a timeout at <code>timeout</code> milliseconds for this thread to die. A timeout of 0 means to wait forever. 
	 * @param timeout in milliseconds
	 * @throws ChaseException
	 * @throws TimeoutException
	 */
	protected void fesSaturate(long timeout) throws ChaseException, TimeoutException {
		if (!isFESSaturated) {
			GraphOfRuleDependencies grd = this.getFESGraphOfRuleDependencies();
			ChaseWithGRD<AtomSet> chase = new ChaseWithGRD<>(grd, this.store);
			chase.setProfiler(this.getProfiler());
			chase.execute(timeout);
			this.isFESSaturated = true;
		}
	}
	
	protected void fesSaturate() throws ChaseException {
		try {
			this.fesSaturate(0);
		} catch (TimeoutException e) {
			throw new Error("Shoul never occur");
		}
	}

	protected RuleSet getFESRuleSet() {
		if (this.fesRuleSet == null) {
			fesRuleSet = new LinkedListRuleSet();

			if (this.approach == Approach.SATURATION_ONLY) {
				fesRuleSet.addAll(this.ruleset.iterator());
			} else if (this.approach != Approach.REWRITING_ONLY) {
				this.analyse();
				int[] combine = getDecidableCombination();
				List<AnalyserRuleSet> scc = this.analysedRuleSet.getSCC();
				for (int i = 0; i < combine.length; ++i) {
					if ((combine[i] & Analyser.COMBINE_FES) != 0) {
						fesRuleSet.addAll(scc.get(i).iterator());
					}
				}
			}
		}
		return this.fesRuleSet;
	}

	protected RuleSet getFUSRuleSet() {
		if (this.fusRuleSet == null) {
			this.fusRuleSet = new LinkedListRuleSet();

			if (this.approach == Approach.REWRITING_ONLY) {
				fusRuleSet.addAll(this.ruleset.iterator());
			} else if (this.approach != Approach.SATURATION_ONLY) {
				this.analyse();
				int[] combine = getDecidableCombination();
				List<AnalyserRuleSet> scc = this.analysedRuleSet.getSCC();
				for (int i = 0; i < combine.length; ++i) {
					if ((combine[i] & Analyser.COMBINE_FUS) != 0) {
						this.fusRuleSet.addAll(scc.get(i).iterator());
					}
				}
			}
		}
		return this.fusRuleSet;
	}

	protected GraphOfRuleDependencies getFESGraphOfRuleDependencies() {
		if (this.fesGRD == null) {
			RuleSet fesRuleSet = this.getFESRuleSet();
			this.fesGRD = new DefaultGraphOfRuleDependencies(fesRuleSet);
		}
		return this.fesGRD;
	}

	protected int[] getDecidableCombination() {
		if (approach == Approach.SATURATION_FIRST || approach == Approach.SATURATION_ONLY) {
			return this.analyse.combineFES();
		} else {
			return this.analyse.combineFUS();
		}
	}

	protected void analyse() {
		if (!this.isAnalysed) {
			Map<String, RuleSetProperty> properties = new HashMap<>();
			if(this.approach != Approach.REWRITING_ONLY) {
				properties.putAll(RuleSetPropertyHierarchy.generatePropertyMapSpecializationOf(FESProperty.instance()));
			}
			if(this.approach != Approach.SATURATION_ONLY) {
				properties.putAll(RuleSetPropertyHierarchy.generatePropertyMapSpecializationOf(FUSProperty.instance()));
			}
			this.analyse = new Analyser(analysedRuleSet);
			this.analyse.setProperties(properties.values());
			this.isAnalysed = true;
		}
	}

	protected void compileRule() {
		if (!this.isCompiled) {
			this.ruleCompilation = new IDCompilation();
			this.ruleCompilation.compile(this.getFUSRuleSet().iterator());
			this.isCompiled = true;
		}
	}

	protected void load(Parser<Object> parser) throws AtomSetException {
		Object o;
		try {
			while (parser.hasNext()) {
				o = parser.next();
				if (o instanceof Rule) {
					this.getOntology().add((Rule) o);
				} else if (o instanceof Atom) {
					this.getFacts().add((Atom) o);
				}
			}
		} catch (IteratorException e) {
			throw new AtomSetException(e);
		}
	}

};
