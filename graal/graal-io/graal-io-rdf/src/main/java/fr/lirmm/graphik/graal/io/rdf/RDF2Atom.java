/**
 * 
 */
package fr.lirmm.graphik.graal.io.rdf;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.AbstractAtomParser;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class RDF2Atom extends AbstractAtomParser {

	
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

	private Atom atom = null;
	//private Map<String, LinkedList<Atom>> atomPendingBlankNodeResolution = new TreeMap<String, LinkedList<Atom>>();
	private Iterator<Atom> reader;
	

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public RDF2Atom(Iterator<Atom> atomReader) {
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
	public boolean hasNext() {
		while (this.atom == null && this.reader.hasNext()) {
			Atom a = this.reader.next();
			String predicateStr = a.getPredicate().toString();
			
			if(LOGGER.isDebugEnabled())
				LOGGER.debug("RDF Predicate: " + predicateStr);
			
			if(RDF_TYPE.equals(predicateStr)) {
				Predicate p = new Predicate(a.getTerm(1).toString(), 1);
				this.atom = new DefaultAtom(p, a.getTerm(0));
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
				this.atom = a;
			}
			
		}
		return this.atom != null;
	}

	/**
	 * @param term
	 * @return
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
	public Atom next() {
		if(this.atom == null)
			this.hasNext();
		
		Atom a = this.atom;
		this.atom = null;
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
    		
    		if(!RDF_NIL.equals(collection.getLast())) {
    			this.collectionMap.put(collection.getFirst(), collection);
    			this.reverseCollectionMap.put(collection.getLast(), collection);
    		} else {
    			collection.removeLast();
    			List<Term> terms = new LinkedList<Term>();
    			for(String s : collection) {
					terms.add(DefaultTermFactory.instance().createConstant(s));
    			}
    			this.atom = new DefaultAtom(PREDICATE_RDF_LIST, terms);
    		}
    		
//    		System.out.print("#### new Collection ["   );
//    		for(String s : collection) {
//    			System.out.print(s + ", ");
//    		}
//    		System.out.println("]");
		}
		
	}

}
