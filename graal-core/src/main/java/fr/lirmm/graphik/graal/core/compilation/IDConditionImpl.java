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
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Substitutions;
import fr.lirmm.graphik.graal.core.factory.DefaultRuleFactory;
import fr.lirmm.graphik.graal.core.factory.DefaultSubstitutionFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.MethodNotImplementedError;
import fr.lirmm.graphik.util.Partition;

/**
 * Code an ID rule
 * 
 * @author Mélanie KÖNIG
 * 
 */
class IDConditionImpl implements IDCondition {

	private int[] condBody;
	private int[] condHead;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	public IDConditionImpl(int[] condBody, int[] condHead) {
		this.condBody = condBody;
		this.condHead = condHead;
	}

	public IDConditionImpl(List<Term> body, List<Term> head) {

		// code the condition on the body terms
		condBody = new int[body.size()];
		int var = -1;
		for (int i = 0; i < body.size(); ++i) {
			condBody[i] = -1;
			for (int j = 0; j < i; ++j) {
				if (body.get(i).equals(body.get(j))) {
					condBody[i] = condBody[j];
				}
			}
			if (condBody[i] == -1) {
				condBody[i] = ++var;
			}
		}

		// code the condition on the head terms
		condHead = new int[head.size()];
		for (int j = 0; j < head.size(); j++) {
			boolean found = false;
			int i = 0;
			while (!found && i < body.size()) {
				if (body.get(i).equals(head.get(j))) {
					found = true;
					condHead[j] = condBody[i];
				}
				i++;
			}
		}
	}

