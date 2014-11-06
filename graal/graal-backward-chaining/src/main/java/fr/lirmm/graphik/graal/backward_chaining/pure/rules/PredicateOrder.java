package fr.lirmm.graphik.graal.backward_chaining.pure.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.TermPartition;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;

public class PredicateOrder extends AbstractRulesCompilation {

	// relies the Predicate and the index in the matrix order
	private HashMap<Predicate, Integer> predicateIndex;
	private ArrayList<Predicate> indexPredicate;

	// a matrix for code the order order[i][j] = 1 iff predicate(i) >
	// predicate(j)
	private byte[][] order;
	int size_order; // size of the tab order used by this

	// the list of the compiled rules
	private LinkedList<Rule> rules;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public PredicateOrder(Iterable<Rule> rules) {
		rules = new LinkedList<Rule>();
		for (Rule r : rules) {
			this.rules.add(r);
		}
		predicateIndex = new HashMap<Predicate, Integer>();
		size_order = 0;
	}

	// /////////////////////////////////////////////////////////////////////////
	// GETTERS / SETTERS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Iterable<Rule> getSaturation() {
		return this.rules;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public void compile() {
		Iterator<Rule> i = rules.iterator();
		Rule r;
		TreeSet<Predicate> set = new TreeSet<Predicate>();
		int nb_pred = 0;

		while (i.hasNext()) {
			r = i.next();
			if (isCompilable(r)) {
				i.remove();
				this.rules.add(r);
				// count the number of new pred in r
				try {
					for (Predicate p : r.getBody().getAllPredicates())
						if (set.add(p))
							nb_pred++;

					for (Predicate p : r.getHead().getAllPredicates())
						if (set.add(p))
							nb_pred++;
				} catch (AtomSetException e) {

				}
			}
		}

		if (this.getProfiler() != null)
			System.out.println("hierarchical rules: " + this.rules.size());
		Atom father;
		Atom son;
		// System.out.println("nb pred: "+nb_pred);
		set = null;
		System.gc();
		order = new byte[nb_pred][nb_pred];
		indexPredicate = new ArrayList<Predicate>(nb_pred);
		for (Rule ru : this.rules) {
			father = ru.getHead().iterator().next();
			son = ru.getBody().iterator().next();
			this.addRule(father, son);
		}
	}

	@Override
	public boolean isCompilable(Rule rule) {
		Iterator<Atom> headIt = rule.getHead().iterator();
		Iterator<Atom> bodyIt = rule.getBody().iterator();
		if (headIt.hasNext() && bodyIt.hasNext()) {
			Atom father = headIt.next();
			Atom son = bodyIt.next();
			if (!headIt.hasNext() && !bodyIt.hasNext()) {
				// the head and the body of the rule contain only one atom

				if (father.getPredicate().getArity() == son.getPredicate()
						.getArity()) {
					int j = 0;
					while (j < father.getPredicate().getArity()) {
						if (father.getTerm(j).isConstant()
								|| !father.getTerm(j).equals(son.getTerm(j)))
							return false;
						j++;
					}
					return true;
				}
			}
		}
		return false;
	}

	private void addRule(Atom father, Atom son) {
		Predicate pred_father = father.getPredicate();
		Predicate pred_son = son.getPredicate();
		if (predicateIndex.get(pred_father) == null)
			addPredicate(pred_father);
		if (predicateIndex.get(pred_son) == null)
			addPredicate(pred_son);
		Integer f = predicateIndex.get(pred_father);
		Integer s = predicateIndex.get(pred_son);
		order[f][s] = 1;
		computeTransitiveClosure(f, s);
	}

	/**
	 * update the transitive closure of the predicate order when the subsumption
	 * father > son has been added
	 */
	private void computeTransitiveClosure(int father, int son) {
		// compute new descendant
		for (int i = 0; i < size_order; i++) {
			// if son has descendant we add its in father
			if (order[son][i] == 1)
				order[father][i] = 1;
		}

		// actualize ancestor
		for (int j = 0; j < size_order; j++) {
			// if this is an ancestor of father
			if (order[j][father] == 1) {
				for (int i = 0; i < size_order; i++) {
					// we add descendant of father in it ancestor
					if (order[father][i] == 1)
						order[j][i] = 1;
				}
			}
		}
	}

