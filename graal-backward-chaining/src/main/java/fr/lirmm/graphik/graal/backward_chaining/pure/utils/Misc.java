/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure.utils;

import java.util.HashMap;
import java.util.Iterator;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultFreeVarGen;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class Misc {

	private static DefaultFreeVarGen varGen = new DefaultFreeVarGen("X"
			+ Integer.toString(Misc.class.hashCode()));

	private Misc() {
	}

	public static AtomSet getIrredondant(RulesCompilation comp, AtomSet atomSet) {
		AtomSet irr = new LinkedListAtomSet(atomSet);
		if (comp != null) {
			Iterator<Atom> i = irr.iterator();
			Iterator<Atom> j;
			Atom origin;
			Atom target;
			boolean isSubsumed;
			while (i.hasNext()) {
				target = i.next();
				j = irr.iterator();
				isSubsumed = false;
				while (j.hasNext() && !isSubsumed) {
					origin = j.next();
					if (target != origin)
						if (comp.isImplied(target, origin)) {
							isSubsumed = true;
						}

				}
				if (isSubsumed)
					i.remove();
			}
		}
		return irr;
	}

	public static Rule getSafeCopy(Rule rule) {
		Substitution substitution = new TreeMapSubstitution();
		for (Term t : rule.getTerms(Term.Type.VARIABLE)) {
			substitution.put(t, varGen.getFreeVar());
		}

		AtomSet body = rule.getBody();
		AtomSet head = rule.getHead();

		AtomSet safeBody = new LinkedListAtomSet();
		AtomSet safeHead = new LinkedListAtomSet();

		substitution.substitut(body, safeBody);
		substitution.substitut(head, safeHead);

		return new DefaultRule(safeBody, safeHead);
	}

	public boolean isMoreGeneralThan(AtomSet a1, AtomSet a2) {
		Homomorphism hom = new Homomorphism(a1, a2);
		return hom.existHomomorphism();
	}

	public static boolean equivalent(Rule r1, Rule r2) {
		// Rule o = Misc.getSafeCopy(r2);
		Iterator<Atom> r1BodyIt = r1.getBody().iterator();
		Iterator<Atom> r2BodyIt = r2.getBody().iterator();

		if (r1BodyIt.hasNext() && r2BodyIt.hasNext()) {
			Atom b1 = r1BodyIt.next();
			Atom b2 = r2BodyIt.next();
			Atom h1 = r1.getHead().iterator().next();
			Atom h2 = r2.getHead().iterator().next();

			if (r1BodyIt.hasNext() && r2BodyIt.hasNext()) {

				if (b2.getPredicate().equals(b1.getPredicate())
						&& h2.getPredicate().equals(h1.getPredicate())) {
					HashMap<Term, Term> s = new HashMap<Term, Term>(b2
							.getPredicate().getArity());
					for (int i = 0; i < b2.getPredicate().getArity(); i++) {
						if (s.get(b2.getTerm(i)) == null)
							s.put(b2.getTerm(i), b1.getTerm(i));
						else if (!s.get(b2.getTerm(i)).equals(b1.getTerm(i)))
							return false;

					}
					for (int i = 0; i < h2.getPredicate().getArity(); i++) {
						if (s.get(h2.getTerm(i)) == null)
							s.put(h2.getTerm(i), h1.getTerm(i));
						else if (!s.get(h2.getTerm(i)).equals(h1.getTerm(i)))
							return false;
					}
					return true;
				}
			}
		}
		return false;
	}
}
