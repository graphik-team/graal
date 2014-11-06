package fr.lirmm.graphik.graal.backward_chaining.pure;

import java.util.ArrayList;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.backward_chaining.pure.queries.MarkedQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.queries.PureQuery;
import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.QueryUnifier;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSets;

/**
 * @author Mélanie KÖNIG Query rewriting engine that rewrite query using
 *         aggregation all rule of most general single piece-unifiers, selecting
 *         unifiers during the aggregation and using a marking system to avoid
 *         redundant rewritings
 */
public class QREAggregAllRules extends QREAggregAllRulesBasic {

	public QREAggregAllRules(PureQuery query, Iterable<Rule> rules,
			RulesCompilation order) {
		super(query, rules, order);
	}

	public ConjunctiveQuery getQuery() {
		ConjunctiveQuery q = super.getQuery();
		ArrayList<Atom> markedAtoms = new ArrayList<Atom>();
		for (Atom a : q.getAtomSet()) {
			markedAtoms.add(a);
		}
		MarkedQuery markedQuery = new MarkedQuery(q, markedAtoms);
		return markedQuery;
	}

	/**
	 * Returns the list of the atoms of the query that can be unify with the
	 * head of R
	 * 
	 * @param query
	 *            the query to unify
	 * @param R
	 *            the rule whose has the head to unify
	 * @return the list of the atoms of the query that have the same predicate
	 *         as the head atom of R and that are recently created in query
	 * @throws Exception
	 */
	@Override
	protected LinkedList<Atom> getUnifiableAtoms(ConjunctiveQuery q, Rule R) {
		MarkedQuery query;
		if (q instanceof MarkedQuery) {
			query = (MarkedQuery) q;
			LinkedList<Atom> atoms;
			atoms = super.getUnifiableAtoms(q, R);

			LinkedList<Atom> res = new LinkedList<Atom>();
			// keep only the recently created so marked in query
			for (Atom a : atoms)
				if (query.isMarked(a)) {
					res.add(a);
				}
			return res;
		} else
			return super.getUnifiableAtoms(q, R);

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
	@Override
	public MarkedQuery rewrite(ConjunctiveQuery q, QueryUnifier u) {

		AtomSet ajout = u.getImageOf(u.getRule().getBody());
		AtomSet restant = u.getImageOf(AtomSets.minus(q.getAtomSet(),
				u.getPiece()));
		AtomSet res = AtomSets.union(ajout, restant);
		ArrayList<Term> ansVar = new ArrayList<Term>();
		ansVar.addAll(q.getAnswerVariables());
		MarkedQuery rew = new MarkedQuery(res, ansVar);

		ArrayList<Atom> markedAtoms = new ArrayList<Atom>();
		for (Atom a : ajout)
			markedAtoms.add(a);

		rew.setMarkedAtom(markedAtoms);

		return rew;
	}

	@Override
	protected LinkedList<QueryUnifier> getSRUnifier(ConjunctiveQuery q, Rule r) {
		LinkedList<QueryUnifier> unifiers = new LinkedList<QueryUnifier>();
		LinkedList<QueryUnifier> simpleUnifiers = new LinkedList<QueryUnifier>();
		/** compute the simple unifiers **/
		simpleUnifiers = getSinglePieceUnifiers(q, r);
		if (!simpleUnifiers.isEmpty()) {
			if (q instanceof MarkedQuery) {
				MarkedQuery copy = (MarkedQuery) new MarkedQuery(
						(MarkedQuery) q);
				copy.markAll();
				simpleUnifiers = getSinglePieceUnifiers(copy, r);
			}
		}
		/** compute the aggregated unifier by rule **/
		unifiers.addAll(getAggregatedUnifiers(simpleUnifiers));

		return unifiers;
	}

}
