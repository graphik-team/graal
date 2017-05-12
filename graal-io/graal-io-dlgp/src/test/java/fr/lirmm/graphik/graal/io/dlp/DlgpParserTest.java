package fr.lirmm.graphik.graal.io.dlp;

import java.io.File;
import java.io.FileNotFoundException;

/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2017)
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
import fr.lirmm.graphik.graal.api.core.NegativeConstraint;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.Term;
import fr.lirmm.graphik.graal.api.core.Variable;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.util.DefaultURI;
import fr.lirmm.graphik.util.Prefix;
import fr.lirmm.graphik.util.URI;
import fr.lirmm.graphik.util.URIUtils;
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
		Assert.assertTrue(a.getTerm(1).isVariable());
	}

	@Test
	public void parseAtomWithPrefix() throws ParseException {
		Atom a = DlgpParser.parseAtom("@prefix ex: <http://example.com/> ex:p(a, X).");
		Assert.assertTrue(a.getTerm(1).isVariable());
	}

	@Test(expected = ParseException.class)
	public void parseAtomWrongObject() throws ParseException {
		DlgpParser.parseAtom("p(X) :- q(X).");
	}

	@Test(expected = ParseException.class)
	public void parseAtomSyntax() throws ParseException {
		DlgpParser.parseAtom("p(a)");
	}

	@Test(expected = ParseException.class)
	public void parseAtomTwoObject() throws ParseException {
		DlgpParser.parseAtom("p(a). p(b).");
	}

	@Test(expected = ParseException.class)
	public void parseAtomNoObject() throws ParseException {
		DlgpParser.parseAtom("");
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

	@Test
	public void parseAtomSetWithPrefix() throws ParseException, IteratorException {
		CloseableIterator<Atom> it = DlgpParser
				.parseAtomSet("@prefix ex: <http://example.com/> ex:p(a). ex:p(b), ex:p(1).");
		int cpt = 0;
		while (it.hasNext()) {
			++cpt;
			Atom a = it.next();
			Term t = a.getTerm(0);
			Assert.assertTrue(t.equals(B) || t.equals(A) || t.equals(L1));
		}
		Assert.assertEquals(3, cpt);
	}

	@Test(expected = DlgpParseException.class)
	public void parseAtomSetException() throws ParseException, IteratorException {
		CloseableIterator<Atom> it = DlgpParser.parseAtomSet("p(a). p(b) p(1).");
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

	@Test
	public void parseQueryWithPrefix() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("@prefix ex: <http://example.com/> ?(X) :- ex:p(a,X).");
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

	@Test
	public void parseBooleanQuery2() throws ParseException {
		ConjunctiveQuery q = DlgpParser.parseQuery("?() :- .");
		Assert.assertTrue(q.getAnswerVariables().isEmpty());
		Assert.assertTrue(q.isBoolean());
		Assert.assertFalse(q.getAtomSet().iterator().hasNext());
	}

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
		Assert.assertTrue(body.getTerm(0).isVariable());
		Assert.assertTrue(body.getTerm(1).isVariable());

		Atom head = r.getHead().iterator().next();
		Assert.assertTrue(head.getTerm(0).isVariable());
		Assert.assertTrue(head.getTerm(1).isVariable());

	}

	@Test
	public void parseRuleWithPrefix() throws ParseException {
		Rule r = DlgpParser.parseRule("@prefix ex: <http://example.com/> ex:p(X,Y) :- ex:q(X,Z).");

		Atom body = r.getBody().iterator().next();
		Assert.assertTrue(body.getTerm(0).isVariable());
		Assert.assertTrue(body.getTerm(1).isVariable());

		Atom head = r.getHead().iterator().next();
		Assert.assertTrue(head.getTerm(0).isVariable());
		Assert.assertTrue(head.getTerm(1).isVariable());

	}

	@Test(expected = ParseException.class)
	public void parseRuleWrongObject() throws ParseException {
		DlgpParser.parseRule("p(a).");
	}

	// /////////////////////////////////////////////////////////////////////////
	// NEGATIVE CONSTRAINT
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void parseNegativeConstraint() throws ParseException {
		NegativeConstraint r = DlgpParser.parseNegativeConstraint("[N1]!:-p(X,Y), q(X,Y).");

		CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
		Atom body = it.next();
		Assert.assertTrue(body.getTerm(0).isVariable());
		Assert.assertTrue(body.getTerm(1).isVariable());

		body = it.next();
		Assert.assertTrue(body.getTerm(0).isVariable());
		Assert.assertTrue(body.getTerm(1).isVariable());

		Assert.assertEquals("N1", r.getLabel());

	}

	@Test
	public void parseNegativeConstraintWithPrefix() throws ParseException {
		NegativeConstraint r = DlgpParser
				.parseNegativeConstraint("@prefix ex: <http://example.com/> [N1]!:-ex:p(X,Y), ex:q(X,Y).");

		CloseableIteratorWithoutException<Atom> it = r.getBody().iterator();
		Atom body = it.next();
		Assert.assertTrue(body.getTerm(0).isVariable());
		Assert.assertTrue(body.getTerm(1).isVariable());

		body = it.next();
		Assert.assertTrue(body.getTerm(0).isVariable());
		Assert.assertTrue(body.getTerm(1).isVariable());

		Assert.assertEquals("N1", r.getLabel());

	}

	@Test(expected = ParseException.class)
	public void parseNegativeConstraintWrongObject() throws ParseException {
		DlgpParser.parseNegativeConstraint("p(a).");
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
	// Exception
	// /////////////////////////////////////////////////////////////////////////

	@Test(expected = DlgpParseException.class)
	public void exception1() throws IteratorException {
		DlgpParser parser = new DlgpParser("?(X) :- p(a).");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	@Test(expected = DlgpParseException.class)
	public void exception2() throws IteratorException {
		DlgpParser parser = new DlgpParser("p(a,b.\n");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	@Test(expected = DlgpParseException.class)
	public void exception3() throws IteratorException {
		DlgpParser parser = new DlgpParser("P(a,b.\n");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	@Test(expected = DlgpParseException.class)
	public void exception4() throws IteratorException {
		DlgpParser parser = new DlgpParser(".");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	@Test(expected = DlgpParseException.class)
	public void exception5() throws IteratorException {
		DlgpParser parser = new DlgpParser(" :- p(a).");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	@Test(expected = DlgpParseException.class)
	public void exception6() throws IteratorException {
		DlgpParser parser = new DlgpParser("! :-.");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	@Test(expected = DlgpParseException.class)
	public void exception7() throws IteratorException {
		DlgpParser parser = new DlgpParser("[label ? :- p(a).");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	@Test(expected = DlgpParseException.class)
	public void exception8() throws IteratorException {
		DlgpParser parser = new DlgpParser("p(a)");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	@Test(expected = DlgpParseException.class)
	public void exception9() throws IteratorException {
		DlgpParser parser = new DlgpParser("p(a) p(b).");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	// /////////////////////////////////////////////////////////////////////////
	// MISCS
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void backslashedDoubleQuoteTest() throws ParseException {
		Atom a = DlgpParser.parseAtom("p(\"test\\\"test\").");
		Term identifier = a.getTerm(0);
		Assert.assertTrue(identifier instanceof Literal);
		URI datatype = ((Literal) identifier).getDatatype();
		Assert.assertEquals(URIUtils.XSD_STRING, datatype);
		Object value = ((Literal) identifier).getValue();
		Assert.assertEquals("test\"test", value);
	}
	
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
		DlgpParser parser = new DlgpParser("@base <http://example.com/>\n" + "@prefix pre: <http://example.com/>\n"
				+ "@prefix pre2: <http://example2.com/>\n" + "@top <http://example.com/top>\n" + "@una\n");
		int cpt = 0;
		while (parser.hasNext()) {
			++cpt;
			parser.next();
		}
		Assert.assertEquals(5, cpt);
		parser.close();
	}

	@Test
	public void directiveOrder2() throws IteratorException {
		DlgpParser parser = new DlgpParser(
				"@prefix pre: <http://example.com/>\n" + "@prefix pre2: <http://example2.com/>\n"
						+ "@top <http://example.com/top>\n" + "@una\n" + "@base <http://example.com/>\n");
		int cpt = 0;
		while (parser.hasNext()) {
			++cpt;
			parser.next();
		}
		Assert.assertEquals(5, cpt);
		parser.close();
	}

	@Test
	public void directiveOrder3() throws IteratorException {
		DlgpParser parser = new DlgpParser(
				"@una\n" + "@prefix pre: <http://example.com/>\n" + "@base <http://example.com/>\n"
						+ "@prefix pre2: <http://example2.com/>\n" + "@top <http://example.com/top>\n");
		int cpt = 0;
		while (parser.hasNext()) {
			++cpt;
			parser.next();
		}
		Assert.assertEquals(5, cpt);
		parser.close();
	}

	@Test(expected = DlgpParseException.class)
	public void directiveOrderWithDoublon() throws IteratorException {
		DlgpParser parser = new DlgpParser("@una\n" + "@top <http://example.com/top>\n"
				+ "@prefix pre: <http://example.com/>\n" + "@base <http://example.com/>\n"
				+ "@prefix pre2: <http://example2.com/>\n" + "@top <http://example.com/top2>\n" + "@una");
		while (parser.hasNext()) {
			parser.next();
		}
		parser.close();
	}

	// /////////////////////////////////////////////////////////////////////////
	// FILES
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void testSimpleFile() throws IteratorException, FileNotFoundException {
		DlgpParser parser = new DlgpParser(new File("./src/test/resources/simple.dlp"));
		int i = 0;
		while (parser.hasNext()) {
			++i;
			parser.next();
		}
		parser.close();
		Assert.assertEquals(21, i);
	}

	@Test
	public void testIrisAndLiteralsFile() throws IteratorException, FileNotFoundException {
		DlgpParser parser = new DlgpParser(new File("./src/test/resources/irisAndLiterals.dlp"));
		int i = 0;
		while (parser.hasNext()) {
			++i;
			parser.next();
		}
		parser.close();
		Assert.assertEquals(8, i);
	}

	@Test
	public void testCorrectFile() throws IteratorException, FileNotFoundException {
		DlgpParser parser = new DlgpParser(new File("./src/test/resources/correct.dlp"));
		int i = 0;
		while (parser.hasNext()) {
			++i;
			parser.next();
		}
		Assert.assertEquals(19, i);
		parser.close();
	}

	// /////////////////////////////////////////////////////////////////////////
	// URI FORMAT
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void uriWithUnicode() throws ParseException {
		String uri = "\\u007C\\u003e\\u0020\\u262D";
		Atom a = DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">).");
		Assert.assertEquals("|> \u262D", a.getPredicate().getIdentifier().toString());
	}
	
	@Test
	public void uriWithUnicode2() throws ParseException {
		String uri = "\\U0000007C\\U0000003e\\U00000020\\U0000262D";
		Atom a = DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">).");
		Assert.assertEquals("|> \u262D", a.getPredicate().getIdentifier().toString());
	}

	@Test
	public void uriWithUnicode4Bytes() throws ParseException {
		String uri = "\\U00010000\\u003e\\U00000020";
		Atom a = DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">).");
		String s = new String(Character.toChars(0x10000));
		Assert.assertEquals( s + "> ",a.getPredicate().getIdentifier().toString());
	}

	@Test
	public void uriValide() throws ParseException {
		String uri = "/a//a///a////a/////a//////" + "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "´áéíóúýàèìòùỳ¨äëïöüÿâêîôûŷ" + "?¿.:,;!¡~'" + "0123456789₀₁₂₃₄₅₆₇₈₉₍₎₊₋₌ₐₑₒₓₔ⁰¹⁴⁵⁶⁷⁸⁹⁺⁽⁾⁼⁻"
				+ "+-*%=()[]«»_—–#" + "ßæœçåÅøØĐħĦ" + "€$¤£" + "¶¦¬©®™ªº♯♮♭"
				+ "ΑΒΔΕΦΓΗΙΘΚΛΜΝΟΠΧΡΣΤΥΩΞΨΖαβδεφγηιθκλμνοπχρστυωξψζ"
				+ "‰≃≠≮≯≤≥≰≱≲≳Ω¼½¾ƒℓ⅓⅔⅛⅜⅝⅞⩽⩾←↑→↓↔↦⇒⇔∂∙∏∑∆∇√∞∫≈≡∀∃∈∉∪∩⊂⊃♀♂ℝℂℚℕℤℍ⊥‖∧∨⟦⟧⟨⟩∘" + "  " + // unbreakable
																									// spaces
				"////";
		Atom a = DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">).");

		Assert.assertEquals(uri, a.getPredicate().getIdentifier().toString());
		Assert.assertEquals(uri, a.getTerm(0).getIdentifier().toString());
	}
	
	@Test
	public void uriValidePercent() throws ParseException {
		String uri = "%";
		Atom a = DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">).");

		Assert.assertEquals(uri, a.getPredicate().getIdentifier().toString());
		Assert.assertEquals(uri, a.getTerm(0).getIdentifier().toString());
	}

	@Test(expected = DlgpParseException.class)
	public void uriNotValideSpace() throws ParseException {
		String uri = "/ /";
		DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">).");
	}

	/*
	 * @Test(expected = DlgpParseException.class) public void
	 * uriNotValideLesserThan() throws ParseException { String uri = "/</";
	 * DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">)."); }
	 */

	@Test(expected = DlgpParseException.class)
	public void uriNotValideGreaterThan() throws ParseException {
		String uri = "/>/";
		System.out.println(DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">)."));
		;
	}

	/*
	 * @Test(expected = DlgpParseException.class) public void
	 * uriNotValideDoubleQuotes() throws ParseException { String uri = "/\"/";
	 * DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">)."); }
	 */

	/*
	 * @Test(expected = DlgpParseException.class) public void
	 * uriNotValideLeftCurlyBracket() throws ParseException { String uri =
	 * "/{/"; DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">)."); }
	 * 
	 * @Test(expected = DlgpParseException.class) public void
	 * uriNotValideRightCurlyBracket() throws ParseException { String uri =
	 * "/}/"; DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">)."); }
	 */

	/*
	 * @Test(expected = DlgpParseException.class) public void uriNotValidePipe()
	 * throws ParseException { String uri = "/|/"; DlgpParser.parseAtom("<" +
	 * uri + ">(<" + uri + ">)."); }
	 */

	/*
	 * @Test(expected = DlgpParseException.class) public void
	 * uriNotValideCircumflexAccent() throws ParseException { String uri =
	 * "/^/"; DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">)."); }
	 */

	/*
	 * @Test(expected = DlgpParseException.class) public void
	 * uriNotValideBackQuote() throws ParseException { String uri = "/`/";
	 * DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">)."); }
	 */

	/*
	 * @Test(expected = DlgpParseException.class) public void
	 * uriNotValideBackSlash() throws ParseException { String uri = "/\\/";
	 * DlgpParser.parseAtom("<" + uri + ">(<" + uri + ">)."); }
	 */
	
	
	// /////////////////////////////////////////////////////////////////////////
	// BUGS
	// /////////////////////////////////////////////////////////////////////////
	
	/*@Test FIXME
	public void bug9() throws ParseException {
		try {
			DlgpParser.parseAtom("@prefix o: <http://o/>\n"
					+ "o:p(o:Jo%C5%BEe-Topori%C5%A1i%3F).");
		} catch (Exception e) {
			Assert.fail();
		}
	}*/
	

}
