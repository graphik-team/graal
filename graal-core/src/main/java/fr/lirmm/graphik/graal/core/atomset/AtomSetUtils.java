/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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
package fr.lirmm.graphik.graal.core.atomset;

import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.factory.DefaultAtomSetFactory;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public final class AtomSetUtils {

	private AtomSetUtils() {
	}
	
	public static boolean isSingleton(AtomSet a) throws IteratorException {
		CloseableIterator<Atom> i = a.iterator();
		if (!i.hasNext())
			return false;
		i.next();
		return !i.hasNext();
	}

	public static boolean isSingleton(InMemoryAtomSet a) {
		try {
			return isSingleton((AtomSet) a);
		} catch (IteratorException e) {
			throw new Error("Should never happen");
		}
		
	}

	public static boolean hasSize2(AtomSet a) throws IteratorException {
		CloseableIterator<Atom> i = a.iterator();
		if (!i.hasNext())
			return false;
		i.next();
		if (!i.hasNext())
			return false;
		i.next();
		return !i.hasNext();
	}

	public static boolean hasSize2(InMemoryAtomSet a) {
		try {
			return hasSize2((AtomSet) a);
		} catch (IteratorException e) {
			throw new Error("Should never happen");
		}
	}

	/**
	 * 
	 * @param a1
	 * @param a2
	 * @return true if a1 contains a2, false otherwise.
	 */
	public static boolean contains(InMemoryAtomSet a1, InMemoryAtomSet a2) {
		CloseableIteratorWithoutException<Atom> it = a2.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (!a1.contains(a)) {
				return false;
			}
		}
		return true;
	}


	public static InMemoryAtomSet minus(InMemoryAtomSet a1, InMemoryAtomSet a2) {
		InMemoryAtomSet atomset = DefaultAtomSetFactory.instance().create();
		CloseableIteratorWithoutException<Atom> it = a1.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (!a2.contains(a)) {
				atomset.add(a);
			}
		}
		return atomset;
	}

	/**
	 * Return the terms occuring both in Q\P and P
	 */
	public static LinkedList<Term> sep(InMemoryAtomSet a1, InMemoryAtomSet a2) {
		InMemoryAtomSet pBar = minus(a2, a1);
		LinkedList<Term> sep = new LinkedList<Term>();
		for (Term t : pBar.getTerms()) {
			for (Term x : a2.getTerms())
				if (x.equals(t))
					sep.add(t);
		}
		return sep;
	}
	
	public static InMemoryAtomSet union(InMemoryAtomSet a1, InMemoryAtomSet a2) {
		InMemoryAtomSet atomset = DefaultAtomSetFactory.instance().create();
		CloseableIteratorWithoutException<Atom> it = a1.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			atomset.add(new DefaultAtom(a));
		}
		it = a2.iterator();
		while (it.hasNext()) {
			Atom a = it.next();
			if (!atomset.contains(a)) {
				atomset.add(new DefaultAtom(a));
			}				
		}
		return atomset;
	}

}
