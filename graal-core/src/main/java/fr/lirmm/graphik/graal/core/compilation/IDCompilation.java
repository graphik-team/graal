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
 package fr.lirmm.graphik.graal.core.compilation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultVariableGenerator;
import fr.lirmm.graphik.graal.core.Rules;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.Partition;
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
				saturation.add(DefaultRuleFactory.instance().create(body, e2.getValue()));
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
		Iterator<Rule> monoPiecesaturation = Rules
				.computeSinglePiece(saturation);
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
				terms.add(varGen.getFreshSymbol());
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
		return Rules.hasAtomicBody(r) && Rules.hasAtomicHead(r)
				&& r.getExistentials().isEmpty()
				&& r.getConstants().isEmpty();
	}

	/**
	 * can return true if there are not mappable
	 */
	@Override
	public boolean isMappable(Predicate father, Predicate son) {
		if (son.equals(father))
			return true;
		else
			return !getConditions(son, father).isEmpty();
	}

	@Override
	public LinkedList<Substitution> homomorphism(Atom father, Atom son, Substitution s) {
		LinkedList<Substitution> res = new LinkedList<Substitution>();
		Predicate predB = son.getPredicate();
		Predicate predH = father.getPredicate();
		List<IDCondition> conds = getConditions(predB, predH);
		for (IDCondition cond : conds) {
			if (cond.checkBody(son.getTerms())) {
				Substitution homo = cond.homomorphism(father.getTerms(), son.getTerms(), s);
				if (homo != null) {
					res.add(new TreeMapSubstitution(homo));
				}
			}
		}
		return res;
	}

	@Override
	public LinkedList<Partition<Term>> getUnification(Atom father, Atom son) {
		LinkedList<Partition<Term>> res = new LinkedList<Partition<Term>>();
		Predicate predB = son.getPredicate();
		Predicate predH = father.getPredicate();
		List<IDCondition> conds = getConditions(predB, predH);
		for (IDCondition cond : conds) {
			Partition<Term> unif = cond.generateUnification(son.getTerms(), father.getTerms());
			if (unif != null) {
				res.add(unif);
			}
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
	public Collection<Pair<Atom, Substitution>> getRewritingOf(Atom atom) {
		TreeSet<Pair<Atom, Substitution>> res = new TreeSet<Pair<Atom, Substitution>>();
		res.add(new ImmutablePair<Atom, Substitution>(atom, DefaultSubstitutionFactory.instance().createSubstitution()));

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
					Pair<List<Term>, Substitution> ret = cond.generateBody(atom.getTerms());
					if (ret != null) {
						List<Term> generatedBody = ret.getLeft();
						res.add(new ImmutablePair<Atom, Substitution>(new DefaultAtom(predB, generatedBody),
						                                              ret.getRight()));
					}
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
