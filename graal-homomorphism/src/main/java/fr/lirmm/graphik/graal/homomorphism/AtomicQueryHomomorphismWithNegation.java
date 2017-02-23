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
package fr.lirmm.graphik.graal.homomorphism;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithNegation;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.HashMapSubstitution;
import fr.lirmm.graphik.graal.core.factory.DefaultConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.stream.filter.ConjunctiveQueryFilterIterator;
import fr.lirmm.graphik.graal.homomorphism.backjumping.GraphBaseBackJumping;
import fr.lirmm.graphik.graal.homomorphism.bbc.BCC;
import fr.lirmm.graphik.graal.homomorphism.bootstrapper.StarBootstrapper;
import fr.lirmm.graphik.graal.homomorphism.forward_checking.NFC2;
import fr.lirmm.graphik.util.collections.Trie;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.converter.ConversionException;
import fr.lirmm.graphik.util.stream.converter.Converter;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;
import fr.lirmm.graphik.util.stream.filter.Filter;
import fr.lirmm.graphik.util.stream.filter.FilterIterator;

/**
 * An homomorphism for Atomic query without constant or multiple occurences of a variables.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class AtomicQueryHomomorphismWithNegation extends AbstractHomomorphism<ConjunctiveQueryWithNegation, AtomSet> implements Homomorphism<ConjunctiveQueryWithNegation, AtomSet> {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private static final AtomicQueryHomomorphismWithNegation INSTANCE = new AtomicQueryHomomorphismWithNegation();

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public static AtomicQueryHomomorphismWithNegation instance() {
		return INSTANCE;
	}

	// /////////////////////////////////////////////////////////////////////////
	// HOMOMORPHISM METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public <U1 extends ConjunctiveQueryWithNegation, U2 extends AtomSet> CloseableIterator<Substitution> execute(U1 query, U2 data) throws HomomorphismException {
		try {
			Atom atom = query.getPositiveAtomSet().iterator().next();
			List<Term> ans = query.getAnswerVariables();
			CloseableIterator<Atom> atomsByPredicateIt = data.atomsByPredicate(atom.getPredicate());
			
			CloseableIterator<Substitution> subIt;
			if(ans.containsAll(atom.getVariables())) {
				subIt = new ConverterCloseableIterator<Atom, Substitution>(atomsByPredicateIt, new Atom2SubstitutionConverter(atom, ans));
			} else {
				ConverterCloseableIterator<Atom, Term[]> converterArrayIt = new ConverterCloseableIterator<Atom, Term[]>(atomsByPredicateIt, new Atom2ArrayConverter(atom, ans));
				FilterIterator<Term[], Term[]> filterIt = new FilterIterator<Term[], Term[]>(converterArrayIt, new UniqFilter());
				subIt = new ConverterCloseableIterator<Term[], Substitution>(filterIt, new Array2SubstitutionConverter(ans));
			}
			Set<Variable> frontier = query.getFrontierVariables();
			return new FilterIterator<Substitution, Substitution>(subIt, new NegFilter(query.getNegativeAtomSet(), frontier, data));

		} catch (AtomSetException e) {
			throw new HomomorphismException(e);
		}
	}


	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASS
	// /////////////////////////////////////////////////////////////////////////
	
	private static class UniqFilter implements Filter<Term[]> {

		Trie<Term, Boolean> sol = new Trie<Term, Boolean>();
		
		@Override
		public boolean filter(Term[] s) {
			return sol.put(true, s) == null;
		}
		
	}
	
	private static class NegFilter implements Filter<Substitution> {

		private AtomSet data;
		private InMemoryAtomSet head;
		
		public NegFilter(InMemoryAtomSet head, Set<Variable> frontier, AtomSet data) {
			this.data = data;
			this.head = head;
		}
		
		@Override
		public boolean filter(Substitution s) {
			try {
				ConjunctiveQuery nquery = DefaultConjunctiveQueryFactory.instance().create(s.createImageOf(this.head), Collections.<Term>emptyList());
				return !StaticExistentialHomomorphism.instance().exist(nquery, data);	
			} catch (HomomorphismException e) {
				// TODO treat this exception
				e.printStackTrace();
				throw new Error(e);
			}
		}
	}
	
	private static class NegFilterContains implements Filter<Substitution> {

		private AtomSet data;
		private InMemoryAtomSet nquery;
		
		public NegFilterContains(InMemoryAtomSet nquery, AtomSet data) {
			this.nquery = nquery;
			this.data = data;
		}
		
		@Override
		public boolean filter(Substitution s) {
			try {
				return !FullyInstantiatedQueryHomomorphism.instance().execute(s.createImageOf(nquery), data).hasNext();
			} catch (IteratorException e) {
				// TODO treat this exception
				e.printStackTrace();
				throw new Error("Untreated exception");
			} catch (HomomorphismException e) {
				// TODO treat this exception
				e.printStackTrace();
				throw new Error("Untreated exception");
			}
		}
		
	}

	private static class Atom2ArrayConverter implements Converter<Atom, Term[]> {
		
		private Map<Variable, Integer> variables = new TreeMap<Variable, Integer>();
		private List<Term> ans;
		
		public Atom2ArrayConverter(Atom query, List<Term> ans) {
			this.ans = ans;
			int i = 0;
			for(Term t : query) {
				if(ans.contains(t))	 {
					variables.put((Variable) t, i);
				}
				++i;
			}
		}
		
		public Atom2ArrayConverter(Atom query, List<Term> ans, Substitution rew) {
			this.ans = ans;
			int i = 0;
			for(Term t : query) {
				if(ans.contains(t))	 {
					variables.put((Variable) t, i);
				}
				++i;
			}
			for(Variable var : rew.getTerms()) {
				variables.put(var, variables.get(rew.createImageOf(var)));
			}
		}
		
		@Override
		public Term[] convert(Atom object) throws ConversionException {
			Term res[] = new Term[ans.size()]; 
			int i = -1;
			for (Term var : ans) { 
				res[++i] = object.getTerm(variables.get(var));
			}
			return res;
		}
	}
	
	private static class Array2SubstitutionConverter implements Converter<Term[], Substitution> {
		
		private List<Term> ans;
		
		public Array2SubstitutionConverter(List<Term> ansVar) {
			this.ans = ansVar;
		}
		
		@Override
		public Substitution convert(Term object[]) throws ConversionException {
			Substitution s = new HashMapSubstitution();
			int i = -1;
			for (Term var : ans) { 
				s.put((Variable) var, object[++i]);
			}
			return s;
		}
	}


}
