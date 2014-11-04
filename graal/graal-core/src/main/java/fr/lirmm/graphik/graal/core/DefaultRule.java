/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.util.EquivalentRelation;
import fr.lirmm.graphik.util.TreeMapEquivalentRelation;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class DefaultRule implements Rule {

	private String label;
	private final AtomSet body;
	private final AtomSet head;

	private Set<Term> terms = null;
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
			atomSet.addAll(body);
		} catch (AtomSetException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		}
		this.body = atomSet;

		atomSet = new LinkedListAtomSet();
		try {
			atomSet.addAll(head);
		} catch (AtomSetException e) {
			// TODO treat this exception
			e.printStackTrace();
			throw new Error("Untreated exception");
		}
		this.head = atomSet;
	}

	// copy constructor
	public DefaultRule(Rule rule) {
		this(rule.getLabel(), new LinkedListAtomSet(rule.getBody()),
				new LinkedListAtomSet(rule.getHead()));
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public AtomSet getBody() {
		return this.body;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public AtomSet getHead() {
		return this.head;
	}

	@Override
	public Set<Term> getTerms() {
		if(this.terms == null) {
			this.terms = new TreeSet<Term>();
			try {
				this.terms.addAll(this.getBody().getTerms());
				this.terms.addAll(this.getHead().getTerms());
			} catch (AtomSetException e) {}
		}
		return this.terms;
	}

	@Override
	public Set<Term> getTerms(Term.Type type) {
		Set<Term> terms = new TreeSet<Term>();
		terms.addAll(this.getBody().getTerms(type));
		terms.addAll(this.getHead().getTerms(type));
		return terms;
	}

	@Override
	public Set<Term> getFrontier() {
		if (this.frontier == null) {
			this.computeFrontierAndExistentials();
		}

		return this.frontier;
	}

	@Override
	public Set<Term> getExistentials() {
		if (this.existentials == null) {
			this.computeFrontierAndExistentials();
		}

		return this.existentials;
	}

	@Override
	public Collection<AtomSet> getPieces() {
		Set<Term> existentials = getExistentials();
		Collection<AtomSet> pieces = new LinkedList<AtomSet>();

		// compute equivalent classes
		EquivalentRelation<Term> classes = new TreeMapEquivalentRelation<Term>();
		for (Atom a : this.getHead()) {
			Term representant = null;
			for (Term t : a) {
				if (existentials.contains(t)) {
					if (representant == null)
						representant = t;
					else
						classes.mergeClasses(representant, t);
				}
			}
		}

		// init pieces for equivalent classes
		Map<Integer, AtomSet> tmpPieces = new TreeMap<Integer, AtomSet>();
		for (Term e : existentials) {
			if (tmpPieces.get(classes.getIdClass(e)) == null) {
				tmpPieces.put(classes.getIdClass(e), AtomSetFactory
						.getInstance().createAtomSet());
			}
		}

		// Affect atoms to one pieces
		boolean isAffected;
		AtomSet atomset;
		Term e;
		for (Atom a : this.getHead()) {
			isAffected = false;
			Iterator<Term> it = existentials.iterator();
			while (it.hasNext() && !isAffected) {
				e = it.next();
				if (a.getTerms().contains(e)) {
					tmpPieces.get(classes.getIdClass(e)).add(a);
					isAffected = true;
				}
			}
			if (!isAffected) { // does not contain existential variable
				atomset = AtomSetFactory.getInstance().createAtomSet();
				atomset.add(a);
				pieces.add(atomset);
			}
		}

		pieces.addAll(tmpPieces.values());

		return pieces;
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
		StringBuilder builder = new StringBuilder();
		if (!this.label.isEmpty()) {
			builder.append('[');
			builder.append(this.label);
			builder.append("] ");
		}
		builder.append(this.body.toString());
		builder.append(" -> ");
		builder.append(this.head);
		return builder.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Rule)) {
			return false;
		}
		return this.equals((Rule) obj);
	}

	public boolean equals(Rule other) {
		if(this.label.compareTo(other.getLabel()) != 0)
			return false;
		if(!other.getHead().equals(this.getHead()))
			return false;
		if(!other.getBody().equals(this.getBody()))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		return this.label.hashCode();
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
			if (isExistential) {
				this.existentials.add(termHead);
			}
		}
	}

}
