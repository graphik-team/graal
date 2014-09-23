package fr.lirmm.graphik.graal.backward_chaining.pure.rules;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

public class IDCompilation implements RulesCompilation {

	private static DefaultFreeVarGen varGen = new DefaultFreeVarGen("X"
			+ Integer.toString(IDCompilation.class.hashCode()));

	TreeSet<Predicate> pred = new TreeSet<Predicate>();
	int nb_pred = 0;

	// a matrix for store conditions ( p -> q : [q][p] )
	private HashMap<Predicate, HashMap<Predicate, LinkedList<IDCondition>>> conditions;

	private LinkedList<Rule> saturation = new LinkedList<Rule>();

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
			HashMap<Predicate, LinkedList<IDCondition>> cond_h = conditions
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
	public void code(Iterable<Rule> list, String rule_name) {
		saturation.clear();
		// initialise first rules in the saturation
		Iterator<Rule> it = list.iterator();
		Rule r;
		while (it.hasNext()) {
			r = it.next();
			if (isCompilable(r)) {
				saturation.add(Misc.getSafeCopy(r));
				it.remove();
			}
		}
		try {
			File save = new File(rule_name + ".save");
			if (!save.createNewFile()) {// the save file for the saturation of
										// this set of rule exists
				loadSaturation(rule_name);
			} else {
				// compute the saturation and save it
				computeSaturation();
				// create file to save the saturation
				FileWriter file = new FileWriter(save);
				Atom b;
				Atom h;
				for (Rule ru : saturation) {
					h = ru.getHead().iterator().next();
					b = ru.getBody().iterator().next();
					file.write(h + ":-" + b + ".\n");
					file.flush();
				}
			}
		} catch (Exception e) {
			System.err.println("Problem to save the saturation");
		}
		// create IDCondition from the saturation
		createIDCondition();
	}

	public void loadSaturation(String rule_name) throws FileNotFoundException {
		// load the rule saved in the saturation backup file
		// saturation.clear();
		// DlgpParser parser = new DlgpParser();
		// parser.parse(new FileReader(rule_name+".save"));
		// saturation.addAll(parser.rules);
		throw new Error("loadSaturation not reimplemented");
	}

	private void createIDCondition() {
		Atom b;
		Atom h;
		conditions = new HashMap<Predicate, HashMap<Predicate, LinkedList<IDCondition>>>(
				nb_pred);
		for (Rule ru : saturation) {
			h = ru.getHead().iterator().next();
			b = ru.getBody().iterator().next();
			addCondition(b.getPredicate(), h.getPredicate(), b.getTerms(),
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
				if (pred.add(b.getPredicate()))
					nb_pred++;
				if (pred.add(h.getPredicate()))
					nb_pred++;
				for (Term t : h.getTerms())
					if (t.isConstant() || r.getExistentials().contains(t))
						return false;
				return true;
			}
		}
		return false;
	}

	private void computeSaturation() {
		IndexedByBodyPredicatesRuleSet rules = new IndexedByBodyPredicatesRuleSet();
		for (Rule r : saturation) {
			rules.add(Misc.getSafeCopy(r));
		}

		// TODO pb a résoudre avec unification trop importante
		LinkedList<Rule> lastCompute = new LinkedList<Rule>();
		LinkedList<Rule> tmp = new LinkedList<Rule>();
		lastCompute.addAll(saturation);
		Atom head1;
		Atom body2;
		TermPartition part;
		AtomSet impliedHead;
		AtomSet impliedBody;
		Rule impliedRule;
		while (!lastCompute.isEmpty()) {
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
								saturation.add(impliedRule);
							}
						}
					}
				}
			}
			lastCompute = tmp;
			tmp = new LinkedList<Rule>();
		}
	}

	/**
	 * return true if saturation does not already contain a rule that implied
	 * the given one
	 */
	private boolean mustBeKeeped(Rule r) {
		Iterator<Rule> i = saturation.iterator();
		Rule o;
		boolean isImplied = false;
		while (!isImplied && i.hasNext()) {
			o = i.next();
			if (Misc.equivalent(o, r))
				isImplied = true;
		}
		return !isImplied;
	}

	private void addCondition(Predicate pred_b, Predicate pred_h, List<Term> b,
			List<Term> h) {

		HashMap<Predicate, LinkedList<IDCondition>> cond_h = conditions
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
					new HashMap<Predicate, LinkedList<IDCondition>>(12));
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
		HashMap<Predicate, LinkedList<IDCondition>> cond_h = conditions.get(p);
		res.add(p);
		if (cond_h != null)
			res.addAll(cond_h.keySet());

		return res;
	}

	@Override
	public Collection<Atom> getRewritingOf(Atom father) {
		TreeSet<Atom> res = new TreeSet<Atom>();
		res.add(father);

		Predicate pred_h = father.getPredicate();
		HashMap<Predicate, LinkedList<IDCondition>> cond_h = conditions
				.get(pred_h);
		LinkedList<IDCondition> conds;
		if (cond_h != null) {
			for (Predicate pred_b : cond_h.keySet()) {
				conds = cond_h.get(pred_b);
				for (IDCondition cond : conds) {
					if (cond.checkHead(father.getTerms()))
						res.add(new DefaultAtom(pred_b, cond.getBody(father
								.getTerms())));
				}
			}
		}
		return res;
	}

}
