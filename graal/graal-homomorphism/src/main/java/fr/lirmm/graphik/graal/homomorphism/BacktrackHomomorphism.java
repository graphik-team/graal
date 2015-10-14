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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Term.Type;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.TreeMapSubstitution;
import fr.lirmm.graphik.util.stream.AbstractIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * This Backtrack is inspired by the Baget Jean-François Thesis (Chapter 5)
 * 
 * see also "Backtracking Through Biconnected Components of a Constraint Graph"
 * (Jean-François Baget, Yannic S. Tognetti IJCAI 2001)
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class BacktrackHomomorphism implements Homomorphism<InMemoryAtomSet, AtomSet> {

	@Override
	public <U1 extends InMemoryAtomSet, U2 extends AtomSet> CloseableIterator<Substitution> execute(U1 q, U2 a)
	                                                                                                throws HomomorphismException {
		return new BT(q, a);
	}

	// /////////////////////////////////////////////////////////////////////////
	// STRUCTS
	// /////////////////////////////////////////////////////////////////////////

	private static class Var {
		Variable value;
		int level;
		Collection<Atom> preAtoms;

		Var(Variable value, int level) {
			this.value = value;
			this.level = level;
		}

		@Override
		public String toString() {
			if (value != null)
				return value.toString();
			else
				return "NULL";
		}
	}

	private class BT extends AbstractIterator<Substitution> implements CloseableIterator<Substitution> {

		private InMemoryAtomSet h;
		private AtomSet g;
		private Substitution next = null;

		private Term[] domain;

		private Var[] vars;
		Var currentVar;
		private Map<Variable, Integer> index;

		int[] images;
		int levelMax;
		int level;
		boolean goBack;

		// /////////////////////////////////////////////////////////////////////////
		// CONSTRUCTORS
		// /////////////////////////////////////////////////////////////////////////

		/**
		 * Look for an homomorphism of h into g.
		 * 
		 * @param h
		 * @param g
		 */
		public BT(InMemoryAtomSet h, AtomSet g) {
			this.h = h;
			this.g = g;

			Set<Term> terms;
			try {
				terms = this.g.getTerms();
			} catch (AtomSetException e) {
				// TODO treat this exception
				e.printStackTrace();
				throw new Error("Untreated exception");
			}
			domain = terms.toArray(new Term[terms.size()]);

			// Compute order on query variables and atoms
			vars = this.computeOrder(this.h);
			index = new TreeMap<Variable, Integer>();
			for (Var v : vars) {
				if (v.value != null)
					index.put(v.value, v.level);
			}
			computeAtomOrder(h, vars);

			currentVar = null;
			images = new int[vars.length];
			levelMax = vars.length - 1;
			level = 1;
			goBack = false;

		}

		// /////////////////////////////////////////////////////////////////////////
		// PUBLIC METHODS
		// /////////////////////////////////////////////////////////////////////////

		@Override
		public boolean hasNext() {
			if (this.next == null) {
				try {
					this.next = computeNext();
				} catch (HomomorphismException e) {
					this.next = null;
					// TODO
				}
			}
			return this.next != null;
		}

		@Override
		public Substitution next() {
			Substitution tmp = null;
			if (this.hasNext()) {
				tmp = this.next;
				this.next = null;
			}
			return tmp;
		}

		// /////////////////////////////////////////////////////////////////////////
		// PRIVATE METHODS
		// /////////////////////////////////////////////////////////////////////////

		private Substitution computeNext() throws HomomorphismException {
			try {

				if (levelMax == 0) {
					return null;
				} else {
					while (level > 0) {
						//
						if (level > levelMax) {
							goBack = true;
							--level;
							return solutionFound(vars, domain, images);
						} else {
							currentVar = vars[level];
						}

						//
						if (goBack) {
							if (hasMoreValues(currentVar, images, g)) {
								goBack = false;
								level = nextLevel(currentVar);
							} else {
								level = previousLevel(currentVar);
							}
						} else {
							if (getFirstValue(currentVar, images, g)) {
								level = nextLevel(currentVar);
							} else {
								goBack = true;
								level = previousLevel(currentVar);
							}
						}
					}
				}
			} catch (AtomSetException e) {
				throw new HomomorphismException("Exception during backtracking", e);
			}
			return null;
		}

		private Substitution solutionFound(Var[] vars, Term[] domain, int[] images) {
			Substitution s = new TreeMapSubstitution();

			for (int i = 1; i < vars.length; ++i) {
				s.put(vars[i].value, domain[images[vars[i].level]]);
			}

			return s;
		}

		private boolean hasMoreValues(Var var, int[] images, AtomSet g) throws AtomSetException {
			int i = images[var.level];
			while (++i < images.length) {
				images[var.level] = i;
				if (isHomomorphism(var.preAtoms, g, images)) {
					return true;
				}
			}
			return false;
		}

		private boolean getFirstValue(Var var, int[] images, AtomSet g) throws AtomSetException {
			int i = -1;
			while (++i < images.length) {
				images[var.level] = i;
				if (isHomomorphism(var.preAtoms, g, images)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * @param var
		 * @return
		 */
		private int previousLevel(Var var) {
			return var.level - 1;
		}

		/**
		 * @param var
		 * @return
		 */
		private int nextLevel(Var var) {
			return var.level + 1;
		}

		/**
		 * Compute an order over variables from h
		 * 
		 * @param h
		 * @return
		 */
		private Var[] computeOrder(InMemoryAtomSet h) {
			Set<Term> terms = h.getTerms(Term.Type.VARIABLE);
			Var[] vars = new Var[terms.size() + 1];
			int i = 1;
			vars[0] = new Var(null, 0);
			for (Term t : terms) {
				vars[i] = new Var((Variable) t, i);
				++i;
			}
			return vars;
		}

		/**
		 * The index 0 contains the fully instantiated atoms.
		 * 
		 * @param atomset
		 * @param varsOrdered
		 * @return
		 */
		private void computeAtomOrder(Iterable<Atom> atomset, Var[] vars) {
			int tmp, rank;

			for (int i = 0; i < vars.length; ++i)
				vars[i].preAtoms = new LinkedList<Atom>();

			//
			for (Atom a : atomset) {
				rank = 0;
				for (Term t : a.getTerms(Type.VARIABLE)) {
					tmp = indexOf((Variable) t);

					if (rank < tmp)
						rank = tmp;
				}
				vars[rank].preAtoms.add(a);
			}
		}

		/**
		 * Return the index of the specified variable.
		 * 
		 * @param var
		 * @return
		 */
		private int indexOf(Variable var) {
			return this.index.get(var);
		}

		private boolean isHomomorphism(Collection<Atom> atomsFrom, AtomSet atomsTo, int[] images)
		                                                                                         throws AtomSetException {
			for (Atom atom : atomsFrom) {
				if (!atomsTo.contains(createImageOf(atom, images)))
					return false;
			}
			return true;
		}

		/**
		 * @param atom
		 * @param images
		 * @return
		 */
		private Atom createImageOf(Atom atom, int[] images) {
			List<Term> termsSubstitut = new LinkedList<Term>();
			for (Term term : atom.getTerms()) {
				if (term instanceof Variable) {
					termsSubstitut.add(this.domain[images[indexOf((Variable) term)]]);
				} else {
					termsSubstitut.add(term);
				}
			}

			return new DefaultAtom(atom.getPredicate(), termsSubstitut);
		}

		@Override
		public void close() {
		}

	}

}
