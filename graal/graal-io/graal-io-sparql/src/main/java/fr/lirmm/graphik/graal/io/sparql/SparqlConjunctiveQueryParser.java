/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
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
package fr.lirmm.graphik.graal.io.sparql;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementAssign;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementData;
import com.hp.hpl.jena.sparql.syntax.ElementDataset;
import com.hp.hpl.jena.sparql.syntax.ElementExists;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementNotExists;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;

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

		Query sparql = QueryFactory.create(queryString);
		if (sparql.isSelectType()) {
			for (String v : sparql.getResultVars()) {
				ans.add(DefaultTermFactory.instance().createVariable(v));
			}
		}

		ElementVisitorImpl visitor = new ElementVisitorImpl();
		sparql.getQueryPattern().visit(visitor);
		InMemoryAtomSet atomset = visitor.getAtomSet();

		return ConjunctiveQueryFactory.instance().create(atomset, ans);
	}

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE CLASSES
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
				if (node.getLiteralValue() instanceof String) {
					// FIXME Jena ARQ Bug fix
					term = DefaultTermFactory.instance().createLiteral(URIUtils.XSD_STRING, node.getLiteralValue());
				} else {
					term = DefaultTermFactory.instance().createLiteral(
							URIUtils.createURI(node.getLiteralDatatypeURI()), node.getLiteralValue());
				}
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
