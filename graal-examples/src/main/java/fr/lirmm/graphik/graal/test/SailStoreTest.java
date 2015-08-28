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
 /**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.util.Iterator;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.RepositoryException;

import parser.TERM_TYPE;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.impl.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.graal.store.triplestore.SailStore;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SailStoreTest {
	
	public static Term createTerm(TERM_TYPE termType, Object term) {
		Term.Type type = null;
		switch(termType) {
		case ANSWER_VARIABLE:
		case VARIABLE:
			type = Term.Type.VARIABLE;
			break;
		case CONSTANT: 
			type = Term.Type.CONSTANT;
			break;
		case FLOAT:
		case INTEGER:
		case STRING:
			type = Term.Type.LITERAL;
			break;
		}
		return DefaultTermFactory.instance().createTerm(term, type);
	}
	
	public static void main(String args[]) throws RepositoryException, MalformedQueryException, AtomSetException {
				
		Store store = new SailStore();
		
		Term t1 = DefaultTermFactory.instance()
				.createConstant("http://to.to/b");
		Term t2 = DefaultTermFactory.instance()
				.createConstant("http://to.to/a");
		Predicate p = new Predicate("http://to.to/p", 2);
		Atom atom1 = new DefaultAtom(p, t1, t2);
		
		t1 = DefaultTermFactory.instance().createConstant("http://to.to/a");
		t2 = DefaultTermFactory.instance().createConstant("http://to.to/a");
		Atom atom2 = new DefaultAtom(p, t1, t2);
		
		t1 = DefaultTermFactory.instance().createConstant("http://to.to/c");
		t2 = DefaultTermFactory.instance().createConstant("http://to.to/a");
		Atom atom3 = new DefaultAtom(p, t1, t2);
		System.out.println("atom3: " + atom3);
		
		store.add(atom1);
		store.add(atom2);
		store.add(atom3);
		
		Atom atom4 = DlgpParser.parseAtom("p(a,b).");
		System.out.println("atom4: " + atom4);
		System.out.println("==================");
		store.add(atom4);
		
		System.out.println("////////");
		for(Atom a : store) {
			System.out.println(a);
		}
		System.out.println("»»»»»»»»");
		

		
		/*if(store.remove(atom2)) {
			System.out.println("remove :/ ?");
		} else {
			System.out.println("return of remove is ok");
		}*/
				
		
		System.out.println(store.contains(atom1) + "/ true");
		System.out.println(store.contains(atom2) + "/ false");

		{
			System.out.println("*******************"); 
			Iterator<Predicate> it = store.predicatesIterator();
			while(it.hasNext()) {
				System.out.println(it.next());
			}
		}
		
		{
			System.out.println("*******************"); 
			Iterator<Term> it = store.termsIterator();
			while(it.hasNext()) {
				System.out.println(it.next());
			}
		}
			
		

	}
}
