/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.factory.SubstitutionFactory;
import fr.lirmm.graphik.util.LinkedSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class Unifier {

	private Unifier() {
	};

	public static Set<Substitution> computePieceUnifier(Rule rule,
			AtomSet atomset) {
//		Substitution sh = new Substitution();
//		Substitution sb = new Substitution();
//
//		for (Term t : rule.getTerms()) {
//			sh.put(t,new Term(t.getValue().toString() + ".H",t.getType()));
//		}
//		for (Term t : atomset.getTerms()) {
//			sb.put(t,new Term(t.getValue().toString() + ".B",t.getType()));
//		}

		Set<Substitution> unifiers = new LinkedSet<Substitution>();
		Queue<Atom> atomQueue = new LinkedList<Atom>();
		for (Atom a : atomset) {
			atomQueue.add(a);
		}

		for (Atom a : atomset) {
			Queue<Atom> tmp = new LinkedList<Atom>(atomQueue);
			unifiers.addAll(extendUnifier(rule, tmp, a,
					new TreeMapSubstitution()));
		}
		return unifiers;
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE FUNCTIONS
	// /////////////////////////////////////////////////////////////////////////

	private static Collection<Substitution> extendUnifier(Rule rule,
			Queue<Atom> atomset, Atom pieceElement, Substitution unifier) {
		atomset.remove(pieceElement);
		Collection<Substitution> unifierCollection = new LinkedList<Substitution>();
		Set<Term> frontierVars = rule.getFrontier();
		Set<Term> existentialVars = rule.getExistentials();

		for (Atom atom : rule.getHead()) {
			Substitution u = unifier(unifier, pieceElement, atom, frontierVars,
					existentialVars);
			if (u != null) {
				Iterator<Atom> it = atomset.iterator();
				Atom newPieceElement = null;
				while (it.hasNext() && newPieceElement == null) {
					Atom a = it.next();

					for (Term t : a) {
						if (existentialVars.contains(u.getSubstitute(t))) {
							newPieceElement = a;
							break;
						}
					}

				}

				if (newPieceElement == null) {
					unifierCollection.add(u);
				} else {
					unifierCollection.addAll(extendUnifier(rule, atomset,
							newPieceElement, u));
				}
			}
		}
		return unifierCollection;
	}

	private static Substitution unifier(Substitution baseUnifier, Atom a1,
			Atom atomFromHead, Set<Term> frontierVars, Set<Term> existentialVars) {
		if (a1.getPredicate().equals(atomFromHead.getPredicate())) {
			boolean error = false;
			Substitution u = SubstitutionFactory.getInstance()
					.createSubstitution();
			u.put(baseUnifier);
			for (int i = 0; i < a1.getPredicate().getArity(); ++i) {
				Term t1 = a1.getTerm(i);
				Term t2 = atomFromHead.getTerm(i);
				if (!t1.equals(t2)) {
					if (Term.Type.VARIABLE.equals(t1.getType())) {
						if (!compose(u, frontierVars, existentialVars, t1, t2))
							error = true;
					} else if (Term.Type.VARIABLE.equals(t2.getType())
							&& !existentialVars.contains(t2)) {
						if (!compose(u, frontierVars, existentialVars, t2, t1))
							error = true;
					} else {
						error = true;
					}
				}
			}

			if (!error)
				return u;
		}

		return null;
	}

	private static boolean compose(Substitution u, Set<Term> frontierVars,
			Set<Term> existentials, Term term, Term substitut) {
		term = u.getSubstitute(term);
		substitut = u.getSubstitute(substitut);

		if (Term.Type.CONSTANT.equals(term.getType())
				|| existentials.contains(term)) {
			Term tmp = term;
			term = substitut;
			substitut = tmp;
		}

		for (Term t : u.getTerms()) {
			if (term.equals(u.getSubstitute(t))) {
				if (!put(u, frontierVars, existentials, t, substitut)) {
					return false;
				}
			}
		}

		if (!put(u, frontierVars, existentials, term, substitut)) {
			return false;
		}
		return true;
	}

	private static boolean put(Substitution u, Set<Term> frontierVars,
			Set<Term> existentials, Term term, Term substitut) {
		if (!term.equals(substitut)) {
			// two (constant | existentials vars)
			if (Term.Type.CONSTANT.equals(term.getType())
					|| existentials.contains(term)) {
				return false;
				// fr -> existential vars
			} else if (frontierVars.contains(term)
					&& existentials.contains(substitut)) {
				return false;
			}
		}
		return u.put(term, substitut);
	}
}
