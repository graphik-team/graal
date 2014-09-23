/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.pure.queries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import fr.lirmm.graphik.graal.backward_chaining.pure.rules.RulesCompilation;
import fr.lirmm.graphik.graal.backward_chaining.pure.utils.Misc;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class PureQuery extends DefaultConjunctiveQuery {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public PureQuery() {
		super();
	}

	/**
	 * Create a query which has the same atom and id as the given fact and which
	 * has the given term as answerVariable
	 * 
	 * @param f
	 * @param answerVariable
	 */
	public PureQuery(AtomSet atomSet, Collection<Term> answerVariable) {
		super(atomSet, answerVariable);
	}

	/**
	 * @param f
	 * @param answerVariable
	 */
	public PureQuery(ArrayList<Atom> atoms, ArrayList<Term> answerVariable) {
		super(atoms, answerVariable);
	}

	/**
	 * Copy constructor
	 * 
	 * @param q
	 */
	public PureQuery(ConjunctiveQuery q) {
		super(q);
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	public PureQuery getIrredondant(RulesCompilation comp) {
		return new PureQuery(Misc.getIrredondant(comp, this.getAtomSet()),
				this.getAnswerVariables());
	}

	public boolean containAuxiliaryPredicate() {
		for (Atom a : this.getAtomSet()) {
			String label = (String) a.getPredicate().getLabel();
			if (label.length() > 3 && label.substring(0, 4).equals("aux_"))
				return true;
		}
		return false;
	}

	public void removeAnswerPredicate() {
		Iterator<Atom> ita = this.getAtomSet().iterator();
		while (ita.hasNext()) {
			Atom a = ita.next();
			String label = (String) a.getPredicate().getLabel();
			if (label.length() > 3 && label.substring(0, 4).equals("Ans_")) {
				ita.remove();
			}
		}
	}
}
