/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 package fr.lirmm.graphik.graal.transformation;

import java.util.LinkedList;
import java.util.List;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.VariableGenerator;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.Term;

public abstract class Util {

	/**
	 * Transform 
	 * (reification)
	 * @param a
	 * @param freeVarGen 
	 * @param id
	 * @return
	 */
	public static InMemoryAtomSet reification(Atom a, VariableGenerator freeVarGen) {
		InMemoryAtomSet atomSet = new LinkedListAtomSet();
		
		String predicatLabel = a.getPredicate().getIdentifier().toString();
		Term termId = freeVarGen.getFreshVar();
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
