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
package fr.lirmm.graphik.util.stream.filter;

import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;


/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class FilterCloseableIterator<U, T> extends AbstractCloseableIterator<T> {

	private final CloseableIterator<U> it;
	private final Filter<U> filter;
	private T next;

	public FilterCloseableIterator(CloseableIterator<U> it, Filter<U> filter) {
		this.filter = filter;
		this.it = it;
		this.next = null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasNext() throws IteratorException {
		while (this.next == null && this.it.hasNext()) {
			U o = this.it.next();
			if(this.filter.filter(o)) {
				this.next = (T) o;
			}
		}
		return this.next != null;
	}

	@Override
	public T next() throws IteratorException {
		this.hasNext();
		T t = this.next;
		this.next = null;
		return t;
	}

	@Override
	public void close() {
		this.it.close();
	}

}
