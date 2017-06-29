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
package fr.lirmm.graphik.graal.forward_chaining.rule_applier;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQueryWithNegatedParts;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplicationException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.core.RuleWrapper2ConjunctiveQueryWithNegatedParts;
import fr.lirmm.graphik.graal.homomorphism.SmartHomomorphism;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class RestrictedChaseRuleApplier<T extends AtomSet> implements RuleApplier<Rule, T> {

	private static final RuleApplier<Rule, AtomSet> FALLBACK = new DefaultRuleApplier<AtomSet>();

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public boolean apply(Rule rule, T atomSet) throws RuleApplicationException {
		try {
			boolean res = false;
			ConjunctiveQueryWithNegatedParts query = new RuleWrapper2ConjunctiveQueryWithNegatedParts(rule);
			CloseableIterator<Substitution> results;
			
			results = SmartHomomorphism.instance().execute(query, atomSet);
			while (results.hasNext()) {
				res = true;
				Substitution proj = results.next();
	
				// replace variables by fresh symbol
				for (Variable t : rule.getExistentials()) {
					proj.put(t, atomSet.getFreshSymbolGenerator().getFreshSymbol());
				}
	
				CloseableIteratorWithoutException<Atom> it = proj.createImageOf(rule.getHead()).iterator();
				while (it.hasNext()) {
					atomSet.add(it.next());
				}
			}
			
			return res;
		} catch (HomomorphismException e) {
			throw new RuleApplicationException("", e);
		} catch (AtomSetException e) {
			throw new RuleApplicationException("", e);
		} catch (IteratorException e) {
			throw new RuleApplicationException("", e);
		}
	}

	@Override
	public  CloseableIterator<Atom> delegatedApply(Rule rule, T atomSet) throws RuleApplicationException {
		try {
			ConjunctiveQueryWithNegatedParts query = new RuleWrapper2ConjunctiveQueryWithNegatedParts(rule);
			CloseableIterator<Substitution> results = SmartHomomorphism.instance().execute(query, atomSet);
			return new RuleApplierIterator(results, rule, atomSet);
		} catch (HomomorphismException e) {
			throw new RuleApplicationException("", e);
		}
	}

	@Override
	public  CloseableIterator<Atom> delegatedApply(Rule rule, T atomSetOnWichQuerying, T atomSetOnWichCheck)
	    throws RuleApplicationException {
		if(atomSetOnWichQuerying == atomSetOnWichCheck) {
			return this.delegatedApply(rule, atomSetOnWichQuerying);
		} else {
			return FALLBACK.delegatedApply(rule, atomSetOnWichQuerying, atomSetOnWichCheck);
		}
	}

	
	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static class RuleApplierIterator extends AbstractCloseableIterator<Atom> {

		private CloseableIterator<Substitution> substitutionIt;
		private CloseableIterator<Atom> localIt;
		private boolean hasNextCallDone;
		private Rule rule;
		private AtomSet atomset;
		
		public RuleApplierIterator(CloseableIterator<Substitution> it, Rule rule, AtomSet atomset) {
			this.substitutionIt = it;
			this.rule = rule;
			this.atomset = atomset;
			this.hasNextCallDone = false;
			this.localIt = null;
		}
		
		@Override
		public boolean hasNext() throws IteratorException {
			if (!this.hasNextCallDone) {
				this.hasNextCallDone = true;
				
				if (this.localIt != null && !this.localIt.hasNext()) {
					this.localIt.close();
					this.localIt = null;
				}
				while ((this.localIt == null || !this.localIt.hasNext()) && this.substitutionIt.hasNext()) {
					Substitution proj = this.substitutionIt.next();
					
					// replace variables by fresh symbol
					for (Variable t : rule.getExistentials()) {
						proj.put(t, atomset.getFreshSymbolGenerator().getFreshSymbol());
					}
		
					localIt = proj.createImageOf(rule.getHead()).iterator();
				}
			}
			return this.localIt != null && this.localIt.hasNext();
		}

		@Override
		public Atom next() throws IteratorException {
			if (!this.hasNextCallDone)
				this.hasNext();

			this.hasNextCallDone = false;

			return this.localIt.next();
		}

		@Override
		public void close() {
			if(localIt != null) {
				localIt.close();
			}
			substitutionIt.close();
		}
		
	}
}
