package fr.lirmm.graphik.graal.apps;

import java.util.LinkedList;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Query;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.forward_chaining.RuleApplicationHandler;
import fr.lirmm.graphik.graal.homomorphism.Homomorphism;

public class FGHRuleApplicationHandler implements RuleApplicationHandler {

	public FGHRuleApplicationHandler(AtomIndex index, FGH fgh) {
		_index = index;
		_fgh = fgh;
	}

	@Override
	public boolean onRuleApplication(ReadOnlyAtomSet from, ReadOnlyAtomSet atomSet, ReadOnlyAtomSet base) {

		try {
		Query q = new DefaultConjunctiveQuery(from,from.getTerms(Term.Type.VARIABLE));
		for (Substitution s : _solver.execute(q,base)) {

			//ReadOnlyAtomSet from2 = s.getSubstitut(from);

			LinkedList causes = new LinkedList<Integer>();
			for (Atom a : from) {
				causes.add(new Integer(_index.get(s.getSubstitut(a))));
			}
			for (Atom a : atomSet) {
				_fgh.add(causes,_index.get(a));
			}
		}

		return true;
		}
		catch (Exception e) {
			System.err.println("Something went wrong????");
		}
		return true;
	}

	public void setSolver(Homomorphism solver) {
		_solver = solver;
	}

	private Homomorphism _solver;
	private AtomIndex   _index;
	private FGH         _fgh;

};

