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
package fr.lirmm.graphik.graal.forward_chaining.rule_applier;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseHaltingCondition;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

class RuleApplierIterator extends AbstractCloseableIterator<Atom> {

	private CloseableIterator<Substitution> substitutionIt;
	private CloseableIterator<Atom> localIt;
	private boolean hasNextCallDone;
	private Rule rule;
	private ChaseHaltingCondition haltingCondition;
	private AtomSet atomset;
	
	public RuleApplierIterator(CloseableIterator<Substitution> it, Rule rule, AtomSet atomset, ChaseHaltingCondition haltingCondition) {
		this.substitutionIt = it;
		this.localIt = null;
		this.hasNextCallDone = false;
		this.rule = rule;
		this.haltingCondition = haltingCondition;
		this.atomset = atomset;
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
				try {
					localIt = haltingCondition.apply(rule, substitutionIt.next(), atomset);
				} catch (HomomorphismFactoryException e) {
					throw new IteratorException("Error during rule application", e);
				} catch (HomomorphismException e) {
					throw new IteratorException("Error during rule application", e);
				}
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