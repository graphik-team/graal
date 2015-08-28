/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.RuleUtils;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.core.impl.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.util.collections.ListComparator;

public class IDCompilation extends AbstractRulesCompilation {

	private static DefaultVariableGenerator varGen = new DefaultVariableGenerator("X"
			+ Integer.toString(IDCompilation.class.hashCode()));

	// a matrix for store conditions ( p -> q : [q][p] )
	private Map<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> conditions;



	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public IDCompilation() {
		super();
		this.conditions = new TreeMap<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>>();
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<Rule> getSaturation() {
		LinkedListRuleSet saturation = new LinkedListRuleSet();
		Map<Predicate, TreeMap<List<Integer>, InMemoryAtomSet>> newMap = new TreeMap<Predicate, TreeMap<List<Integer>, InMemoryAtomSet>>();
		// p -> q
		Predicate p, q;
		for (Map.Entry<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> e : this.conditions
				.entrySet()) {
			q = e.getKey();
			for (Map.Entry<Predicate, LinkedList<IDCondition>> map : e
					.getValue().entrySet()) {
				p = map.getKey();
				TreeMap<List<Integer>, InMemoryAtomSet> head = newMap
						.get(p);
				if (head == null) {
					head = new TreeMap<List<Integer>, InMemoryAtomSet>(
							new ListComparator<Integer>());
					newMap.put(p, head);
				}
				for (IDCondition conditionPQ : map.getValue()) {
					InMemoryAtomSet atomSet = head.get(conditionPQ.getBody());
					if (atomSet == null) {
						atomSet = new LinkedListAtomSet();
						head.put(conditionPQ.getBody(), atomSet);
					}
					atomSet.add(new DefaultAtom(q, conditionPQ.generateHead()));
				}
			}
		}
		
		for(Map.Entry<Predicate, TreeMap<List<Integer>, InMemoryAtomSet>> e1 : newMap.entrySet()) {
			p = e1.getKey();
			for (Map.Entry<List<Integer>, InMemoryAtomSet> e2 : e1.getValue()
					.entrySet()) {
				List<Term> terms = new LinkedList<Term>();
				for (Integer i : e2.getKey()) {
					terms.add(DefaultTermFactory.instance().createVariable(
							"X" + i));
				}
				InMemoryAtomSet body = new LinkedListAtomSet();
				body.add(new DefaultAtom(p, terms));
				saturation.add(RuleFactory.instance().create(body, e2.getValue()));
			}
		}
		return saturation;
	}

	@Override
	public void compile(Iterator<Rule> ruleset) {
		LinkedList<Rule> compilable = this.extractCompilable(ruleset);
		if (this.getProfiler() != null) {
			this.getProfiler().start("Compilation total time");
		}

		this.createIDCondition(compilable.iterator());
		this.computeSaturation();

		if (this.getProfiler() != null) {
			this.getProfiler().stop("Compilation total time");
		}
	}

	@Override
	public void load(Iterator<Rule> ruleSet, Iterator<Rule> saturation) {
		if (this.getProfiler() != null) {
			this.getProfiler().start("Compilation load time");
		}

		this.extractCompilable(ruleSet); // compilable rules are removed
		Iterator<Rule> monoPiecesaturation = RuleUtils
				.computeMonoPiece(saturation);
		this.createIDCondition(monoPiecesaturation);

		if (this.getProfiler() != null) {
			this.getProfiler().stop("Compilation load time");
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// CONST METHODS
	// /////////////////////////////////////////////////////////////////////////

	public List<IDCondition> getConditions(Predicate predB, Predicate predH) {
		LinkedList<IDCondition> res = null;
		if (predB.equals(predH)) {
			res = new LinkedList<IDCondition>();
			ArrayList<Term> terms = new ArrayList<Term>(predB.getArity());
			for (int i = 0; i < predH.getArity(); i++) {
				terms.add(varGen.getFreshVar());
			}
			res.add(new IDConditionImpl(terms, terms));
		} else {
			Map<Predicate, LinkedList<IDCondition>> condH = this.conditions
					.get(predH);

			if (condH != null) {
				res = condH.get(predB);
			}
		}
		if (res != null)
			return res;
		else
			return Collections.emptyList();
	}

	/**
	 * Return true if the specified rule is compilable.
	 */
	@Override
	public boolean isCompilable(Rule r) {
		return RuleUtils.hasAtomicBody(r) && RuleUtils.hasAtomicHead(r)
				&& r.getExistentials().isEmpty()
				&& r.getTerms(Term.Type.CONSTANT).isEmpty();
	}

	/**
	 * can return true if there are not mappable
	 */
	@Override
	public boolean isMappable(Atom father, Atom son) {
		Predicate predB = son.getPredicate();
		Predicate predH = father.getPredicate();
		if (predB.equals(predH))
			return true;

		else
			return !getConditions(predB, predH).isEmpty();
	}

	/*
	 * @Override public LinkedList<Substitution> getMapping(Atom father, Atom
	 * son) { LinkedList<Substitution> res = new LinkedList<Substitution>();
	 * Predicate predB = son.getPredicate(); Predicate predH =
	 * father.getPredicate(); List<DCondition2> conds = getConditions(predB,
	 * predH); for (DCondition2 cond : conds) { if
	 * (cond.checkBody(son.getTerms()))
	 * res.add(cond.getSubstitution(son.getTerms(), father.getTerms())); }
	 * return res; }
	 */

	/**
	 * can return true if there are not unifiable
	 */
	@Override
	public boolean isUnifiable(Atom father, Atom son) {
		return isMappable(father, son);
	}

	@Override
	public LinkedList<TermPartition> getUnification(Atom father, Atom son) {
		LinkedList<TermPartition> res = new LinkedList<TermPartition>();
		Predicate predB = son.getPredicate();
		Predicate predH = father.getPredicate();
		List<IDCondition> conds = getConditions(predB, predH);
		for (IDCondition cond : conds) {
			res.add(cond.generateUnification(son.getTerms(), father.getTerms()));
		}
		return res;
	}

	@Override
	public boolean isImplied(Atom father, Atom son) {
		Predicate predB = son.getPredicate();
		Predicate predH = father.getPredicate();
		List<IDCondition> conds = getConditions(predB, predH);
		for (IDCondition cond : conds) {
			if (cond.imply(son.getTerms(), father.getTerms())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Collection<Predicate> getUnifiablePredicate(Predicate p) {
		Collection<Predicate> res = new LinkedList<Predicate>();
		Map<Predicate, LinkedList<IDCondition>> condH = this.conditions
				.get(p);
		res.add(p);
		if (condH != null)
			res.addAll(condH.keySet());

		return res;
	}

	/**
	 * Return all possible rewritings of this Atom by this compilation.
	 */
	@Override
	public Collection<Atom> getRewritingOf(Atom atom) {
		TreeSet<Atom> res = new TreeSet<Atom>();
		res.add(atom);

		Predicate predH = atom.getPredicate();
		Map<Predicate, LinkedList<IDCondition>> condH = this.conditions
				.get(predH);
		if (condH != null) {
			LinkedList<IDCondition> conds;
			Predicate predB;
			for (Map.Entry<Predicate, LinkedList<IDCondition>> entry : condH
					.entrySet()) {
				predB = entry.getKey();
				conds = entry.getValue();
				for (IDCondition cond : conds) {
					if (cond.checkHead(atom.getTerms()))
						res.add(new DefaultAtom(predB, cond.generateBody(atom
								.getTerms())));
				}
			}
		}
		return res;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void createIDCondition(Iterator<Rule> compilable) {
		if (this.getProfiler() != null) {
			this.getProfiler().start("Compilation create IDCondition time");
		}
		Atom b;
		Atom h;
		while (compilable.hasNext()) {
			Rule ru = compilable.next();
			b = ru.getBody().iterator().next();
			h = ru.getHead().iterator().next();
			IDCondition cond = new IDConditionImpl(b.getTerms(), h.getTerms());
			addCondition(b.getPredicate(), h.getPredicate(), cond,
					this.conditions);

		}
		if (this.getProfiler() != null) {
			this.getProfiler().stop("Compilation create IDCondition time");
		}
	}

	private static TreeMap<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> deepCopyMapMapList(
			Map<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> map) {
		TreeMap<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> tmp = new TreeMap<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>>();
		for (Map.Entry<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> e : map
				.entrySet()) {
			tmp.put(e.getKey(), deepCopyMapList(e.getValue()));
		}
		return tmp;
	}

	private static TreeMap<Predicate, LinkedList<IDCondition>> deepCopyMapList(
			Map<Predicate, LinkedList<IDCondition>> map) {
		TreeMap<Predicate, LinkedList<IDCondition>> tmp = new TreeMap<Predicate, LinkedList<IDCondition>>();
		for (Map.Entry<Predicate, LinkedList<IDCondition>> e : map.entrySet()) {
			tmp.put(e.getKey(), new LinkedList<IDCondition>(e.getValue()));
		}
		return tmp;
	}

	private void computeSaturation() {
		// deep copy of conditions
		Map<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> conditionsTmp = deepCopyMapMapList(this.conditions);
		// p -> q
		Predicate p, q;
		for (Map.Entry<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> e : conditionsTmp
				.entrySet()) {
			q = e.getKey();
			for (Map.Entry<Predicate, LinkedList<IDCondition>> map : e
					.getValue().entrySet()) {
				p = map.getKey();
				for (IDCondition conditionPQ : map.getValue()) {
					computeSaturation(conditionsTmp, p, q, conditionPQ);
				}
			}
		}
	}

	private void computeSaturation(
			Map<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> conditionsTmp,
			Predicate p, Predicate q,
			IDCondition conditionPQ) {
		TreeMap<Predicate, LinkedList<IDCondition>> map = conditionsTmp
				.get(p);
		if (map != null) {
			for (Map.Entry<Predicate, LinkedList<IDCondition>> e : map
					.entrySet()) {
				Predicate r = e.getKey();
				for (IDCondition conditionRP : e.getValue()) {
					IDCondition conditionRQ = conditionRP
							.composeWith(conditionPQ);
					if (conditionRQ != null) {
						// filter trivial implication - p(x,y,z) -> p(x,y,z)
						if (!(r.equals(q) && conditionRQ.isIdentity())) {
							if (addCondition(r, q, conditionRQ,
								this.conditions)) {
								this.computeSaturation(conditionsTmp, r, q,
									conditionRQ);
							}
						}
					}
				}
			}
		}
	}

	private static boolean addCondition(
			Predicate predBody,
			Predicate predHead,
			IDCondition cond,
			Map<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> conditionMatrix) {
		LinkedList<IDCondition> conds;
		TreeMap<Predicate, LinkedList<IDCondition>> condH = conditionMatrix
				.get(predHead);
		
		if (condH == null) {
			condH = new TreeMap<Predicate, LinkedList<IDCondition>>();
			conditionMatrix.put(predHead, condH);
		}

		conds = condH.get(predBody);
		if (conds == null) {
			conds = new LinkedList<IDCondition>();
			condH.put(predBody, conds);
		}

		if (!conds.contains(cond)) {
			conds.add(cond);
			return true;
		} else {
			return false;
		}
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS OVERRIDING
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		this.appendTo(sb);
		return sb.toString();
	}

	public void appendTo(StringBuilder sb) {
		for (Rule r : this.getSaturation()) {
			r.appendTo(sb);
			sb.append('\n');
		}
	}


}
