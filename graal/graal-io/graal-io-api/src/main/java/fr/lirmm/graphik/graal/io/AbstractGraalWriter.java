/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.util.Prefix;


/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public abstract class AbstractGraalWriter extends AbstractWriter implements
		GraalWriter {

	protected AbstractGraalWriter(Writer out) {
		super(out);
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	@Override
	public abstract void writeComment(String comment) throws IOException;

	public void write(Object o) throws IOException {
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
		}
	}

	public void writeIterable(Iterable<?> it) throws IOException {
		for (Object o : it)
			this.write(o);
	}

	public void writeIterator(Iterator<?> it) throws IOException {
		while (it.hasNext())
			this.write(it.next());
	}

	protected final void writeAtom(Atom atom) throws IOException {
		if (atom.equals(Atom.BOTTOM)) {
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
