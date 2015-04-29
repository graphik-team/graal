package fr.lirmm.graphik.graal.homomorphism;

import java.util.Iterator;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.BuiltInPredicate;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.stream.SubstitutionReader;
import fr.lirmm.graphik.graal.core.term.Term;


public class ComplexHomomorphism<Q extends ConjunctiveQuery, F extends AtomSet> implements Homomorphism<Q,F> {

	private Homomorphism<ConjunctiveQuery,F> rawSolver;
	private LinkedList<Atom> builtInAtoms;

	public ComplexHomomorphism(Homomorphism<ConjunctiveQuery,F> rawSolver) {
		this.rawSolver = rawSolver;
	}

	@Override
	public <U1 extends Q, U2 extends F> SubstitutionReader execute(U1 q, U2 f)
			throws HomomorphismException {
    	InMemoryAtomSet rawAtoms = new LinkedListAtomSet();
		this.builtInAtoms = new LinkedList<Atom>();
		for (Atom a : q) {
			if (a.getPredicate() instanceof BuiltInPredicate) {
				this.builtInAtoms.add(a);
			}
			else {
				rawAtoms.add(a);
			}
		}
		DefaultConjunctiveQuery rawQuery = new DefaultConjunctiveQuery(rawAtoms);
		rawQuery.setAnswerVariables(q.getAnswerVariables());
		return new BuiltInSubstitutionReader(this.rawSolver.execute(rawQuery,f));
	}

	protected class BuiltInSubstitutionReader implements SubstitutionReader {

		public BuiltInSubstitutionReader(SubstitutionReader reader) {
			this.rawReader = reader;
		}

		@Override
    	public boolean hasNext() {
			if(this.next == null)
				this.next = this.computeNext();
			return this.next != null;
		}

		@Override
    	public Substitution next() {
			hasNext();
			Substitution res = this.next;
			this.next = null;
			return res;
		}

		protected Substitution computeNext() {
			if (this.rawReader.hasNext()) {
				Substitution res = this.rawReader.next();
				if (check(res)) {
					return res;
				}
				else {
					return computeNext();
				}
			}
			else {
				return null;
			}
		}

		protected boolean check(Substitution s) {
			for (Atom a : builtInAtoms) {
				Atom a2 = s.getSubstitut(a);
				if (!((BuiltInPredicate)a2.getPredicate()).evaluate(a2.getTerms().toArray(new Term[a2.getTerms().size()]))) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void remove() { }

		@Override
    	public Iterator<Substitution> iterator() { return this; }

		@Override
		public void close() { this.rawReader.close(); this.rawReader = null; }

		private Substitution next;
		private SubstitutionReader rawReader;

	};
};

