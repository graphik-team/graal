package fr.lirmm.graphik.graal.backward_chaining.pure.rules;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.IDCondition;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.Misc;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.TermPartition;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.ruleset.IndexedByBodyPredicatesRuleSet;
import fr.lirmm.graphik.util.stream.ObjectWriter;

public class IDCompilation implements RulesCompilation {

	private static DefaultFreeVarGen varGen = new DefaultFreeVarGen("X"
			+ Integer.toString(IDCompilation.class.hashCode()));

	private TreeSet<Predicate> predicates = new TreeSet<Predicate>();

	// a matrix for store conditions ( p -> q : [q][p] )
	private Map<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>> conditions;

	private LinkedList<Rule> saturation = new LinkedList<Rule>();

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public IDCompilation() {
		super();
	}

	public IDCompilation(Iterable<Rule> ruleSet) {
		super();
		this.code(ruleSet);
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public LinkedList<IDCondition> getConditions(Predicate pred_b,
			Predicate pred_h) {
		LinkedList<IDCondition> res = null;
		if (pred_b.equals(pred_h)) {
			res = new LinkedList<IDCondition>();
			ArrayList<Term> terms = new ArrayList<Term>(pred_b.getArity());
			for (int i = 0; i < pred_h.getArity(); i++) {
				terms.add(varGen.getFreeVar());
			}
			res.add(new IDCondition(terms, terms));
		} else {
			Map<Predicate, LinkedList<IDCondition>> cond_h = conditions
					.get(pred_h);

			if (cond_h != null) {
				res = cond_h.get(pred_b);
			}
		}
		if (res != null)
			return res;
		else
			return new LinkedList<IDCondition>();
	}

	@Override
	public void code(Iterable<Rule> ruleSet) {
		this.saturation.clear();

		// initialise first rules in the saturation
		Rule r;
		Iterator<Rule> it = ruleSet.iterator();
		while (it.hasNext()) {
			r = it.next();
			if (this.isCompilable(r)) {
				this.saturation.add(r);
				it.remove();
			}
		}

		this.computeSaturation();

		// create IDCondition from the saturation
		this.createIDCondition();
	}

	/**
	 * Save the IDCompilation with a writer
	 * @param ruleWriter
	 * @throws IOException
	 */
	public void save(ObjectWriter<Rule> ruleWriter) throws IOException {
		ruleWriter.write(this.saturation);
	}

	/**
	 * load a ruleSet
	 * @param file
	 * @throws FileNotFoundException
	 */
	public void load(Iterable<Rule> saturation) throws FileNotFoundException {
		 this.saturation.clear();
		 for(Rule rule : saturation) {
			 this.saturation.add(rule);
		 }
	}

	private void createIDCondition() {
		Atom b;
		Atom h;
		this.conditions = new TreeMap<Predicate, TreeMap<Predicate, LinkedList<IDCondition>>>();
		for (Rule ru : this.saturation) {
			h = ru.getHead().iterator().next();
			b = ru.getBody().iterator().next();
			this.addCondition(b.getPredicate(), h.getPredicate(), b.getTerms(),
					h.getTerms());
		}
	}

	@Override
	public boolean isCompilable(Rule r) {
		Iterator<Atom> bodyIt = r.getBody().iterator();
		Iterator<Atom> headIt = r.getHead().iterator();

		if (bodyIt.hasNext() && headIt.hasNext()) {
			Atom b = bodyIt.next();
			Atom h = headIt.next();

			if (!bodyIt.hasNext() && !headIt.hasNext()) {
				// atomic head and body
				predicates.add(b.getPredicate());
				predicates.add(h.getPredicate());

				for (Term t : h.getTerms())
					if (t.isConstant() || r.getExistentials().contains(t))
						return false;
				return true;
			}
		}
		return false;
	}

	private void computeSaturation() {
		// FIXME pb à résoudre avec unification trop importante

		IndexedByBodyPredicatesRuleSet rules = new IndexedByBodyPredicatesRuleSet();
		for (Rule r : saturation) {
			rules.add(Misc.getSafeCopy(r));
		}

		Collection<Rule> lastCompute = new LinkedList<Rule>();
		lastCompute.addAll(saturation);

		while (!lastCompute.isEmpty()) {
			lastCompute = computeSaturationPart(lastCompute, rules);
			saturation.addAll(lastCompute);
		}
	}

	private Collection<Rule> computeSaturationPart(Iterable<Rule> lastCompute, IndexedByBodyPredicatesRuleSet rules) {
		Atom head1, body2;
		TermPartition part;
		AtomSet impliedHead, impliedBody;
		Rule impliedRule;

		LinkedList<Rule> tmp = new LinkedList<Rule>();

		for (Rule r1 : lastCompute) {
			head1 = r1.getHead().iterator().next();
			for (Rule r2 : rules.getRulesByBodyPredicate(head1
					.getPredicate())) {
				body2 = r2.getBody().iterator().next();
				if (head1.getPredicate().equals(body2.getPredicate())) {
					part = TermPartition.getPartitionByPosition(head1,
							body2);
					impliedBody = part.getAssociatedSubstitution(null)
							.getSubstitut(r1.getBody());
					impliedHead = part.getAssociatedSubstitution(null)
							.getSubstitut(r2.getHead());
					if (!impliedHead.equals(impliedBody)) {
						impliedRule = new DefaultRule(impliedBody,
								impliedHead);
						if (mustBeKeeped(impliedRule)) {
							tmp.add(impliedRule);
						}
					}
				}
			}
		}

		return tmp;
	}

	/**
	 * return true if saturation does not already contain a rule that implied
	 * the given one
	 */
	private boolean mustBeKeeped(Rule rule) {
		Iterator<Rule> it = saturation.iterator();
		Rule o;
		boolean isImplied = false;
		while (!isImplied && it.hasNext()) {
			o = it.next();
			if (Misc.imply(o, rule))
				isImplied = true;
		}
		return !isImplied;
	}

	private void addCondition(Predicate pred_b, Predicate pred_h, List<Term> b,
			List<Term> h) {

		Map<Predicate, LinkedList<IDCondition>> cond_h = this.conditions
				.get(pred_h);
		LinkedList<IDCondition> conds;
		if (cond_h != null) {
			conds = cond_h.get(pred_b);
			if (conds == null) {
				cond_h.put(pred_b, new LinkedList<IDCondition>());
				conds = cond_h.get(pred_b);
			}
		} else {
			conditions.put(pred_h,
					new TreeMap<Predicate, LinkedList<IDCondition>>());
			cond_h = conditions.get(pred_h);
			cond_h.put(pred_b, new LinkedList<IDCondition>());
			conds = cond_h.get(pred_b);
		}
		conds.add(new IDCondition(b, h));
	}

	/**
	 * can return true if there are not mappable
	 */
	@Override
	public boolean isMappable(Atom father, Atom son) {
		Predicate pred_b = son.getPredicate();
		Predicate pred_h = father.getPredicate();
		if (pred_b.equals(pred_h))
			return true;

		else
			return !getConditions(pred_b, pred_h).isEmpty();
	}

	@Override
	public LinkedList<Substitution> getMapping(Atom father, Atom son) {
		LinkedList<Substitution> res = new LinkedList<Substitution>();
		Predicate pred_b = son.getPredicate();
		Predicate pred_h = father.getPredicate();
		LinkedList<IDCondition> conds = getConditions(pred_b, pred_h);
		for (IDCondition cond : conds) {
			if (cond.checkBody(son.getTerms()))
				res.add(cond.getSubstitution(son.getTerms(), father.getTerms()));
		}
		return res;
	}

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
		Predicate pred_b = son.getPredicate();
		Predicate pred_h = father.getPredicate();
		LinkedList<IDCondition> conds = getConditions(pred_b, pred_h);
		for (IDCondition cond : conds) {
			res.add(cond.getUnification(son.getTerms(), father.getTerms()));
		}
		return res;
	}

