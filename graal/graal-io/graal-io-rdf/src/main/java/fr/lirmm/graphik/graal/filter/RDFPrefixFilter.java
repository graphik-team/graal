/**
 * 
 */
package fr.lirmm.graphik.graal.filter;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class RDFPrefixFilter extends AbstractReader<Atom>{

	private Iterator<Atom> reader;
	private String prefix;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public RDFPrefixFilter(Iterator<Atom> reader, String prefix) {
		this.reader = reader;
		this.prefix = prefix;
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
		Atom a= this.reader.next();
		Predicate p = a.getPredicate();
		p = new Predicate(this.filter(p.getIdentifier()), p.getArity());
		a = new DefaultAtom(p, a.getTerms());
		return a;
	}

	/**
	 * @param label
	 * @return
	 */
	private String filter(String label) {
		return label.replaceFirst(prefix, "");
	}

}
