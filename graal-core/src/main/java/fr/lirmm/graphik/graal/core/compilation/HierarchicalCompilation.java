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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.util.Partition;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;

public class HierarchicalCompilation extends AbstractRulesCompilation {

	// relies the Predicate and the index in the matrix order
	private TreeMap<Predicate, Integer> predicateIndex;
	private ArrayList<Predicate> indexPredicate;

	// a matrix for code the order order[i][j] = 1 iff predicate(i) >=
	// predicate(j)
	private byte[][] order;
	int sizeOrder; // size of the tab order used by this

	// the list of the compiled rules
	private LinkedList<Rule> rules;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public HierarchicalCompilation() {
		this.rules = new LinkedList<Rule>();
		this.predicateIndex = new TreeMap<Predicate, Integer>();
		this.indexPredicate = new ArrayList<Predicate>();
		this.sizeOrder = 0;
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
	public void compile(Iterator<Rule> ruleset) {
		this.rules = extractCompilable(ruleset);
		this.computeIndex(this.rules);
	}

	@Override
	public void load(Iterator<Rule> ruleset, Iterator<Rule> compilation) {
		extractCompilable(ruleset); // compilable rules are removed
		while (compilation.hasNext()) {
			this.rules.add(compilation.next());
		}
		this.computeIndex(this.rules);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// CONST METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Return true if the specified rule is compilable.
	 */
	@Override
	public boolean isCompilable(Rule rule) {
		CloseableIteratorWithoutException<Atom> headIt = rule.getHead().iterator();
		CloseableIteratorWithoutException<Atom> bodyIt = rule.getBody().iterator();
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
	
	/**
	 *  can answer true if there is no homomorphism
	 */
	@Override
	public boolean isMappable(Predicate father, Predicate son) {
		Integer f = predicateIndex.get(father);
		Integer s = predicateIndex.get(son);
		if (f != null && s != null) {
			return order[f][s] == 1;
		} else {
			return son.equals(father);
		}
	}

	@Override
	public Collection<Substitution> homomorphism(Atom father, Atom son, Substitution s) {
		Set<Variable> fixedTerms = s.getTerms();
		LinkedList<Substitution> res = new LinkedList<Substitution>();
		if (isMappable(father.getPredicate(), son.getPredicate())) {
			Substitution sub = DefaultSubstitutionFactory.instance().createSubstitution();
			Iterator<Term> fatherTermsIt = father.getTerms().iterator();
			Iterator<Term> sonTermsIt = son.getTerms().iterator();

			Term fatherTerm, sonTerm;
			while (fatherTermsIt.hasNext() && sonTermsIt.hasNext()) {
				fatherTerm = fatherTermsIt.next();
				sonTerm = sonTermsIt.next();

				if (fatherTerm.isConstant() || fixedTerms.contains(fatherTerm)) {
					if (!s.createImageOf(fatherTerm).equals(sonTerm)) {
						return res;
					}
				} else if (!sub.getTerms().contains(fatherTerm))
					sub.put((Variable) fatherTerm, sonTerm);
				else if (!sub.createImageOf(fatherTerm).equals(sonTerm))
					return res;
			}
			res.add(sub);
		}
		return res;
	}

	@Override
	public Collection<Partition<Term>> getUnification(Atom father, Atom son) {
		LinkedList<Partition<Term>> res = new LinkedList<Partition<Term>>();
		if (isMappable(father.getPredicate(), son.getPredicate())) {
			res.add(new Partition<Term>(father.getTerms(), son.getTerms()));
		}
		return res;
	}

	@Override
	public boolean isImplied(Atom father, Atom son) {
		Predicate predFather = father.getPredicate();
		Predicate predSon = son.getPredicate();
		Integer f = predicateIndex.get(predFather);
		Integer s = predicateIndex.get(predSon);
		if (f != null && s != null && Objects.equals(father.getTerms(), son.getTerms()))
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
			for (int i = 0; i < sizeOrder; i++) {
				if (order[index][i] == 1) {
					res.add(indexPredicate.get(i));
				}
			}
		return res;
	}

	@Override
	public Collection<Pair<Atom, Substitution>> getRewritingOf(Atom father) {
		LinkedList<Pair<Atom, Substitution>> res = new LinkedList<Pair<Atom, Substitution>>();
		res.add(new ImmutablePair<Atom, Substitution>(father, Substitutions.emptySubstitution()));

		Integer index = predicateIndex.get(father.getPredicate());
		if (index != null)
			for (int i = 0; i < sizeOrder; i++) {
				if (order[index][i] == 1) {
					Atom a = new DefaultAtom(father);
					a.setPredicate(indexPredicate.get(i));
					res.add(new ImmutablePair<Atom, Substitution>(a, Substitutions.emptySubstitution()));
				}
			}

		return res;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * update the transitive closure of the predicate order when the subsumption
	 * father > son has been added
	 */
	private void computeTransitiveClosure(int father, int son) {
		// compute new descendant
		for (int i = 0; i < sizeOrder; i++) {
			// if son has descendant we add its in father
			if (order[son][i] == 1)
				order[father][i] = 1;
		}

		// actualize ancestor
		for (int j = 0; j < sizeOrder; j++) {
			// if this is an ancestor of father
			if (order[j][father] == 1) {
				for (int i = 0; i < sizeOrder; i++) {
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
	private boolean addPredicate(Predicate p) {
		if (predicateIndex.get(p) == null) {
			predicateIndex.put(p, sizeOrder++);
			indexPredicate.add(p);
			return true;
		} else {
			return false;
		}
	}
	
	private void addRule(Atom father, Atom son) {
		Predicate predFather = father.getPredicate();
		Predicate predSon = son.getPredicate();

		Integer f = predicateIndex.get(predFather);
		Integer s = predicateIndex.get(predSon);
		order[f][s] = 1;
		computeTransitiveClosure(f, s);
	}
	
	private void computeIndex(Iterable<Rule> ruleset) {
		

		for (Rule rule : ruleset) {
			// count the number of new pred in r
			CloseableIteratorWithoutException<Predicate> it = rule.getBody().predicatesIterator();
			while (it.hasNext()) {
				this.addPredicate(it.next());
			}

			it = rule.getHead().predicatesIterator();
			while (it.hasNext()) {
				this.addPredicate(it.next());
			}
		}
		
		int nbPred = this.indexPredicate.size();

		Atom father, son;
		this.order = new byte[nbPred][nbPred];
		for (Rule ru : ruleset) {
			father = ru.getHead().iterator().next();
			son = ru.getBody().iterator().next();
			this.addRule(father, son);
		}

		// reflexivity
		for (int i = 0; i < this.order.length; ++i) {
			this.order[i][i] = 1;
		}
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < sizeOrder; i++) {
			s += indexPredicate.get(i) + " | ";
			for (int j = 0; j < sizeOrder; j++) {
				if (order[i][j] == 1)
					s += indexPredicate.get(j) + " ";
			}
			s += "\n";
		}
		return s;
	}

}