	@Override
	public boolean isImplied(Atom father, Atom son) {
		Predicate pred_b = son.getPredicate();
		Predicate pred_h = father.getPredicate();
		LinkedList<IDCondition> conds = getConditions(pred_b, pred_h);
		for (IDCondition cond : conds)
			if (cond.check(son.getTerms(), father.getTerms()))
				return true;

		return false;
	}

	@Override
	public Collection<Predicate> getUnifiablePredicate(Predicate p) {
		Collection<Predicate> res = new LinkedList<Predicate>();
		Map<Predicate, LinkedList<IDCondition>> cond_h = conditions.get(p);
		res.add(p);
		if (cond_h != null)
			res.addAll(cond_h.keySet());

		return res;
	}

	/**
	 * Return all possible rewritings of this Atom by this compilation.
	 */
	@Override
	public Collection<Atom> getRewritingOf(Atom atom) {
		TreeSet<Atom> res = new TreeSet<Atom>();
		res.add(atom);

		Predicate pred_h = atom.getPredicate();
		Map<Predicate, LinkedList<IDCondition>> cond_h = this.conditions
				.get(pred_h);
		if (cond_h != null) {
			LinkedList<IDCondition> conds;
			Predicate pred_b;
			for (Map.Entry<Predicate, LinkedList<IDCondition>> entry : cond_h.entrySet()) {
				pred_b = entry.getKey();
				conds = entry.getValue();
				for (IDCondition cond : conds) {
					if (cond.checkHead(atom.getTerms()))
						res.add(new DefaultAtom(pred_b, cond.getBody(atom
								.getTerms())));
				}
			}
		}
		return res;
	}
}
