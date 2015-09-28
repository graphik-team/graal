/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.util.Iterator;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.RepositoryException;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.store.Store;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.triplestore.SailStore;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SailStoreTest {

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
