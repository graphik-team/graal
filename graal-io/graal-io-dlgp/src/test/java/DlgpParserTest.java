
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

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Constant;
import fr.lirmm.graphik.graal.api.core.Literal;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.DefaultNegativeConstraint;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.io.dlp.Directive;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.CloseableIteratorWithoutException;
import fr.lirmm.graphik.util.stream.IteratorException;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DlgpParserTest {

	private static final Constant A = DefaultTermFactory.instance().createConstant("a");
	private static final Constant B = DefaultTermFactory.instance().createConstant("b");
	private static final Variable X = DefaultTermFactory.instance().createVariable("X");
	// private static final Variable Y =
	// DefaultTermFactory.instance().createVariable("Y");
	private static final Literal L1 = DefaultTermFactory.instance()
			.createLiteral(new DefaultURI("http://www.w3.org/2001/XMLSchema#integer"), "1");
	private static final Literal LSTRING = DefaultTermFactory.instance()
			.createLiteral(new DefaultURI("http://www.w3.org/2001/XMLSchema#string"), "string");
	private static final Literal LTRUE = DefaultTermFactory.instance()
			.createLiteral(new DefaultURI("http://www.w3.org/2001/XMLSchema#boolean"), "true");

	// /////////////////////////////////////////////////////////////////////////
	// ATOM
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void parseAtom() throws ParseException {
		Atom a = DlgpParser.parseAtom("p(a, X).");
		Assert.assertEquals(Term.Type.VARIABLE, a.getTerm(1).getType());
	}

	@Test(expected = ParseException.class)
	public void parseAtomWrongObject() throws ParseException {
		DlgpParser.parseQuery("p(X) :- q(X).");
	}

	@Test(expected = ParseException.class)
	public void parseAtomSyntax() throws ParseException {
		DlgpParser.parseQuery("p(a)");
	}

	@Test(expected = ParseException.class)
	public void parseAtomTwoObject() throws ParseException {
		DlgpParser.parseQuery("p(a). p(b).");
	}

	@Test(expected = ParseException.class)
	public void parseAtomNoObject() throws ParseException {
		DlgpParser.parseQuery(".");
	}

	// /////////////////////////////////////////////////////////////////////////
	// ATOMSET
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void parseAtomSet() throws ParseException, IteratorException {
		CloseableIterator<Atom> it = DlgpParser.parseAtomSet("p(a). p(b), p(1).");
		int cpt = 0;
		while (it.hasNext()) {
			++cpt;
			Atom a = it.next();
			Term t = a.getTerm(0);
			Assert.assertTrue(t.equals(B) || t.equals(A) || t.equals(L1));
		}
		Assert.assertEquals(3, cpt);
	}

	// /////////////////////////////////////////////////////////////////////////
	// QUERY
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void parseQuery() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(X) :- p(a,X).");
		Assert.assertEquals(X, q.getAnswerVariables().get(0));
		Atom a = q.getAtomSet().iterator().next();
		Assert.assertEquals(A, a.getTerm(0));
		Assert.assertEquals(X, a.getTerm(1));
	}

	@Test(expected = ParseException.class)
	public void parseQueryWithNewVarInAns() throws ParseException {
		DlgpParser.parseQuery("?(X,Y) :- p(a,X).");
	}

	@Test(expected = ParseException.class)
	public void parseQueryWrongObject() throws ParseException {
		DlgpParser.parseQuery("p(a).");
	}

	@Test
	public void parseBooleanQuery() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("? :- p(a,X).");
		Assert.assertTrue(q.getAnswerVariables().isEmpty());
		Assert.assertTrue(q.isBoolean());
		Atom a = q.getAtomSet().iterator().next();
		Assert.assertEquals(A, a.getTerm(0));
		Assert.assertEquals(X, a.getTerm(1));
	}

	/*@Test
	public void parseBooleanQuery2() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?() :- .");
		Assert.assertTrue(q.getAnswerVariables().isEmpty());
		Assert.assertTrue(q.isBoolean());
		Assert.assertFalse(q.getAtomSet().iterator().hasNext());
	}*/

	@Test
	public void parseQueryWithConstantInAns() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(b,X) :- p(a,X).");
		List<Term> ans = q.getAnswerVariables();
		Assert.assertEquals(B, ans.get(0));
		Assert.assertEquals(X, ans.get(1));
		Atom a = q.getAtomSet().iterator().next();
		Assert.assertEquals(A, a.getTerm(0));
		Assert.assertEquals(X, a.getTerm(1));
	}

	@Test
	public void parseQueryWithIntegerLiteralInAns() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(1,X) :- p(a,X).");
		List<Term> ans = q.getAnswerVariables();
		Assert.assertEquals(L1, ans.get(0));
		Assert.assertEquals(X, ans.get(1));
		Atom a = q.getAtomSet().iterator().next();
		Assert.assertEquals(A, a.getTerm(0));
		Assert.assertEquals(X, a.getTerm(1));
	}

	@Test
	public void parseQueryWithStringLiteralInAns() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(\"string\",X) :- p(a,X).");
		List<Term> ans = q.getAnswerVariables();
		Assert.assertEquals(LSTRING, ans.get(0));
		Assert.assertEquals(X, ans.get(1));
		Atom a = q.getAtomSet().iterator().next();
		Assert.assertEquals(A, a.getTerm(0));
		Assert.assertEquals(X, a.getTerm(1));
	}

	@Test
	public void parseQueryWithBooleanLiteralInAns() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?(true,X) :- p(a,X).");
		List<Term> ans = q.getAnswerVariables();
		Assert.assertEquals(LTRUE, ans.get(0));
		Assert.assertEquals(X, ans.get(1));
		Atom a = q.getAtomSet().iterator().next();
		Assert.assertEquals(A, a.getTerm(0));
		Assert.assertEquals(X, a.getTerm(1));
	}

	// /////////////////////////////////////////////////////////////////////////
	// RULE
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void parseRule() throws ParseException {
		Rule r = DlgpParser.parseRule("p(X,Y) :- q(X,Z).");

		Atom body = r.getBody().iterator().next();
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(1).getType());

		Atom head = r.getHead().iterator().next();
		Assert.assertEquals(Term.Type.VARIABLE, head.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, head.getTerm(1).getType());

	}

	@Test(expected = ParseException.class)
	public void parseRuleWrongObject() throws ParseException {
		DlgpParser.parseQuery("p(a).");
	}

	// /////////////////////////////////////////////////////////////////////////
	// NEGATIVE CONSTRAINT
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void parseNegativeConstraint() throws ParseException {
		DefaultNegativeConstraint r = DlgpParser.parseNegativeConstraint("[N1]!:-p(X,Y), q(X,Y).");

		CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
		Atom body = it.next();
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(1).getType());

		body = it.next();
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(1).getType());

		Assert.assertEquals("N1", r.getLabel());

	}

	@Test(expected = ParseException.class)
	public void parseNegativeConstraintWrongObject() throws ParseException {
		DlgpParser.parseQuery("p(a).");
	}

	// /////////////////////////////////////////////////////////////////////////
	// DIRECTIVE
	// /////////////////////////////////////////////////////////////////////////
	
	@Test
	public void commentDirectiveTest() throws ParseException {
		DlgpParser parser = new DlgpParser("%% mydirectiveValue\n");
		boolean b = false;
		while (parser.hasNext()) {
			b = true;
			Object o = parser.next();
			Assert.assertTrue(o instanceof Directive);
			Directive d = (Directive) o;
			Assert.assertEquals(Directive.Type.COMMENT, d.getType());
			Assert.assertEquals("mydirectiveValue", d.getValue());
		}
		Assert.assertTrue("No element found", b);
		parser.close();
	}
	
	@Test
	public void baseDirectiveTest() throws ParseException {
		DlgpParser parser = new DlgpParser("@base <http://example.com/>\n");
		boolean b = false;
		while (parser.hasNext()) {
			b = true;
			Object o = parser.next();
			Assert.assertTrue(o instanceof Directive);
			Directive d = (Directive) o;
			Assert.assertEquals(Directive.Type.BASE, d.getType());
			Assert.assertEquals("http://example.com/", d.getValue());
		}
		Assert.assertTrue("No element found", b);
		parser.close();
	}
	
	@Test
	public void unaDirectiveTest() throws ParseException {
		DlgpParser parser = new DlgpParser("@una\n");
		boolean b = false;
		while (parser.hasNext()) {
			b = true;
			Object o = parser.next();
			Assert.assertTrue(o instanceof Directive);
			Directive d = (Directive) o;
			Assert.assertEquals(Directive.Type.UNA, d.getType());
			Assert.assertEquals(null, d.getValue());
		}
		Assert.assertTrue("No element found", b);
		parser.close();
	}
	
	@Test
	public void topDirectiveTest() throws ParseException {
		DlgpParser parser = new DlgpParser("@top <http://example.com/top>\n");
		boolean b = false;
		while (parser.hasNext()) {
			b = true;
			Object o = parser.next();
			Assert.assertTrue(o instanceof Directive);
			Directive d = (Directive) o;
			Assert.assertEquals(Directive.Type.TOP, d.getType());
			Assert.assertEquals("http://example.com/top", d.getValue());
		}
		Assert.assertTrue("No element found", b);
		parser.close();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// OTHERS
	// /////////////////////////////////////////////////////////////////////////
	
	@Test
	public void prefixTest() throws ParseException {
		DlgpParser parser = new DlgpParser("@prefix pre: <http://example.com/>\n");
		boolean b = false;
		while (parser.hasNext()) {
			b = true;
			Object o = parser.next();
			Assert.assertTrue(o instanceof Prefix);
			Prefix p = (Prefix) o;
			Assert.assertEquals("pre", p.getPrefixName());
			Assert.assertEquals("http://example.com/", p.getPrefix());
		}
		Assert.assertTrue("No element found", b);
		parser.close();
	}

	@Test
	public void parseWithChevron() throws ParseException {
		Atom a1 = DlgpParser.parseAtom("p(a).");
		Atom a2 = DlgpParser.parseAtom("<p>(<a>).");
		Assert.assertEquals(a1, a2);
	}

	@Test
	public void labelTest() throws ParseException {
		String label = "_- 0aZéàß";
		Rule r1 = DlgpParser.parseRule("[" + label + "] q(X) :- r(X).");
		Assert.assertEquals(label, r1.getLabel());
	}
	
	@Test
	public void directiveOrder() throws ParseException {
		DlgpParser parser = new DlgpParser("@base <http://example.com/>\n"
				+ "@prefix pre: <http://example.com/>\n"
				+ "@prefix pre2: <http://example2.com/>\n"
				+ "@top <http://example.com/top>\n"
				+ "@una\n");
		int cpt = 0;
		while (parser.hasNext()) {
			++cpt;
			parser.next();
		}
		Assert.assertEquals(5,cpt);
		parser.close();
	}
	
	@Test(expected = ParseException.class)
	public void directiveOrder2() throws IteratorException {
			DlgpParser parser = new DlgpParser("@prefix pre: <http://example.com/>\n"
					+ "@prefix pre2: <http://example2.com/>\n"
					+ "@top <http://example.com/top>\n"
					+ "@una\n"
					+ "@base <http://example.com/>\n");
			int cpt = 0;
			while (parser.hasNext()) {
				++cpt;
				parser.next();
			}
			Assert.assertEquals(5,cpt);
			parser.close();
		
	}
}
