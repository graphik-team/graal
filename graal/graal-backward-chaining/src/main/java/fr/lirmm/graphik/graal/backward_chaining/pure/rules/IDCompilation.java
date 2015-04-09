package fr.lirmm.graphik.graal.backward_chaining.pure.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.IDCondition;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.IDConditionImpl;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.TermPartition;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.RuleUtils;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;

public class IDCompilation extends AbstractRulesCompilation {

	private static DefaultFreeVarGen varGen = new DefaultFreeVarGen("X"
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
		// p -> q
		Predicate p, q;
		for (Map.Entry<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> e : this.conditions
				.entrySet()) {
			q = e.getKey();
			for (Map.Entry<Predicate, LinkedList<IDCondition>> map : e
					.getValue().entrySet()) {
				p = map.getKey();
				for (IDCondition conditionPQ : map.getValue()) {
					saturation.add(conditionPQ.generateRule(p, q));
				}
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
		this.createIDCondition(saturation);

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
				terms.add(varGen.getFreeVar());
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
						if(r.equals(q)) {
							// filter trivial implication - p(x,y,z) -> p(x,y,z)
							Rule rule = conditionRQ.generateRule(r, q);
							List<Term> a = rule.getBody().iterator().next()
									.getTerms();
							List<Term> b = rule.getHead().iterator().next()
									.getTerms();
							if (!a.equals(b)) {
								if (addCondition(r, q, conditionRQ,
										this.conditions)) {
									this.computeSaturation(conditionsTmp, r, q,
											conditionRQ);
								}
							}
						} else if (addCondition(r, q, conditionRQ,
								this.conditions)) {
							this.computeSaturation(conditionsTmp, r, q,
									conditionRQ);
						}
					}
				}
			}
		}
	}

	private Collection<Rule> compactSaturation(Iterator<Rule> rules) {
		TreeMap<Atom, Rule> map = new TreeMap<Atom, Rule>();
		Rule rule, tmp;
		Atom atomicBody;
		while (rules.hasNext()) {
			rule = rules.next();
			atomicBody = rule.getBody().iterator().next();
			tmp = map.get(atomicBody);
			if (tmp == null) {
				map.put(atomicBody, rule);
			} else {
				tmp.getHead().addAll(rule.getHead());
			}
		}
		return map.values();
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
