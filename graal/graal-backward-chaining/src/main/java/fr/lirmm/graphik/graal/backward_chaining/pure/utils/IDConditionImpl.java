package fr.lirmm.graphik.graal.backward_chaining.pure.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
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
public class IDConditionImpl implements IDCondition {


	private static DefaultFreeVarGen varGen = new DefaultFreeVarGen("X"
			+ Integer.toString(IDConditionImpl.class.hashCode()));

	private int arityBody;
	private Partition<Integer> condBody;
	private int[] condHead;

	// //////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// //////////////////////////////////////////////////////////////////////////

	public IDConditionImpl(List<Term> body, List<Term> head) {

		arityBody = body.size();

		// code the condition on the body terms
		condBody = new Partition<Integer>();
		for (int i = 0; i < body.size(); i++)
			for (int j = i + 1; j < body.size(); j++)
				if (body.get(i).equals(body.get(j)))
					condBody.add(i, j);

		// code the condition on the head terms
		condHead = new int[head.size()];
		for (int j = 0; j < head.size(); j++) {
			boolean found = false;
			int i = 0;
			while (!found && i < body.size()) {
				if (body.get(i).equals(head.get(j))) {
					found = true;
					condHead[j] = i;
				}
				i++;
			}
		}
	}
	
	public IDConditionImpl(Partition<Integer> condBody, int arityBody, int[] condHead) {
		this.condBody = new Partition<Integer>(condBody);
		this.arityBody = arityBody;
		this.condHead = condHead;
	}

	// //////////////////////////////////////////////////////////////////////////
	// METHODS
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * array must have the good lenght i. e. condition on predicate arity have
	 * already been check
	 */
	@Override
	public boolean imply(List<Term> body, List<Term> head) {

		// check the condition on the body terms
		if (!checkBody(body))
			return false;
		;
		// check the condition on the head terms
		if (head.size() != condHead.length)
			return false;

		for (int k = 0; k < head.size(); k++) {
			if (!head.get(k).equals(body.get(condHead[k])))
				return false;
		}
		return true;
	}

	/**
	 * Return true iff the given term fulfills the condition on the head term of
	 * this
	 */
	@Override
	public boolean checkHead(List<Term> head) {
		for (int i = 0; i < condHead.length; i++)
			for (int j = i + 1; j < condHead.length; j++)
				if (condHead[i] == condHead[j])
					if (!head.get(i).equals(head.get(j)))
						return false;
		return true;
	}

	/**
	 * Return true iff the given term fulfills the condition on the body term of
	 * this
	 */
	@Override
	public boolean checkBody(List<Term> body) {
		if (body.size() != arityBody)
			return false;
		// check the condition on the body terms
		Iterator<ArrayList<Integer>> i = condBody.iterator();
		while (i.hasNext()) {
			List<Integer> cl = i.next();
			Term t = body.get(cl.get(0));
			for (Integer j : cl) {
				if (!t.equals(body.get(j)))
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
		List<Term> body = new ArrayList<Term>(arityBody);

		// initialize with fresh variable
		for (int i = 0; i < arityBody; i++)
			body.add(varGen.getFreeVar());

		// ensure equality in body
		for (Collection<Integer> eq : this.condBody) {
			Iterator<Integer> it = eq.iterator();
			if (it.hasNext()) {
				Term rep = body.get(it.next());
				while (it.hasNext()) {
					body.set(it.next(), rep);
				}
			}
		}

		// pick frontier variables from the head
		for (int i = 0; i < head.size(); i++) {
			Term t = body.get(condHead[i]);
			Term rep = head.get(i);
			for (int j = 0; j < this.arityBody; ++j) {
				if (t.equals(body.get(j))) {
					body.set(j, rep);
				}
			}

		}

		return body;
	}

	/**
	 * Return the partition that unify the term of head with the term of body
	 * according to this
	 */
	@Override
	public TermPartition generateUnification(List<Term> body, List<Term> head) {
		TermPartition res = new TermPartition();
		// put together term of body that must be unify according to this
		for (Collection<Integer> cl : condBody) {
			Iterator<Integer> it = cl.iterator();
			int rep = it.next();

			while (it.hasNext()) {
				res.add(body.get(rep), body.get(it.next()));
			}

		}
		// put term of head into the class of the corresponding term of body
		// according this
		for (int i = 0; i < condHead.length; i++) {
			res.add(head.get(i), body.get(condHead[i]));
		}
		return res;
	}

	@Override
	public IDCondition composeWith(IDCondition condition2) {
		if (condition2 instanceof IDConditionImpl) {
			return composeWith((IDConditionImpl) condition2);
		}
		return null;
	}

	public IDCondition composeWith(IDConditionImpl condition) {
		Partition<Integer> condBody = new Partition<Integer>(this.condBody);
		
		for (Collection<Integer> cl : condition.condBody) {
			Iterator<Integer> it = cl.iterator();
			int rep = this.condHead[it.next()];
			while (it.hasNext()) {
				condBody.add(rep, this.condHead[it.next()]);
			}
		}

		int[] condHead = new int[condition.condHead.length];
		for(int i=0; i<condHead.length; ++i) {
			condHead[i] = this.condHead[condition.condHead[i]];
		}
		
		return new IDConditionImpl(condBody, this.arityBody, condHead);
	}

	// //////////////////////////////////////////////////////////////////////////
	// OBJECT METHODS
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		String s = "(";
		s += condBody.toString() + "";
		s += "[";
		boolean isFirst = true;
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
		for (int i = 0; i < arityBody; i++)
			body.add(new Term("X" + i, Term.Type.VARIABLE));

		// ensure equality in body
		for (Collection<Integer> eq : this.condBody) {
			Iterator<Integer> it = eq.iterator();
			if (it.hasNext()) {
				Term rep = body.get(it.next());
				while (it.hasNext()) {
					body.set(it.next(), rep);
				}
			}
		}

		// pick frontier variables from the head
		for (int i = 0; i < this.condHead.length; i++) {
			head.add(body.get(this.condHead[i]));
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
		if (obj == null || !(obj instanceof IDConditionImpl)) {
			return false;
		}
		IDConditionImpl other = (IDConditionImpl) obj;

		// test head condition
		if (this.condHead.length != other.condHead.length) {
			return false;
		}
		LinkedList<Term> head = new LinkedList<Term>();
		for (int i = 0; i < this.condHead.length; ++i) {
			Collection<Integer> classs = this.condBody
					.getClass(this.condHead[i]);
			if (classs != null) {
				if (!classs.contains(other.condHead[i])) {
					return false;
				}
			} else if (other.condBody.getClass(other.condHead[i]) != null) {
				return false;
			}

		}

		// test body condition
		if (this.arityBody != other.arityBody) {
			return false;
		}

		for (int i = 0; i < this.condHead.length; ++i) {
			head.add(new Term("X" + this.condHead[i], Term.Type.VARIABLE));
		}

		List<Term> body1 = this.generateBody(head);
		List<Term> body2 = other.generateBody(head);

		Map<Term, Term> map = new TreeMap<Term, Term>();
		for (int i = 0; i < body1.size(); ++i) {
			Term x1 = body1.get(i);
			Term x2 = body2.get(i);
			if (!x1.equals(x2)) {
				if (head.contains(x1)) {
					return false;
				} else {
					// x1 is an generated variable
					Term xcheck = map.get(x1);
					if (xcheck == null) {
						map.put(x1, x2);
					} else if (!xcheck.equals(x2)) {
						return false;
					}
				}
			}
		}
		return true;
	}

}

