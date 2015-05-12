/**
 * 
 */
package fr.lirmm.graphik.graal.io.rdf;

import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultRule;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.AbstractParser;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class RDFS2Rules extends AbstractParser<Object> {

	public static final String RDFS_PREFIX = "http://www.w3.org/2000/01/rdf-schema#";

	/** range(p,c) => c(Y) :- p(X,Y) */
	public static final String RDFS_RANGE = RDFS_PREFIX + "range";

	/** domain(p,c) => c(X) :- p(X,Y) */
	public static final String RDFS_DOMAIN = RDFS_PREFIX + "domain";
	
	
	/** subClassOf(c1, c2) => c2(X) :- c1(X) */
	public static final String RDFS_SUB_CLASS_OF = RDFS_PREFIX + "subClassOf";
	
	/** subPropertyOf(p1, p2) => p2(X,Y) :- p1(X,Y); */
	public static final String RDFS_SUB_PROPERTY_OF = RDFS_PREFIX
													  + "subPropertyOf";
	
	/** */
	public static final String RDFS_LABEL = RDFS_PREFIX + "label";
	
	/** */
	public static final String RDFS_COMMENT = RDFS_PREFIX + "comment";

	protected static final Term X = DefaultTermFactory.instance()
			.createVariable("X");
	protected static final Term Y = DefaultTermFactory.instance()
			.createVariable("Y");
	protected static final Term Z = DefaultTermFactory.instance()
			.createVariable("Z");

	private Iterator<Atom> reader;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public RDFS2Rules(Iterator<Atom> atomReader) {
		this.reader = new RDF2Atom(atomReader);
	}
	// /////////////////////////////////////////////////////////////////////////
	// METHODS
	// /////////////////////////////////////////////////////////////////////////

	@Override
	public Object next() {
		Object o = null;
		Atom a = this.reader.next();

		String predicateLabel = a.getPredicate().toString();
		if (RDFS_RANGE.equals(predicateLabel)) {
			Rule rule = new DefaultRule();
			Predicate p = new Predicate(a.getTerm(0).toString(), 2);
			rule.getBody().add(new DefaultAtom(p, X, Y));
			p = new Predicate(a.getTerm(1).toString(), 1);
			rule.getHead().add(new DefaultAtom(p, Y));
			o = rule;

		} else if (RDFS_DOMAIN.equals(predicateLabel)) {
			Rule rule = new DefaultRule();
			Predicate p = new Predicate(a.getTerm(0).toString(), 2);
			rule.getBody().add(new DefaultAtom(p, X, Y));
			p = new Predicate(a.getTerm(1).toString(), 1);
			rule.getHead().add(new DefaultAtom(p, X));
			o = rule;
			
		} else if (RDFS_SUB_CLASS_OF.equals(predicateLabel)) {
			Rule rule = new DefaultRule();
			Predicate p1 = new Predicate(a.getTerm(0).toString(), 1);
			Predicate p2 = new Predicate(a.getTerm(1).toString(), 1);
			rule.getBody().add(new DefaultAtom(p1, X));
			rule.getHead().add(new DefaultAtom(p2, X));
			o = rule;
			
		} else if (RDFS_SUB_PROPERTY_OF.equals(predicateLabel)) {
			Rule rule = new DefaultRule();
			Predicate p1 = new Predicate(a.getTerm(0).toString(), 2);
			Predicate p2 = new Predicate(a.getTerm(1).toString(), 2);
			rule.getBody().add(new DefaultAtom(p1, X, Y));
			rule.getHead().add(new DefaultAtom(p2, X, Y));
			o = rule;
			
		} else {
			o = a;
		}

		return o;
	}

	@Override
	public boolean hasNext() {
		return this.reader.hasNext();
	}
	
	@Override
	public void close() {
		
	}
	
}
