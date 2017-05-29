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
package fr.lirmm.graphik.graal.store.triplestore.rdf4j;

import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.TupleQueryResult;

import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

abstract class AbstractTupleQueryResultIterator<E> extends AbstractCloseableIterator<E> {

	protected TupleQueryResult it;
	
	protected AbstractTupleQueryResultIterator(TupleQueryResult it) {
		this.it = it;
	}

	@Override
	public void close() {
		try {
			this.it.close();
		} catch (QueryEvaluationException e) {
			if (RDF4jStore.LOGGER.isErrorEnabled()) {
				RDF4jStore.LOGGER.error("Error during iteration closing", e);
			}
			throw new RuntimeException("An error occurs while closing iterator", e);
		}
	}

	@Override
	public boolean hasNext() throws IteratorException {
		try {
			return this.it.hasNext();
		} catch (QueryEvaluationException e) {
			if (RDF4jStore.LOGGER.isErrorEnabled()) {
				RDF4jStore.LOGGER.error("Error during iteration", e);
			}
			throw new IteratorException("An error occurs during iteration", e);
		}
	}

}