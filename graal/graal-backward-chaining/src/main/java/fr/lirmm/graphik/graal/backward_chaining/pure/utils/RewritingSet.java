package fr.lirmm.graphik.graal.backward_chaining.pure.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;

public class RewritingSet implements Iterable<ConjunctiveQuery>,
		Collection<ConjunctiveQuery> {

	private HashMap<String, LinkedList<ConjunctiveQuery>> queries;
	Iterator<ConjunctiveQuery> first;

	public RewritingSet() {
		queries = new HashMap<String, LinkedList<ConjunctiveQuery>>();
	}

	public boolean add(ConjunctiveQuery q) {
		TreeSet<Predicate> set = new TreeSet<Predicate>();
		try {
			for (Predicate predicate : q.getAtomSet().getAllPredicates()) {
				set.add(predicate);
			}
		} catch (AtomSetException e) {
		}
		String key = set.toString();
		if (queries.get(key) == null)
			queries.put(key, new LinkedList<ConjunctiveQuery>());
		queries.get(key).add(q);
		return true;
	}

	/**
	 * Return all the queries that can be more general than q (i.e. it can exist
	 * an homomorphism from these to q)
	 */
	public LinkedList<ConjunctiveQuery> getComparableQueries(
			ConjunctiveQuery q, RulesCompilation comp) {
		TreeSet<Predicate> set = new TreeSet<Predicate>();
		PureQuery cop = new PureQuery(q);
		cop.removeAnswerPredicate();
		try {
			for (Predicate predicate : cop.getAtomSet().getAllPredicates()) {
				set.add(predicate);
			}
		} catch (AtomSetException e) {
		}
		LinkedList<Predicate> preds = new LinkedList<Predicate>();
		preds.addAll(set);
		// taking account of compiled rules
		if (comp != null) {
			for (Predicate p : preds)
				set.addAll(comp.getUnifiablePredicate(p));
			preds.clear();
			preds.addAll(set);
		}

		// compute all the parts of the predicate set
		Iterator<Predicate> itr = preds.iterator();
		LinkedList<Predicate> p = new LinkedList<Predicate>();
		LinkedList<Collection<Predicate>> parts = new LinkedList<Collection<Predicate>>();
		while (itr.hasNext()) {
			p.clear();
			p.add(itr.next());
			itr.remove();
			addToAll(p, preds, parts);
		}
		// for each parts of the predicats set we compute the key for the map
		LinkedList<String> keys = new LinkedList<String>();
		for (Collection<Predicate> list : parts) {
			set.clear();
			set.addAll(list);
			keys.add(set.toString());
		}
		// for each key we take the associated set of query
		LinkedList<ConjunctiveQuery> res = new LinkedList<ConjunctiveQuery>();
		for (String key : keys) {
			if (queries.get(key) != null)
				res.addAll(queries.get(key));
		}
		return res;
	}

	private void addToAll(LinkedList<Predicate> p, LinkedList<Predicate> preds,
			LinkedList<Collection<Predicate>> res) {

		if (preds.isEmpty()) {
			res.add((Collection<Predicate>) p.clone());
		} else {
			LinkedList<Predicate> copy = (LinkedList<Predicate>) preds.clone();
			Predicate first = copy.getFirst();
			copy.removeFirst();

			// part without first
			addToAll((LinkedList<Predicate>) p.clone(), copy, res);

			p.add(first);
			// part with first
			addToAll(p, copy, res);

		}
	}

	public Iterator<ConjunctiveQuery> iterator() {
		// return new RewritingSetIterator(queries.values());
		throw new Error("RewritingSetIterator: class not found.");
	}

	public int size() {
		int i = 0;
		for (Collection<ConjunctiveQuery> l : queries.values())
			i += l.size();
		return i;
	}

	public boolean isEmpty() {
		return iterator().hasNext();
	}

	public void addAll(RewritingSet toAdd) {
		for (String key : toAdd.queries.keySet()) {
			if (this.queries.get(key) == null) {
				this.queries.put(key, toAdd.queries.get(key));
			} else
				this.queries.get(key).addAll(toAdd.queries.get(key));
		}
	}

	public ConjunctiveQuery getFirst() {
		first = this.iterator();
		return first.next();
	}

	public void removeFirst() {
		first = this.iterator();
		first.next();
		first.remove();
	}

	@Override
	public boolean addAll(Collection<? extends ConjunctiveQuery> toAdd) {
		for (ConjunctiveQuery q : toAdd)
			this.add(q);
		return true;
	}

	@Override
	public void clear() {
		queries.clear();
	}

	@Override
	public boolean contains(Object o) {
		boolean found = false;
		Iterator<ConjunctiveQuery> i = iterator();
		ConjunctiveQuery q;
		while (!found && i.hasNext()) {
			q = i.next();
			if (q.equals(o)) {
				found = true;
			}
		}
		return found;
	}

	@Override
	public boolean containsAll(Collection<?> c) {

		boolean present = true;
		Iterator<?> i = c.iterator();
		Object q;
		while (present && i.hasNext()) {
			q = i.next();
			if (!this.contains(q)) {
				present = false;
			}
		}
		return present;
	}

	@Override
	public boolean remove(Object o) {
		boolean found = false;
		Iterator<ConjunctiveQuery> i = iterator();
		ConjunctiveQuery q;
		while (!found && i.hasNext()) {
			q = i.next();
			if (q.equals(o)) {
				i.remove();
				found = true;
			}
		}
		return found;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean present = true;
		Iterator<?> i = c.iterator();
		Object q;
		while (present && i.hasNext()) {
			q = i.next();
			if (!this.remove(q)) {
				present = false;
			}
		}
		return present;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean present = true;
		Iterator<?> i = this.iterator();
		Object q;
		while (present && i.hasNext()) {
			q = i.next();
			if (!c.contains(q)) {
				i.remove();
				present = false;
			}
		}
		return present;
	}

	@Override
	public Object[] toArray() {
		return queries.values().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return queries.values().toArray(a);
	}

}
