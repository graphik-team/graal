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
package fr.lirmm.graphik.graal.io.rdf;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class OWLFilter extends AbstractReader<Object> {

	private Iterator<Object> reader;
	private Object next;

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public OWLFilter(Iterator<Object> reader) {
		this.reader = reader;
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
		while (this.next == null && this.reader.hasNext()) {
			Object o = this.reader.next();
			if(o instanceof Atom) {
				String predicateStr = ((Atom) o).getPredicate().toString();
				if(!RDFS2Rules.RDFS_LABEL.equals(predicateStr) &&
						!RDFS2Rules.RDFS_COMMENT.equals(predicateStr) &&
						!Owl2Rules.OWL_CLASS.equals(predicateStr) &&
						!Owl2Rules.OWL_OBJECT_PROPERTY.equals(predicateStr) &&
						!Owl2Rules.OWL_DATATYPE_PROPERTY.equals(predicateStr)) {
					this.next = o;
				}
			} else {
				this.next = o;
			}
			
		} 
		return this.next != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#next()
	 */
	@Override
	public Object next() {
		this.hasNext();
		Object o = this.next;
		this.next = null;
		return o;
	}

}
