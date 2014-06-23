package fr.lirmm.graphik.kb;

import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.kb.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.AtomSet;
import fr.lirmm.graphik.kb.core.DefaultAtom;
import fr.lirmm.graphik.kb.core.Predicate;
import fr.lirmm.graphik.kb.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.kb.core.Term;

public abstract class Util {

	/**
	 * Transform 
	 * (reification)
	 * @param a
	 * @param freeVarGen 
	 * @param id
	 * @return
	 */
	public static ReadOnlyAtomSet reification(Atom a, SymbolGenerator freeVarGen) {
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
