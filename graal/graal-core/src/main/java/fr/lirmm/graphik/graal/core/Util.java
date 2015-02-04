package fr.lirmm.graphik.graal.core;

import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;

public abstract class Util {

	/**
	 * Transform 
	 * (reification)
	 * @param a
	 * @param freeVarGen 
	 * @param id
	 * @return
	 */
	public static AtomSet reification(Atom a, SymbolGenerator freeVarGen) {
		AtomSet atomSet = new LinkedListAtomSet();
		
		String predicatLabel = a.getPredicate().getLabel();
		Term termId = freeVarGen.getFreeVar();
		List<Term> terms; 
		
		for(Integer i = 0; i < a.getPredicate().getArity(); ++i) {
			terms = new LinkedList<Term>();
			terms.add(termId);
			terms.add(a.getTerm(i));
			atomSet.add(new DefaultAtom( new Predicate(predicatLabel + "#" + i.toString(), 2), terms));
		}
			
		
		return atomSet;
	}
}
