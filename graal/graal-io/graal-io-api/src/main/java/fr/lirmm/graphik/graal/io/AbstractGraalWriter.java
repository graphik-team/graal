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
		// TODO: j'ai change ca en supposant que c'etait une erreur
		// verifie quand meme
		// (dans le classe DlgpWriter, ca n'a pas de sens que ce
		// soit un retour ligne ici, il faudra verifier pour les
		// autres writer)
		//this.write('\n');
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
