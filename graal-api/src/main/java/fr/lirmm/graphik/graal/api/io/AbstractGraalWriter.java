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
package fr.lirmm.graphik.graal.api.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.NegativeConstraint;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.factory.AtomFactory;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterable;
import fr.lirmm.graphik.util.stream.CloseableIterator;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractGraalWriter extends AbstractWriter implements
		GraalWriter {

	private AtomFactory atomFactory;

	protected AbstractGraalWriter(Writer out, AtomFactory atomFactory) {
		super(out);
		this.atomFactory = atomFactory;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public abstract AbstractGraalWriter writeComment(String comment) throws IOException;

	@Override
	public AbstractGraalWriter write(Object o) throws IOException {
		if (o instanceof Atom) {
			this.write((Atom) o);
		} else if (o instanceof NegativeConstraint) {
			this.write((NegativeConstraint) o);
		} else if (o instanceof Rule) {
			this.write((Rule) o);
		} else if (o instanceof ConjunctiveQuery) {
			this.write((ConjunctiveQuery) o);
		} else if (o instanceof Prefix) {
			this.write((Prefix) o);
		} else if (o instanceof String) {
			this.write((String) o);
		} else if (o instanceof AtomSet) {
			this.write((AtomSet) o);
		} else if (o instanceof Iterable<?>) {
			this.writeIterable((Iterable<?>) o);
		} else if (o instanceof Iterator<?>) {
			this.writeIterator((Iterator<?>) o);
		} else if (o instanceof CloseableIterable<?>) {
			this.writeCloseableIterable((CloseableIterable<?>) o);
		} else if (o instanceof CloseableIterator<?>) {
			this.writeCloseableIterator((CloseableIterator<?>) o);
		} else {
			// fallback
			this.writeln(o.toString());
		}
		return this;
	}

	@Override
	public AbstractGraalWriter write(Object... objects) throws IOException {
		for (Object o : objects) {
			this.write(o);
		}
		return this;
	}


	protected void writeIterable(Iterable<?> it) throws IOException {
		for (Object o : it)
			this.write(o);
	}

	protected void writeIterator(Iterator<?> it) throws IOException {
		while (it.hasNext())
			this.write(it.next());
	}
	
	protected void writeCloseableIterable(CloseableIterable<?> it) throws IOException {
		this.writeCloseableIterator(it.iterator());
	}
	
	protected void writeCloseableIterator(CloseableIterator<?> it) throws IOException {
		while (it.hasNext())
			this.write(it.next());
	}

	protected final void writeAtom(Atom atom) throws IOException {
		if (atom.equals(this.atomFactory.getBottom())) {
			this.writeBottom();
		} else if (atom.getPredicate().equals(Predicate.EQUALITY)) {
			this.writeEquality(atom.getTerm(0), atom.getTerm(1));
		} else {
			this.writeStandardAtom(atom);
		}
	}

	/**
	 * 
	 * @param atom
	 * @throws IOException
	 */
	protected abstract void writeStandardAtom(Atom atom) throws IOException;

	/**
	 * @param term
	 * @param term2
	 * @throws IOException
	 */
	protected abstract void writeEquality(Term term, Term term2)
			throws IOException;

	/**
	 * 
	 */
	protected abstract void writeBottom() throws IOException;

}
