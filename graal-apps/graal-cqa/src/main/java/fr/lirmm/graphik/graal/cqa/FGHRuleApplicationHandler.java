package fr.lirmm.graphik.graal.cqa;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RuleApplicationHandler;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;

public class FGHRuleApplicationHandler implements RuleApplicationHandler {

	public FGHRuleApplicationHandler(AtomIndex index, FGH fgh) {
		this.index = index;
		this.fgh = fgh;
	}

	@Override
	public boolean onRuleApplication(AtomSet from, AtomSet atomSet, AtomSet base) {

		try {
		Query q = new DefaultConjunctiveQuery(from,from.getTerms(Term.Type.VARIABLE));
		for (Substitution s : this.solver.execute(q,base)) {

			//AtomSet from2 = s.getSubstitut(from);

			LinkedList causes = new LinkedList<Integer>();
			for (Atom a : from) {
				causes.add(new Integer(this.index.get(s.getSubstitut(a))));
			}
			for (Atom a : atomSet) {
				this.fgh.add(causes,this.index.get(a));
			}
		}

		return true;
		}
		catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
		return true;
	}

	public void setSolver(Homomorphism solver) {
		this.solver = solver;
	}

	private Homomorphism solver;
	private AtomIndex   index;
	private FGH         fgh;

};

