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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.tuple.Pair;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithNegatedParts;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.RulesCompilation;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismWithCompilation;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorAdapter;
import fr.lirmm.graphik.util.stream.CloseableIteratorAggregator;
import fr.lirmm.graphik.util.stream.converter.ConverterCloseableIterator;
import fr.lirmm.graphik.util.stream.filter.AndFilter;
import fr.lirmm.graphik.util.stream.filter.Filter;
import fr.lirmm.graphik.util.stream.filter.FilterIterator;

/**
 * An homomorphism for Atomic query without constant or multiple occurences of a variables.
 * 
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class AtomicQueryHomomorphismWithNegatedParts extends AbstractHomomorphismWithCompilation<ConjunctiveQueryWithNegatedParts, AtomSet> implements HomomorphismWithCompilation<ConjunctiveQueryWithNegatedParts, AtomSet> {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	private static final AtomicQueryHomomorphismWithNegatedParts INSTANCE = new AtomicQueryHomomorphismWithNegatedParts();

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	public static AtomicQueryHomomorphismWithNegatedParts instance() {
		return INSTANCE;
	}

	// /////////////////////////////////////////////////////////////////////////
	// HOMOMORPHISM METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public CloseableIterator<Substitution> execute(ConjunctiveQueryWithNegatedParts query, AtomSet data, RulesCompilation compilation, Substitution s) throws HomomorphismException {
		try {
			Atom atom = query.getPositivePart().iterator().next();
			List<Term> ans = query.getAnswerVariables();
			
			List<CloseableIterator<Substitution>> iteratorsList = new LinkedList<CloseableIterator<Substitution>>();
			for (Pair<Atom, Substitution> im : compilation.getRewritingOf(atom)) {
				iteratorsList.add(new ConverterCloseableIterator<Atom, Substitution>(data.match(im.getLeft(), s),
						new Atom2SubstitutionConverter(im.getLeft(), ans, im.getRight())));
			}
			
			CloseableIterator<Substitution> subIt = new CloseableIteratorAggregator<Substitution>(
					new CloseableIteratorAdapter<CloseableIterator<Substitution>>(iteratorsList.iterator()));

			
			// manage negative parts
			Set<Variable> variables = query.getPositivePart().getVariables();
			@SuppressWarnings("rawtypes")
			Filter[] filters = new Filter[query.getNegatedParts().size()];
			int i = 0;
			for(InMemoryAtomSet negPart : query.getNegatedParts()) {
				Set<Variable> frontier = SetUtils.intersection(variables, negPart.getVariables());
				filters[i++] =  new NegFilter(negPart, frontier, data, compilation);
			}
			@SuppressWarnings("unchecked")
			Filter<Substitution> filter = new AndFilter<Substitution>(filters);
			return new FilterIterator<Substitution, Substitution>(subIt, filter);

		} catch (AtomSetException e) {
			throw new HomomorphismException(e);
		}
	}

}
