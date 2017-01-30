/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.forward_chaining;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.forward_chaining.AbstractChase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.forward_chaining.halting_condition.RestrictedChaseStopCondition;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.Iterators;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class ChaseWithGRDAndUnfiers extends AbstractChase {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ChaseWithGRDAndUnfiers.class);
	
	private GraphOfRuleDependencies grd;
	private AtomSet atomSet;
	private Queue<Triple<Rule, Substitution, InMemoryAtomSet>> queue = new LinkedList<Triple<Rule, Substitution,InMemoryAtomSet>>();
	private ChaseHaltingCondition hc =  new RestrictedChaseStopCondition();

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////
	
	public ChaseWithGRDAndUnfiers(GraphOfRuleDependencies grd, AtomSet atomSet, RuleApplier ruleApplier) {
		super(ruleApplier);
		this.grd = grd;
		this.atomSet = atomSet;
		for(Rule r : grd.getRules()) {		
			this.queue.add(new ImmutableTriple<Rule, Substitution, InMemoryAtomSet>(r, new HashMapSubstitution(), new LinkedListAtomSet()));
		}
	}
	
	public ChaseWithGRDAndUnfiers(GraphOfRuleDependencies grd, AtomSet atomSet) {
		this(grd, atomSet, new DefaultRuleApplier());
	}
	
	public ChaseWithGRDAndUnfiers(Iterator<Rule> rules, AtomSet atomSet) {
		this(new GraphOfRuleDependencies(rules, true), atomSet, new DefaultRuleApplier());
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void next() throws ChaseException {
		Rule rule, unifiedRule;
		Substitution unificator;
		
		Queue<Triple<Rule, Substitution, InMemoryAtomSet>> newQueue = new LinkedList<Triple<Rule, Substitution,InMemoryAtomSet>>();
		InMemoryAtomSet newAtomSet = new DefaultInMemoryGraphAtomSet();
		
		try {
			while(!queue.isEmpty()) {

				Triple<Rule, Substitution, InMemoryAtomSet> pair = queue.poll();
				if(pair != null) {
					unificator = pair.getMiddle();
					InMemoryAtomSet part = pair.getRight();
					rule = pair.getLeft();		
				
					if(LOGGER.isDebugEnabled()) {
						LOGGER.debug("\nExecute rule: {} with unificator {}", rule, unificator);
					}
					
					unifiedRule = computeInitialTargetTermsSubstitution(rule).createImageOf(rule);
					unifiedRule = unificator.createImageOf(unifiedRule);
					unifiedRule.getBody().removeAll(part);
					unificator = targetToSource(unificator);
					ConjunctiveQuery query = DefaultConjunctiveQueryFactory.instance().create(unifiedRule.getBody(),
					        new LinkedList<Term>(unifiedRule.getFrontier()));
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Rule to execute: {}", unifiedRule.toString());
						LOGGER.debug("       -- Query: {}", query.toString());
					}
					
					// Get projections
					List<Substitution> projections = Iterators.toList(StaticHomomorphism.instance().execute(query, atomSet));

					try {
						for(Substitution proj : projections) {
							InMemoryAtomSet newFacts = proj.createImageOf(unifiedRule.getHead());
							ConjunctiveQuery q = new DefaultConjunctiveQuery(newFacts);

							if (!StaticHomomorphism.instance().execute(q, newAtomSet).hasNext()) {
								// Existential variables instantiation added to proj
								CloseableIterator<Atom> it = hc.apply(unifiedRule, proj, atomSet);
								
								if (it.hasNext()) {
									LinkedListAtomSet foundPart = new LinkedListAtomSet();
									foundPart.addAll(it);
									newAtomSet.addAll(foundPart);
									// Makes the projection compatible with triggered rules unifiers
									Substitution compatibleProj = targetToSource(proj);
									
									for (Integer e : this.grd.getOutgoingEdgesOf(rule)) {
										Rule triggeredRule = this.grd.getEdgeTarget(e);
									
										for (Substitution u : this.grd.getUnifiers(e)) {
											if(u != null) {
												Substitution comp = unificator.compose(u);
												Substitution aggreg = compatibleProj.aggregate(comp);
												aggreg = forgetSource(aggreg);
												if(LOGGER.isDebugEnabled()) {
													LOGGER.debug("-- -- Dependency: {}", triggeredRule);
													LOGGER.debug("-- -- Substitution:{} ", compatibleProj);
													LOGGER.debug("-- -- Unificator: {}", u);
													LOGGER.debug("-- -- Aggregation: {}\n", aggreg);
												}
										
												if(aggreg != null) {
													newQueue.add(new ImmutableTriple<Rule, Substitution, InMemoryAtomSet>(triggeredRule, aggreg, foundPart));
												}
											}
										}
									}
								}
							}
						}
					
					} catch (HomomorphismFactoryException e) {
						throw new RuleApplicationException("Error during rule application", e);
					} catch (HomomorphismException e) {
						throw new RuleApplicationException("Error during rule application", e);
					} catch (IteratorException e) {
						throw new RuleApplicationException("Error during rule application", e);
					}
				}
			}
			
			queue = newQueue;
			atomSet.addAll(newAtomSet);

		} catch (Exception e) {
			e.printStackTrace();
			throw new ChaseException("An error occur pending saturation step.", e);
		}
	}
	
	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}
	
	/** 
	 * Apply s1 over range of s2
	 * @param s1
	 * @param s2
	 * @return
	 */
	private Substitution applyOn(Substitution s1, Substitution s2) {
		Substitution res = SubstitutionFactory.instance().createSubstitution();
		for(Variable v : s2.getTerms()) {
			res.put(v, s1.createImageOf(s2.createImageOf(v)));
		}
		return res;
	}
	
	/** 
	 * Apply s1 over range of s2
	 * @param s1
	 * @param s2
	 * @return
	 */
	private Substitution targetToSource(Substitution s) {
		Substitution res = SubstitutionFactory.instance().createSubstitution();
		for(Variable v : s.getTerms()) {
			res.put(DefaultTermFactory.instance().createVariable(v.getLabel().replaceAll("T::", "S::")), s.createImageOf(v));
		}
		return res;
	}
	
	public static Substitution computeInitialTargetTermsSubstitution(Rule r) {
		Substitution s = new TreeMapSubstitution();

		for (Term t2 : r.getTerms(Term.Type.VARIABLE)) {
			Term t2b = DefaultTermFactory.instance().createVariable("T::" + t2.getIdentifier().toString());
			s.put((Variable) t2, t2b);
		}

		return s;
	}

	private Substitution forgetSource(Substitution s) {
		Substitution res = SubstitutionFactory.instance().createSubstitution();
		for(Variable v : s.getTerms()) {
			if(v.getLabel().startsWith("T::")) {
				res.put(v, s.createImageOf(v));
			}
		}
		return res;
	}
}
