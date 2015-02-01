/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure.queries;

import java.util.ArrayList;

import fr.lirmm.graphik.graal.backward_chaining.pure.utils.QueryUnifier;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSets;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public final class QueryUtils {
	
	private QueryUtils() {}
	
	// /////////////////////////////////////////////////////////////////////////
	// 
	// /////////////////////////////////////////////////////////////////////////
	
	/**
	 * Rewrite the fact q according to the unifier u between the head of r and q
	 * 
	 * @param q
	 *            the fact to rewrite
	 * @param r
	 *            the rule which is unified with q
	 * @param u
	 *            the unifier between q and r
	 * @return the rewrite of q according to the unifier u between the head of r
	 *         and q
	 */
	public static ConjunctiveQuery rewrite(ConjunctiveQuery q, QueryUnifier u) {
		AtomSet ajout = u.getImageOf(u.getRule().getBody());
		AtomSet restant = u.getImageOf(AtomSets.minus(q.getAtomSet(),
				u.getPiece()));
		ConjunctiveQuery rew = null;
		if(ajout != null && restant != null) { // FIXME
			AtomSet res = AtomSets.union(ajout, restant);
			ArrayList<Term> ansVar = new ArrayList<Term>();
			ansVar.addAll(q.getAnswerVariables());
			rew = new DefaultConjunctiveQuery(res, ansVar);
		}
		return rew;
	}
	
	/**
	 * Rewrite the marked fact q according to the unifier u between the head of
	 * r and q
	 * 
	 * @param q
	 *            the fact to rewrite must be a marked fact
	 * @param r
	 *            the rule which is unified with q
	 * @param u
	 *            the unifier between q and r
	 * @return the rewrite of q according to the unifier u between the head of r
	 *         and q
	 */
	public static MarkedQuery rewriteWithMark(ConjunctiveQuery q, QueryUnifier u) {

		AtomSet ajout = u.getImageOf(u.getRule().getBody());
		AtomSet restant = u.getImageOf(AtomSets.minus(q.getAtomSet(),
				u.getPiece()));
		MarkedQuery rew = null;
		if(ajout != null && restant != null) { // FIXME
			AtomSet res = AtomSets.union(ajout, restant);
			ArrayList<Term> ansVar = new ArrayList<Term>();
			ansVar.addAll(q.getAnswerVariables());
			rew = new MarkedQuery(res, ansVar);

			ArrayList<Atom> markedAtoms = new ArrayList<Atom>();
			for (Atom a : ajout)
				markedAtoms.add(a);
	
			rew.setMarkedAtom(markedAtoms);
		}

		return rew;
	}

}
