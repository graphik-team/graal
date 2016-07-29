/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
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
package fr.lirmm.graphik.graal.io.rdf;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.Predicate;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.io.Parser;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.AbstractCloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class RDF2Atom extends AbstractCloseableIterator<Object> implements Parser<Object> {

	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(RDF2Atom.class);
	
	public static final String RDF_PREFIX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	/** */
	public static final String RDF_TYPE = RDF_PREFIX + "type";
	
	public static final String RDF_LIST = RDF_PREFIX + "List";
	public static final String RDF_FIRST = RDF_PREFIX + "first";
	public static final String RDF_REST = RDF_PREFIX + "rest";
	public static final String RDF_NIL = RDF_PREFIX + "nil";
	
	public static final Predicate PREDICATE_RDF_LIST = new Predicate(RDF_LIST, 0);
	
	/*private class BlankNodeComparator implements Comparator<Atom> {

		@Override
		public int compare(Atom atom1, Atom atom2) {
			return atom1.toString().compareTo(atom2.toString());
		}
		
	}
	private MemoryGraphAtomSet atomset = new MemoryGraphAtomSet();*/
	
	private Map<String, String> firstMap = new TreeMap<String, String>();
	private Map<String, String> restMap = new TreeMap<String, String>();
	private Map<String, LinkedList<String>> collectionMap = new TreeMap<String, LinkedList<String>>();
	private Map<String, LinkedList<String>> reverseCollectionMap = new TreeMap<String, LinkedList<String>>();

	private Object object = null;
	//private Map<String, LinkedList<Atom>> atomPendingBlankNodeResolution = new TreeMap<String, LinkedList<Atom>>();
	private CloseableIterator<Object> reader;
	

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public RDF2Atom(RDFRawParser atomReader) {
		this.reader = atomReader;
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
	public boolean hasNext() throws IteratorException {
		while (this.object == null && this.reader.hasNext()) {
			Object o = this.reader.next();
			if(o instanceof Prefix) {
				this.object = o;
			} else if (o instanceof Atom) {
				Atom a = (Atom) o;
				String predicateStr = a.getPredicate().getIdentifier().toString();
    			
    			if(LOGGER.isDebugEnabled())
    				LOGGER.debug("RDF Predicate: " + predicateStr);
    			
    			if(RDF_TYPE.equals(predicateStr)) {
					Predicate p = new Predicate(a.getTerm(1).getIdentifier(), 1);
    				this.object = new DefaultAtom(p, a.getTerm(0));
    			} else if (RDF_FIRST.equals(predicateStr)) {
    				String rest = this.restMap.remove(a.getTerm(0).toString());
    				if(rest == null) {
    					this.firstMap.put(a.getTerm(0).toString(), a.getTerm(1).toString());
    				} else {
    					LinkedList<String> collection = new LinkedList<String>();
    					collection.add(a.getTerm(0).toString());
    					collection.add(a.getTerm(1).toString());
    					collection.add(rest);
    					this.newCollection(collection);
    				}
    			} else if (RDF_REST.equals(predicateStr)) {
    				String first = this.firstMap.remove(a.getTerm(0).toString());
    				if(first == null) {
    					this.restMap.put(a.getTerm(0).toString(), a.getTerm(1).toString());
    				} else {
    					LinkedList<String> collection = new LinkedList<String>();
    					collection.add(a.getTerm(0).toString());
    					collection.add(first);
    					collection.add(a.getTerm(1).toString());
    					this.newCollection(collection);
    				}
    			} else {
    				this.object = a;
    			}
			}
			
		}
		return this.object != null;
	}

	/**
	 * @param term
	 * @return
	 * @throws IteratorException
	 */
	/*private boolean isBlankNode(Term term) {
		return term.toString().startsWith("_:");
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#next()
	 */
	@Override
	public Object next() throws IteratorException {
		if (this.object == null)
			this.hasNext();
		
		Object a = this.object;
		this.object = null;
		return a;
	}
	
	@Override
	public void close() {
	}
	
	
	private void newCollection(LinkedList<String> collection) {
		
		LinkedList<String> startOfCollection = this.reverseCollectionMap.remove(collection.getFirst());
		if(startOfCollection != null) {
			this.collectionMap.remove(startOfCollection.getFirst());
			collection.removeFirst();
			startOfCollection.removeLast();
			startOfCollection.addAll(collection);
			this.newCollection(startOfCollection);
		} else {

			if (!RDF_NIL.equals(collection.getLast())) {
				this.collectionMap.put(collection.getFirst(), collection);
				this.reverseCollectionMap.put(collection.getLast(), collection);
			} else {
				collection.removeLast();
				List<Term> terms = new LinkedList<Term>();
				for (String s : collection) {
					terms.add(DefaultTermFactory.instance().createConstant(s));
				}
				this.object = new DefaultAtom(PREDICATE_RDF_LIST, terms);
			}

			// System.out.print("#### new Collection [" );
			// for(String s : collection) {
			// System.out.print(s + ", ");
			// }
			// System.out.println("]");
		}
		
	}

}