	/**
	 * Add a new Predicate in the order
	 */
	private void addPredicate(Predicate p) {
		if (predicateIndex.get(p) == null) {
			predicateIndex.put(p, size_order);
			indexPredicate.add(p);
			size_order++;
		}
	}

	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < size_order; i++) {
			s += indexPredicate.get(i) + " | ";
			for (int j = 0; j < size_order; j++) {
				if (order[i][j] == 1)
					s += indexPredicate.get(j) + " ";
			}
			s += "\n";
		}
		return s;
	}

	// can answer true if there is no homomorphism
	@Override
	public boolean isMappable(Atom father, Atom son) {
		Predicate pred_father = father.getPredicate();
		Predicate pred_son = son.getPredicate();
		if (pred_son.equals(pred_father))
			return true;
		Integer f = predicateIndex.get(pred_father);
		Integer s = predicateIndex.get(pred_son);
		if (f != null && s != null)
			return order[f][s] == 1;
		return false;
	}

	@Override
	public Collection<Substitution> getMapping(Atom father, Atom son) {
		LinkedList<Substitution> res = new LinkedList<Substitution>();
		if (isMappable(father, son)) {
			Substitution sub = SubstitutionFactory.getInstance()
					.createSubstitution();
			Iterator<Term> fatherTermsIt = father.getTerms().iterator();
			Iterator<Term> sonTermsIt = son.getTerms().iterator();

			Term fatherTerm, sonTerm;
			while (fatherTermsIt.hasNext() && sonTermsIt.hasNext()) {
				fatherTerm = fatherTermsIt.next();
				sonTerm = sonTermsIt.next();
				if (sub.getSubstitute(fatherTerm).equals(fatherTerm))
					sub.put(fatherTerm, sonTerm);
				else if (!sub.getSubstitute(fatherTerm).equals(sonTerm))
					return res;
			}
			res.add(sub);
		}
		return res;
	}

	// can answer true if there is no unifier
	@Override
	public boolean isUnifiable(Atom father, Atom son) {
		Predicate pred_father = father.getPredicate();
		Predicate pred_son = son.getPredicate();
		if (pred_son.equals(pred_father))
			return true;
		Integer f = predicateIndex.get(pred_father);
		Integer s = predicateIndex.get(pred_son);
		if (f != null && s != null) {
			return order[f][s] == 1;
		}
		return false;
	}

	@Override
	public Collection<TermPartition> getUnification(Atom father, Atom son) {
		LinkedList<TermPartition> res = new LinkedList<TermPartition>();
		if (isUnifiable(father, son)) {
			TermPartition p = TermPartition.getPartitionByPosition(father, son);
			if (p != null)
				res.add(p);
		}
		return res;
	}

	@Override
	public boolean isImplied(Atom father, Atom son) {
		Predicate pred_father = father.getPredicate();
		Predicate pred_son = son.getPredicate();
		Integer f = predicateIndex.get(pred_father);
		Integer s = predicateIndex.get(pred_son);
		if (f != null && s != null && father.getTerms().equals(son.getTerms()))
			return order[f][s] == 1;
		else
			return false;
	}

	@Override
	public Collection<Predicate> getUnifiablePredicate(Predicate p) {
		LinkedList<Predicate> res = new LinkedList<Predicate>();
		res.add(p);

		Integer index = predicateIndex.get(p);
		if (index != null)
			for (int i = 0; i < size_order; i++) {
				if (order[index][i] == 1) {
					res.add(indexPredicate.get(i));
				}
			}
		return res;
	}

	@Override
	public Collection<Atom> getRewritingOf(Atom father) {
		LinkedList<Atom> res = new LinkedList<Atom>();
		res.add(father);

		Integer index = predicateIndex.get(father.getPredicate());
		if (index != null)
			for (int i = 0; i < size_order; i++) {
				if (order[index][i] == 1) {
					Atom a = new DefaultAtom(father);
					a.setPredicate(indexPredicate.get(i));
					res.add(a);
				}
			}

		return res;
	}

}
