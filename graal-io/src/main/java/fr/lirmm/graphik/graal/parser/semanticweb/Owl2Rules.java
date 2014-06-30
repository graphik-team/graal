/* TODO
 * owl:IrreflexiveProperty
 * owl:SymmetricProperty
 * owl:AsymmetricProperty
 * owl:FunctionalProperty
 * owl:InverseFunctionalProperty
 * 
 */
package fr.lirmm.graphik.graal.parser.semanticweb;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.util.stream.AbstractReader;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class Owl2Rules extends AbstractReader<Object> {

	private static final Logger logger = LoggerFactory
			.getLogger(Owl2Rules.class);
	
	public static final String OWL_PREFIX = "http://www.w3.org/2002/07/owl#";
	
	public static final String OWL_ONTOLOGY = OWL_PREFIX + "Ontology";

	public static final String OWL_CLASS = OWL_PREFIX + "Class";

	public static final String OWL_TRANSITIVE_PROPERTY = OWL_PREFIX
														 + "TransitiveProperty";

	public static final String OWL_INVERSE_OF = OWL_PREFIX + "inverseOf";

	public static final String OWL_EQUIVALENT_PROPERTY = OWL_PREFIX
														 + "equivalentProperty";
	
	public static final String OWL_DATATYPE_PROPERTY = OWL_PREFIX + "DatatypeProperty";
	
	public static final String OWL_OBJECT_PROPERTY = OWL_PREFIX + "ObjectProperty";
	

	private Queue<Object> queue = new LinkedList<Object>();

	private Iterator<Object> reader;

	protected static final Term x = new Term("X", Type.VARIABLE);
	protected static final Term y = new Term("Y", Type.VARIABLE);
	protected static final Term z = new Term("Z", Type.VARIABLE);

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public Owl2Rules(Iterator<Atom> reader) {
		this.reader = new RDFS2Rules(reader);
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
		return !queue.isEmpty() || this.reader.hasNext();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.lirmm.graphik.util.stream.ObjectReader#next()
	 */
	@Override
	public Object next() {
		Object o = null;

		if (queue.isEmpty()) {
			o = this.reader.next();
			if (o instanceof Atom) {
				Atom a = (Atom) o;
				String predicateStr = a.getPredicate().toString();
				
				if(logger.isDebugEnabled()) 
					logger.debug(predicateStr);
				
				if (OWL_INVERSE_OF.equals(predicateStr)) {
					Rule rule = new DefaultRule();
					Predicate p1 = new Predicate(a.getTerm(0).toString(), 2);
					Predicate p2 = new Predicate(a.getTerm(1).toString(), 2);
					rule.getBody().add(new DefaultAtom(p1, x, y));
					rule.getHead().add(new DefaultAtom(p2, y, x));

					o = rule;
					// inverse
					queue.add(new DefaultRule(rule.getHead(), rule.getBody()));

				} else if (OWL_EQUIVALENT_PROPERTY.equals(predicateStr)) {
					Rule rule = new DefaultRule();
					Predicate p1 = new Predicate(a.getTerm(0).toString(), 2);
					Predicate p2 = new Predicate(a.getTerm(1).toString(), 2);
					rule.getBody().add(new DefaultAtom(p1, x, y));
					rule.getHead().add(new DefaultAtom(p2, x, y));

					o = rule;
					// inverse
					queue.add(new DefaultRule(rule.getHead(), rule.getBody()));
				} else if (OWL_TRANSITIVE_PROPERTY.equals(predicateStr)) {
					Rule rule = new DefaultRule();
					Predicate p = new Predicate(a.getTerm(0).toString(), 2);
					rule.getBody().add(new DefaultAtom(p, x, y));
					rule.getBody().add(new DefaultAtom(p, y, z));
					rule.getHead().add(new DefaultAtom(p, x, z));
					o = rule;
				}
			}
		} else {
			o = queue.poll();
		}

		return o;
	}
}