	// //////////////////////////////////////////////////////////////////////////
	// METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public List<Integer> getBody() {
		List<Integer> list = new ArrayList<Integer>(condBody.length);
		for (int i = 0; i < condBody.length; ++i) {
			list.add(condBody[i]);
		}
		return list;
	}

	/**
	 * array must have the good length i. e. condition on predicate arity have
	 * already been check
	 */
	@Override
	public boolean imply(List<Term> body, List<Term> head) {

		// check the condition on the body terms
		if (body.size() != condBody.length)
			return false;

		Term[] check = new Term[body.size()];
		for (int i = 0; i < condBody.length; i++) {
			if (check[condBody[i]] == null) {
				check[condBody[i]] = body.get(i);
			} else if (!Objects.equals(body.get(i), check[condBody[i]])) {
				return false;
			}
		}

		// check the condition on the head terms
		if (head.size() != condHead.length)
			return false;

		for (int i = 0; i < head.size(); i++) {
			if (!head.get(i).equals(check[condHead[i]]))
				return false;
		}
		return true;
	}

	@Override
	public boolean isIdentity() {
		return Arrays.equals(condBody, condHead);
	}

	/**
	 * Return true iff the given term fulfills the condition on the body term of
	 * this
	 */
	@Override
	public boolean checkBody(List<Term> body) {
		if (body.size() != condBody.length)
			return false;

		Term[] check = new Term[body.size()];
		for (int i = 0; i < condBody.length; i++) {
			if (check[condBody[i]] == null) {
				check[condBody[i]] = body.get(i);
			} else if (!check[condBody[i]].equals(body.get(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Pair<List<Term>, Substitution> generateBody(List<Term> head) {
		Substitution s = DefaultSubstitutionFactory.instance().createSubstitution();
		Set<Variable> toRemoveFromPartition = new TreeSet<Variable>();

		for (int i = 0; i < condHead.length; i++) {
			Variable v = DefaultTermFactory.instance().createVariable(condHead[i]);
			toRemoveFromPartition.add(v);
			if (!s.aggregate(v, head.get(i))) {
				return null;
			}
		}

		List<Term> body = new ArrayList<Term>(condBody.length);
		for (int i = 0; i < condBody.length; i++) {
			Variable v = DefaultTermFactory.instance().createVariable(condBody[i]);
			toRemoveFromPartition.add(v);
			body.add(s.createImageOf(v));
		}

		for (Variable v : toRemoveFromPartition) {
			s.remove(v);
		}

		return new ImmutablePair<List<Term>, Substitution>(body, s);
	}

	@Override
	public List<Term> generateHead() {
		List<Term> head = new LinkedList<Term>();
		for (int i = 0; i < this.condHead.length; ++i) {
			head.add(DefaultTermFactory.instance().createVariable("X" + this.condHead[i]));
		}
		return head;
	}

	/**
	 * Return the partition that unify the term of head with the term of body
	 * according to this. This method returns null the unification is not
	 * possible (two constants in the same partition class)
	 * 
	 */

	@Override
	public Partition<Term> generateUnification(List<Term> body, List<Term> head) {
		Partition<Term> res = new Partition<Term>();
		Term[] map = new Term[body.size()];

		// put together term of body that must be unify according to this
		for (int i = 0; i < condBody.length; ++i) {
			Term t = body.get(i);
			if (map[condBody[i]] == null) {
				map[condBody[i]] = t;
			} else {
				res.add(map[condBody[i]], t);
			}
		}

		// put term of head into the class of the corresponding term of body
		// according this
		for (int i = 0; i < condHead.length; i++) {
			Term t = head.get(i);
			res.add(map[condHead[i]], t);
		}

		// check validity (does not contains two different constants)
		for (ArrayList<Term> classs : res) {
			Term cst = null;
			for (Term t : classs) {
				if (t.isConstant()) {
					if (cst == null)
						cst = t;
					else if (!cst.equals(t))
						return null;
				}
			}
		}
		return res;
	}
	
	@Override
	public Substitution homomorphism(List<Term> head, List<Term> to) {
		return this.homomorphism(head, to, Substitutions.emptySubstitution());
	}

	@Override
	public Substitution homomorphism(List<Term> head, List<Term> to, Substitution initialSub) {
		if (!checkBody(to)) {
			return null;
		}
		Set<Variable> fixedTerms = initialSub.getTerms();

		Pair<List<Term>, Substitution> ret = this.generateBody(head);
		if (ret == null) {
			return null;
		}
		Substitution s = ret.getRight();
		Substitution homo = DefaultSubstitutionFactory.instance().createSubstitution();
		List<Term> generatedBody = ret.getLeft();

		// check for a simple homomorphism from generated body into 'to'
		Iterator<Term> itFrom = generatedBody.iterator();
		Iterator<Term> itTo = to.iterator();
		while (itFrom.hasNext() && itTo.hasNext()) {
			Term termFrom = itFrom.next();
			Term termTo = itTo.next();
			if (termFrom.isConstant() || fixedTerms.contains(termFrom)) {
				if (!initialSub.createImageOf(termFrom).equals(termTo)) {
					return null;
				}
			} else {
				if (!homo.put((Variable) termFrom, termTo)) {
					return null;
				}
			}
		}
		if (itFrom.hasNext() || itTo.hasNext()) {
			throw new Error("Wrong term number");
		}

		// apply homo found over Substitution s from generateBody and add it to
		// homo
		for (Variable t : s.getTerms()) {
			homo.put(t, homo.createImageOf(s.createImageOf(t)));
		}

		return homo;
	}

	@Override
	public IDCondition composeWith(IDCondition condition2) {
		if (condition2 instanceof IDConditionImpl) {
			return composeWith((IDConditionImpl) condition2);
		}
		throw new MethodNotImplementedError();
	}

	public IDCondition composeWith(IDConditionImpl condition) {
		int[] newCondBody = new int[this.condBody.length];
		int[] newCondHead = new int[condition.condHead.length];

		// generate a partition representing variables to unify
		Partition<Integer> partition = new Partition<Integer>();
		for (int i = 0; i < this.condHead.length; ++i) {
			partition.add(this.condHead[i] * 2, condition.condBody[i] * 2 + 1);
		}

		// generate new body
		for (int i = 0; i < newCondBody.length; ++i) {
			newCondBody[i] = partition.getRepresentant(this.condBody[i] * 2);
		}

		// generate new head
		for (int i = 0; i < newCondHead.length; ++i) {
			newCondHead[i] = partition.getRepresentant(condition.condHead[i] * 2 + 1);
		}

		// normalize index
		int var = -1;
		int[] map = new int[newCondBody.length * 2 + 1];
		for (int i = 0; i < map.length; ++i) {
			map[i] = -1;
		}
		for (int i = 0; i < newCondBody.length; ++i) {
			if (map[newCondBody[i]] == -1) {
				map[newCondBody[i]] = ++var;
			}
			newCondBody[i] = map[newCondBody[i]];
		}
		for (int i = 0; i < newCondHead.length; ++i) {
			newCondHead[i] = map[newCondHead[i]];
		}

		return new IDConditionImpl(newCondBody, newCondHead);
	}

	// //////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		boolean isFirst = true;
		String s = "([";
		for (int i = 0; i < condBody.length; i++) {
			if (isFirst) {
				isFirst = false;
			} else {
				s += " ";
			}
			s += condBody[i];
		}
		s += "] -> [";
		isFirst = true;
		for (Integer i : condHead) {
			if (isFirst) {
				isFirst = false;
			} else {
				s += " ";
			}
			s += i;
		}
		s += "])";
		return s;
	}

	@Override
	public Rule generateRule(Predicate bodyPredicate, Predicate headPredicate) {
		List<Term> body = new ArrayList<Term>();
		List<Term> head = new LinkedList<Term>();

		// initialize body with fresh variable
		for (int i = 0; i < condBody.length; i++)
			body.add(DefaultTermFactory.instance().createVariable("X" + condBody[i]));

		// pick frontier variables from the head
		for (int i = 0; i < this.condHead.length; i++) {
			head.add(DefaultTermFactory.instance().createVariable("X" + condHead[i]));
		}

		Rule r = DefaultRuleFactory.instance().create();
		r.getBody().add(new DefaultAtom(bodyPredicate, body));
		r.getHead().add(new DefaultAtom(headPredicate, head));

		return r;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof IDConditionImpl)) {
			return false;
		}
		IDConditionImpl other = (IDConditionImpl) obj;

		return Arrays.equals(this.condBody, other.condBody) && Arrays.equals(this.condHead, other.condHead);
	}

}
