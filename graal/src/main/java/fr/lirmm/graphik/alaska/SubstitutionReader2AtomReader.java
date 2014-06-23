/**
 * 
 */
package fr.lirmm.graphik.alaska;

import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.stream.SubstitutionReader;
import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class SubstitutionReader2AtomReader extends AbstractReader<Atom> {

	private Atom atom;
	private SubstitutionReader reader;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public SubstitutionReader2AtomReader(Atom atom, SubstitutionReader reader) {
		this.reader = reader;
		this.atom = atom;
	}

	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.reader.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#next()
	 */
	@Override
	public Atom next() {
		return this.reader.next().getSubstitut(atom);
	}

}
