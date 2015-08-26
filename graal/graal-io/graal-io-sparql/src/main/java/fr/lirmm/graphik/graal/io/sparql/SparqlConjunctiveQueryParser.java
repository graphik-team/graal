/**
 * 
 */
package fr.lirmm.graphik.graal.io.sparql;

import java.util.LinkedList;
import java.util.List;

import fr.inria.acacia.corese.exceptions.QueryLexicalException;
import fr.inria.acacia.corese.exceptions.QuerySyntaxException;
import fr.inria.acacia.corese.triple.parser.ASTQuery;
import fr.inria.acacia.corese.triple.parser.Atom;
import fr.inria.acacia.corese.triple.parser.Exp;
import fr.inria.acacia.corese.triple.parser.ParserSparql1;
import fr.inria.acacia.corese.triple.parser.Triple;
import fr.inria.acacia.corese.triple.parser.Variable;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
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

	public static ConjunctiveQuery parse(String query) throws ParseException {
		List<Term> ans = new LinkedList<Term>();
		InMemoryAtomSet atomset = new LinkedListAtomSet();

		ConjunctiveQuery cq = new DefaultConjunctiveQuery();
		ASTQuery ast = ASTQuery.create(query);
		try {
			ParserSparql1.create(ast).parse();

			for (Variable v : ast.getSelectVar()) {
				ans.add(DefaultTermFactory.instance().createVariable(v.getName()));
			}

			for (Exp e : ast.getBody().getBody()) {
				if (e.isTriple()) {
					Triple t = e.getTriple();
					Atom arg0 = t.getArg(0);
					Atom arg1 = t.getArg(1);
					Atom p = t.getPredicate();
					if (p.getLongName().equals(URIUtils.RDF_TYPE.toString())) {
						if (arg1.isQName()) {
							Predicate predicate = new Predicate(URIUtils.createURI(arg1.getLongName()), 1);
							atomset.add(new DefaultAtom(predicate, parseTerm(arg0)));
						} else {
							throw new ParseException("Variable over type is not permitted");
						}
					} else {
						Predicate predicate = new Predicate(URIUtils.createURI(p.getLongName()), 2);
						atomset.add(new DefaultAtom(predicate, parseTerm(arg0), parseTerm(arg1)));
					}
				}
			}
		} catch (QueryLexicalException e) {
			throw new ParseException("Error while parsing the query", e);
		} catch (QuerySyntaxException e) {
			throw new ParseException("Error while parsing the query", e);
		}

		return new DefaultConjunctiveQuery(atomset, ans);
	}

	// /////////////////////////////////////////////////////////////////////////
	// OBJECT OVERRIDE METHODS
	// /////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////

	private static Term parseTerm(Atom arg) {
		Term term;
		if (arg.isQName()) {
			term = DefaultTermFactory.instance().createConstant(arg.getLongName());
		} else if (arg.isLiteral()) {
			term = DefaultTermFactory.instance()
					.createLiteral(URIUtils.createURI(arg.getDatatype()), arg.getLongName());
		} else {
			term = DefaultTermFactory.instance().createVariable(arg.getName());
		}
		return term;
	}
}
