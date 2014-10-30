/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

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
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.PureHomomorphism;

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
		try {
			if (PureHomomorphism.getInstance().exist(a1, a2)) {
				return true;
			}
		} catch (HomomorphismException e) {
		}
		return false;
	}

	/**
	 * This methods test if the first rule logically imply the second. This
	 * methods works with linear rules (atomic head and body).
	 * 
	 * @param r1
	 * @param r2
	 * @return true, if r1 logically imply r2.
	 */
	public static boolean imply(Rule r1, Rule r2) {

		Atom b1 = r1.getBody().iterator().next();
		Atom b2 = r2.getBody().iterator().next();
		Atom h1 = r1.getHead().iterator().next();
		Atom h2 = r2.getHead().iterator().next();

		if (b1.getPredicate().equals(b2.getPredicate())
				&& h1.getPredicate().equals(h2.getPredicate())) {
			Map<Term, Term> s = new TreeMap<Term, Term>();
			Term term;
			for (int i = 0; i < b1.getPredicate().getArity(); i++) {
				term = s.get(b1.getTerm(i));
				if (term == null)
					s.put(b1.getTerm(i), b2.getTerm(i));
				else if (!term.equals(b2.getTerm(i)))
					return false;
			}
			for (int i = 0; i < h1.getPredicate().getArity(); i++) {
				term = s.get(h1.getTerm(i));
				if (term == null)
					s.put(h1.getTerm(i), h2.getTerm(i));
				else if (!term.equals(h2.getTerm(i)))
					return false;
			}
			return true;
		}

		return false;
	}

}
