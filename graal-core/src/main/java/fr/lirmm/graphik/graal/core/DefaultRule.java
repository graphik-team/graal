/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class DefaultRule implements Rule {

	private final String label;
	private final AtomSet body;
	private final AtomSet head;

	private Set<Term> frontier = null;
	private Set<Term> existentials = null;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DefaultRule() {
		this("", new LinkedListAtomSet(), new LinkedListAtomSet());
	}

	public DefaultRule(Iterable<Atom> body, Iterable<Atom> head) {
		this("", body, head);
	}

	public DefaultRule(String label, Iterable<Atom> body, Iterable<Atom> head) {
		this.label = label;
		AtomSet atomSet = new LinkedListAtomSet();
		try {
			atomSet.add(body);
		} catch (AtomSetException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		}
		this.body = atomSet;

		atomSet = new LinkedListAtomSet();
		try {
			atomSet.add(head);
		} catch (AtomSetException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		}
		this.head = atomSet;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.kb.core.Rule#getBody()
	 */
	@Override
	public AtomSet getBody() {
		return this.body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.kb.core.Rule#getLabel()
	 */
	@Override
	public String getLabel() {
		return this.label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.kb.core.Rule#getHead()
	 */
	@Override
	public AtomSet getHead() {
		return this.head;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.kb.core.Rule#getFrontier()
	 */
	@Override
	public Set<Term> getFrontier() {
		if (frontier == null) {
			this.computeFrontierAndExistentials();
		}

		return this.frontier;
	}

	@Override
	public Set<Term> getExistentials() {
		if (existentials == null) {
			this.computeFrontierAndExistentials();
		}

		return this.existentials;
	}

	@Override
	public int compareTo(Rule other) {
		return this.label.compareTo(other.getLabel());
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder(this.body.toString());
		builder.append(" -> ");
		builder.append(this.head);
		return builder.toString();
	}

	@Override
	public boolean equals(Object o) {
		boolean res;
		res = o != null && o instanceof Rule;
		if (res) {
			Rule r = (Rule) o;
			res = this.compareTo(r) == 0;
		}
		return res;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private void computeFrontierAndExistentials() {
		this.frontier = new TreeSet<Term>();
		this.existentials = new TreeSet<Term>();
		Collection<Term> body = this.getBody().getTerms(Type.VARIABLE);

		for (Term termHead : this.getHead().getTerms(Type.VARIABLE)) {
			boolean isExistential = true;
			for (Term termBody : body) {
				if (termBody.equals(termHead)) {
					this.frontier.add(termHead);
					isExistential = false;
				}
			}
			if (isExistential)
				this.existentials.add(termHead);
		}
	}

}
