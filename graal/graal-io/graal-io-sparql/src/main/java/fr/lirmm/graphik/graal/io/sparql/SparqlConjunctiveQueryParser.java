/**
 * 
 */
package fr.lirmm.graphik.graal.io.sparql;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.Node;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementData;
import org.apache.jena.sparql.syntax.ElementDataset;
import org.apache.jena.sparql.syntax.ElementExists;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementNotExists;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.ElementVisitor;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomFactory;
import fr.lirmm.graphik.graal.core.factory.ConjunctiveQueryFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.ParseError;
import fr.lirmm.graphik.graal.io.ParseException;
import fr.lirmm.graphik.util.URIUtils;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SparqlConjunctiveQueryParser {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTORS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////

	public static ConjunctiveQuery parse(String queryString) throws ParseException {
		List<Term> ans = new LinkedList<Term>();
		ConjunctiveQuery cq = ConjunctiveQueryFactory.instance().create();

		Query sparql = QueryFactory.create(queryString);
		if (sparql.isSelectType()) {
			List<String> resultVars = sparql.getResultVars();
			for (String v : sparql.getResultVars()) {
				ans.add(DefaultTermFactory.instance().createVariable(v));
			}

		}

		ElementVisitorImpl visitor = new ElementVisitorImpl();
		sparql.getQueryPattern().visit(visitor); // The meat of the query, the
												 // WHERE bit
		InMemoryAtomSet atomset = visitor.getAtomSet();

		return ConjunctiveQueryFactory.instance().create(atomset, ans);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static final class ElementVisitorImpl implements ElementVisitor {

		private InMemoryAtomSet atomset = new LinkedListAtomSet();

		public InMemoryAtomSet getAtomSet() {
			return atomset;
		}

		@Override
		public void visit(ElementSubQuery arg0) {
			throw new ParseError("SubQuery not allowed");
		}

		@Override
		public void visit(ElementService arg0) {
			throw new ParseError("ElementService not allowed");
		}

		@Override
		public void visit(ElementMinus arg0) {
			throw new ParseError("ElementMinus not allowed");
		}

		@Override
		public void visit(ElementNotExists arg0) {
			throw new ParseError("ElementNotExists not allowed");
		}

		@Override
		public void visit(ElementExists arg0) {
			throw new ParseError("ElementExists not allowed");
		}

		@Override
		public void visit(ElementNamedGraph arg0) {
			throw new ParseError("ElementNamedGraph not allowed");
		}

		@Override
		public void visit(ElementDataset arg0) {
			throw new ParseError("ElementDataset not allowed");
		}

		@Override
		public void visit(ElementGroup arg0) {
			for (Element e : arg0.getElements()) {
				e.visit(this);
			}
		}

		@Override
		public void visit(ElementOptional arg0) {
			throw new ParseError("ElementOptional not allowed");
		}

		@Override
		public void visit(ElementUnion arg0) {
			throw new ParseError("ElementUnion not allowed");
		}

		@Override
		public void visit(ElementData arg0) {
			throw new ParseError("ElementData not allowed");
		}

		@Override
		public void visit(ElementBind arg0) {
			throw new ParseError("ElementBind not allowed");
		}

		@Override
		public void visit(ElementAssign arg0) {
			throw new ParseError("ElementAssign not allowed");
		}

		@Override
		public void visit(ElementFilter arg0) {
			throw new ParseError("ElementFilter not allowed");
		}

		@Override
		public void visit(ElementPathBlock arg0) {
			Iterator<TriplePath> it = arg0.patternElts();
			while (it.hasNext()) {
				TriplePath triple = it.next();
				if (triple.isTriple()) {
					Node subject = triple.getSubject();
					Node object = triple.getObject();
					Node predicate = triple.getPredicate();
					if (predicate.getURI().equals(URIUtils.RDF_TYPE.toString())) {
						atomset.add(AtomFactory.instance().create(parsePredicate(object, 1), parseTerm(subject)));
					} else {
						atomset.add(AtomFactory.instance().create(parsePredicate(predicate, 2), parseTerm(subject),
								parseTerm(object)));
					}
				} else {
					throw new ParseError("Path is not allowed.");
				}

				// triple.Triple t = e.getTriple();
				// Atom arg0 = t.getArg(0);
				// Atom arg1 = t.getArg(1);
				// Atom p = t.getPredicate();
				// if (p.getLongName().equals(URIUtils.RDF_TYPE.toString())) {
				// if (arg1.isQName()) {
				// Predicate predicate = new
				// Predicate(URIUtils.createURI(arg1.getLongName()), 1);
				// atomset.add(new DefaultAtom(predicate, parseTerm(arg0)));
				// } else {
				// throw new
				// ParseException("Variable over type is not permitted");
				// }
				// } else {
				// Predicate predicate = new
				// Predicate(URIUtils.createURI(p.getLongName()), 2);
				// atomset.add(new DefaultAtom(predicate, parseTerm(arg0),
				// parseTerm(arg1)));
				// }
			}
		}

		/**
		 * @param node
		 * @return
		 */
		private Term parseTerm(Node node) {
			Term term;
			if (node.isURI()) {
				term = DefaultTermFactory.instance().createConstant(node.getURI());
			} else if (node.isLiteral()) {
				term = DefaultTermFactory.instance().createLiteral(
						URIUtils.createURI(node.getLiteralDatatype().getURI()), node.getLiteralValue());
			} else if (node.isVariable()) {
				term = DefaultTermFactory.instance().createVariable(node.getName());
			} else {
				throw new ParseError("Unknow error on " + node);
			}
			return term;
		}

		private Predicate parsePredicate(Node node, int arity) {
			Predicate predicate;
			if (node.isURI()) {
				predicate = new Predicate(URIUtils.createURI(node.getURI()), arity);
			} else {
				throw new ParseError("Unknow error on " + node);
			}
			return predicate;
		}

		@Override
		public void visit(ElementTriplesBlock arg0) {
			throw new ParseError("ElementTriplesBlock not allowed");
		}
	}


	public static void main(String[] args) throws ParseException {
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
							 + "PREFIX : <http://www.lehigh.edu/~zhp2/2004/0401/univ-bench.owl#>"
							 + "SELECT DISTINCT * "
							 + "WHERE"
							 + "{"
							 + "	?x  :worksFor 1."
							 + "	?x  :affiliatedOrganizationOf ?y."
							 + "	?x :knows ?name . "
							 + "}";
		System.out.println(parse(queryString));
	}
}
