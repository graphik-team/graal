/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.term.Term;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public abstract class AbstractSubstitution implements Substitution {

	protected abstract Map<Term, Term> getMap();

	@Override
	public Set<Term> getTerms() {
		return this.getMap().keySet();
	}

	@Override
	public Set<Term> getValues() {
		return new TreeSet<Term>(this.getMap().values());
	}

	@Override
	public Term createImageOf(Term term) {
		Term substitut = this.getMap().get(term);
		return (substitut == null) ? term : substitut;
	}

	@Override
	public boolean put(Term term, Term substitut) {
		if (term.isConstant() && substitut.isConstant()
				&& !term.equals(substitut)) {
			return false;
		}
		this.getMap().put(term, substitut);
		return true;
	}

	@Override
	public void put(Substitution substitution) {
		for (Term term : substitution.getTerms()) {
			this.put(term, substitution.createImageOf(term));
		}
	}

	@Override
	public Atom createImageOf(Atom atom) {
		List<Term> termsSubstitut = new LinkedList<Term>();
		for (Term term : atom.getTerms())
			termsSubstitut.add(this.createImageOf(term));

		return new DefaultAtom(atom.getPredicate(), termsSubstitut);
	}

	@Override
	public InMemoryAtomSet createImageOf(AtomSet src) {
		InMemoryAtomSet dest = AtomSetFactory.getInstance().createAtomSet();
		this.apply(src, dest);
		return dest;
	}

	@Override
	public void apply(AtomSet src, AtomSet dest) throws AtomSetException {
		for (Atom a : src) {
			dest.add(this.createImageOf(a));
		}
	}

	@Override
	public void apply(AtomSet src, InMemoryAtomSet dest) {
		for (Atom a : src) {
			dest.add(this.createImageOf(a));
		}
	}

	@Override
	public Rule createImageOf(Rule rule) {
		Rule substitut = RuleFactory.getInstance().createRule();
		this.apply(rule.getBody(), substitut.getBody());
		this.apply(rule.getHead(), substitut.getHead());
		return substitut;
	}

	@Override
	public boolean compose(Term term, Term substitut) {
		Term termSubstitut = this.createImageOf(term);
		Term substitutSubstitut = this.createImageOf(substitut);

		if (Term.Type.CONSTANT.equals(termSubstitut.getType())) {
			Term tmp = termSubstitut;
			termSubstitut = substitutSubstitut;
			substitutSubstitut = tmp;
		}

		for (Term t : this.getTerms()) {
			if (termSubstitut.equals(this.createImageOf(t))) {
				if (!this.put(t, substitutSubstitut)) {
					return false;
				}
			}
		}

		if (!this.put(termSubstitut, substitutSubstitut)) {
			return false;
		}
		return true;
	}

	/**
	 * @see fr.lirmm.graphik.graal.core.Substitution#compose(fr.lirmm.graphik.graal.core.Substitution)
	 */
	@Override
	public Substitution compose(Substitution s) {
		Substitution newSub = this.getNewInstance();
		for (Term term : this.getTerms()) {
			if (!newSub.compose(term, this.createImageOf(term))) {
				return null;
			}
		}
		for (Term term : s.getTerms()) {
			if (!newSub.compose(term, s.createImageOf(term))) {
				return null;
			}
		}
		return newSub;
	}

	// /////////////////////////////////////////////////////////////////////////
	// OVERRIDE OBJECT METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		builder.append('{');
		for (Term key : this.getTerms()) {
			if (first)
				first = false;
			else
				builder.append(',');
			builder.append(key).append("->");
			builder.append(this.createImageOf(key));
		}
		builder.append('}');
		return builder.toString();
	}

	protected abstract Substitution getNewInstance();

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Substitution)) {
			return false;
		}
		return this.equals((Substitution) obj);
	}

	public boolean equals(Substitution other) { // NOPMD
		for (Term t : this.getTerms()) {
			if (!this.createImageOf(t).equals(other.createImageOf(t)))
				return false;
		}

		for (Term t : other.getTerms()) {
			if (!this.createImageOf(t).equals(other.createImageOf(t)))
				return false;
		}

		return true;
	}

};
