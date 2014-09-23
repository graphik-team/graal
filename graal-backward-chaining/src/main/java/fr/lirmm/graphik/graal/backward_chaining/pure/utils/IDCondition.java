package fr.lirmm.graphik.graal.backward_chaining.pure.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;

/**
 * Code an ID rule
 * 
 * @author Mélanie KÖNIG
 * 
 */
public class IDCondition {

	private static DefaultFreeVarGen varGen = new DefaultFreeVarGen("X"
			+ Integer.toString(IDCondition.class.hashCode()));

	private int arity_body;
	private Partition<Integer> cond_body;
	private int[] cond_head;

	public IDCondition(List<Term> body, List<Term> head) {

		arity_body = body.size();

		// code the condition on the body terms
		cond_body = new Partition<Integer>();
		for (int i = 0; i < body.size(); i++)
			for (int j = i + 1; j < body.size(); j++)
				if (body.get(i).equals(body.get(j)))
					cond_body.add(i, j);

		// code the condition on the head terms
		cond_head = new int[head.size()];
		for (int j = 0; j < head.size(); j++) {
			boolean found = false;
			int i = 0;
			while (!found && i < body.size()) {
				if (body.get(i).equals(head.get(j))) {
					found = true;
					cond_head[j] = i;
				}
				i++;
			}
		}
	}

	/**
	 * array must have the good lenght i. e. condition on predicate arity have
	 * already been check
	 */
	public boolean check(List<Term> body, List<Term> head) {

		// check the condition on the body terms
		if (!checkBody(body))
			return false;
		;
		// check the condition on the head terms
		for (int k = 0; k < head.size(); k++) {
			if (!head.get(k).equals(body.get(cond_head[k])))
				return false;
		}
		return true;
	}

	/**
	 * Return true iff the given term fulfil the condition on the head term of
	 * this
	 */
	public boolean checkHead(List<Term> head) {
		for (int i = 0; i < cond_head.length; i++)
			for (int j = i + 1; j < cond_head.length; j++)
				if (cond_head[i] == cond_head[j])
					if (!head.get(i).equals(head.get(j)))
						return false;
		return true;
	}

	/**
	 * Return true iff the given term fulfil the condition on the body term of
	 * this
	 */
	public boolean checkBody(List<Term> body) {
		if (body.size() != arity_body)
			return false;
		// check the condition on the body terms
		Iterator<ArrayList<Integer>> i = cond_body.getClasses().iterator();
		while (i.hasNext()) {
			ArrayList<Integer> cl = i.next();
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
	public List<Term> getBody(List<Term> head) {
		List<Term> body = new ArrayList<Term>(arity_body);
		for (int i = 0; i < arity_body; i++)
			body.add(null);
		for (int i = 0; i < head.size(); i++) {
			body.set(cond_head[i], head.get(i));
		}
		for (int i = 0; i < arity_body; i++)
			if (body.get(i) == null)
				body.set(i, varGen.getFreeVar());
		return body;
	}

	/**
	 * Return the susbtitution that mapp the term of head into the term of body
	 * according to this body and father have to be checked by checkBody and
	 * checkHead
	 */
	public Substitution getSubstitution(List<Term> body, List<Term> head) {
		Substitution res = new TreeMapSubstitution();
		for (int i = 0; i < cond_head.length; i++) {
			res.put(head.get(i), body.get(cond_head[i]));
		}
		return res;
	}

	/**
	 * Return the partition that unify the term of head with the term of body
	 * according to this
	 */
	public TermPartition getUnification(List<Term> body, List<Term> head) {
		TermPartition res = new TermPartition();
		// put together term of body that must be unify according to this
		for (ArrayList<Integer> cl : cond_body) {
			int rep = cl.get(0);
			for (int i = 1; i < cl.size(); i++) {
				res.add(body.get(rep), body.get(i));
			}
		}
		// put term of head into the class of the corresponding term of body
		// according this
		for (int i = 0; i < cond_head.length; i++) {
			res.add(head.get(i), body.get(cond_head[i]));
		}
		return res;
	}

	@Override
	public String toString() {
		String s = "";
		s += cond_body.toString() + "\n";
		s += "[";
		for (Integer i : cond_head)
			s += " " + i;
		s += "]";
		return s;
	}

}
