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
import fr.lirmm.graphik.graal.core.Term;
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

	public abstract void writeComment(String comment) throws IOException;

	public void write(Object o) throws IOException {
		if (o instanceof Atom) {
			this.write((Atom) o);
		} else if (o instanceof NegativeConstraint) {
			this.write(o);
		} else if (o instanceof Rule) {
			this.write(o);
		} else if (o instanceof ConjunctiveQuery) {
			this.write(o);
		} else if (o instanceof Prefix) {
			this.write(o);
		} else if (o instanceof String) {
			this.write(o);
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

	protected void write(Atom atom) throws IOException {
		if (atom.equals(Atom.BOTTOM)) {
			this.writeBottom();
		} else if (atom.getPredicate().equals(Predicate.EQUALITY)) {
			this.writeEquality(atom.getTerm(0), atom.getTerm(1));
		} else {
			this.writeAtom(atom);
		}
	}

	/**
	 * @param atom
	 */
	protected abstract void writeAtom(Atom atom) throws IOException;

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
