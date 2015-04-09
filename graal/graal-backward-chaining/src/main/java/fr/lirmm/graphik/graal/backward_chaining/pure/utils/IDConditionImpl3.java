package fr.lirmm.graphik.graal.backward_chaining.pure.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.util.Partition;

/**
 * Code an ID rule
 * 
 * @author Mélanie KÖNIG
 * 
 */
public class IDConditionImpl3 implements IDCondition {

	private int[] condBody;
	private int[] condHead;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	public IDConditionImpl3(int[] condBody, int[] condHead) {
		this.condBody = condBody;
		this.condHead = condHead;
	}

	public IDConditionImpl3(List<Term> body, List<Term> head) {

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
		List<Integer> list = new ArrayList(condBody.length);
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
			} else if (!body.get(i).equals(check[condBody[i]])) {
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
	 * Return true iff the given term fulfills the condition on the head term of
	 * this
	 */
	@Override
	public boolean checkHead(List<Term> head) {
		if (head.size() != condHead.length)
			return false;

		Term[] check = new Term[head.size()];
		for (int i = 0; i < condHead.length; i++) {
			if (check[condHead[i]] == null) {
				check[condHead[i]] = head.get(i);
			} else if (!check[condHead[i]].equals(head.get(i))) {
				return false;
			}
		}

		return true;
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

	/**
	 * Return the term of the body according to the given term of the head
	 */
	@Override
	public List<Term> generateBody(List<Term> head) {
		List<Term> body = new ArrayList<Term>(condBody.length);

		// initialize
		for (int i = 0; i < condBody.length; i++) {
			body.add(new Term("X" + condBody[i], Term.Type.VARIABLE));
		}

		// pick frontier variables from the head
		for (int i = 0; i < head.size(); i++) {
			Term rep = head.get(i);
			for (int j = 0; j < condBody.length; ++j) {
				if (condBody[j] == condHead[i]) {
					body.set(j, rep);
				}
			}

		}

		return body;
	}

	@Override
	public List<Term> generateHead() {
		List<Term> head = new LinkedList<Term>();
		for (int i = 0; i < this.condHead.length; ++i) {
			head.add(new Term("X" + this.condHead[i], Term.Type.VARIABLE));
		}
		return head;
	}

	/**
	 * Return the partition that unify the term of head with the term of body
	 * according to this
	 */
	@Override
	public TermPartition generateUnification(List<Term> body, List<Term> head) {
		TermPartition res = new TermPartition();
		Term[] map = new Term[body.size()];

		// put together term of body that must be unify according to this
		for (int i = 0; i < condBody.length; ++i) {
			if (map[condBody[i]] == null) {
				map[condBody[i]] = body.get(i);
			} else {
				res.add(map[condBody[i]], body.get(i));
			}
		}

		// put term of head into the class of the corresponding term of body
		// according this
		for (int i = 0; i < condHead.length; i++) {
			res.add(head.get(i), map[condHead[i]]);
		}
		return res;
	}

	@Override
	public IDCondition composeWith(IDCondition condition2) {
		if (condition2 instanceof IDConditionImpl3) {
			return composeWith((IDConditionImpl3) condition2);
		}
		return null;
	}

	public IDCondition composeWith(IDConditionImpl3 condition) {
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
			newCondHead[i] = partition
					.getRepresentant(condition.condHead[i] * 2 + 1);
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
		
		return new IDConditionImpl3(newCondBody, newCondHead);
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
			body.add(new Term("X" + condBody[i], Term.Type.VARIABLE));

		// pick frontier variables from the head
		for (int i = 0; i < this.condHead.length; i++) {
			head.add(new Term("X" + condHead[i], Term.Type.VARIABLE));
		}

		Rule r = new DefaultRule();
		r.getBody().add(new DefaultAtom(bodyPredicate, body));
		r.getHead().add(new DefaultAtom(headPredicate, head));

		return r;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || !(obj instanceof IDConditionImpl3)) {
			return false;
		}
		IDConditionImpl3 other = (IDConditionImpl3) obj;

		return Arrays.equals(this.condBody, other.condBody) &&
 Arrays.equals(this.condHead, other.condHead);
	}

}

