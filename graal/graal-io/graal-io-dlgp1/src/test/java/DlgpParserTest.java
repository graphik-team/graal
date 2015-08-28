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
 
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.impl.DefaultNegativeConstraint;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Parser;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DlgpParserTest {

	// /////////////////////////////////////////////////////////////////////////
	// CHECK VARIABLE TYPE
	// /////////////////////////////////////////////////////////////////////////

	@Test
	public void parseAtom() {
		Atom a = Dlgp1Parser.parseAtom("p(a,X).");
		Assert.assertEquals(Term.Type.VARIABLE, a.getTerm(1).getType());
	}
	
	@Test
	public void parseQuery() {
		ConjunctiveQuery q = Dlgp1Parser.parseQuery("?(X) :- p(a,X).");
		Assert.assertEquals(Term.Type.VARIABLE, q.getAnswerVariables().iterator().next().getType());
		Assert.assertEquals(Term.Type.VARIABLE, q.getAtomSet().iterator().next().getTerm(1).getType());
	}
	
	@Test
	public void parseRule() {
		Rule r = Dlgp1Parser.parseRule("p(X,Y) :- q(X,Z).");
		
		Atom body = r.getBody().iterator().next();
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(1).getType());

		Atom head = r.getHead().iterator().next();
		Assert.assertEquals(Term.Type.VARIABLE, head.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, head.getTerm(1).getType());

	}
	
	@Test
	public void parseNegativeConstraint() {
		DefaultNegativeConstraint r = Dlgp1Parser.parseNegativeConstraint("[N1]!:-p(X,Y), q(X,Y).");
		
		Iterator<Atom> it = r.getBody().iterator();
		Atom body = it.next();
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(1).getType());

		body = it.next();
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(1).getType());
		
		Assert.assertEquals("N1", r.getLabel());

	}
	
	@Test
	public void parseWithQuotes() {
		Atom a1 = Dlgp1Parser.parseAtom("p(a).");
		Atom a2 = Dlgp1Parser.parseAtom("\"p\"(a).");
		Assert.assertEquals(a1, a2);
	}

	@Test
	public void parseFile() throws FileNotFoundException {
		Dlgp1Parser parser = new Dlgp1Parser(new File(
				"./src/test/resources/test.dlgp"));
		boolean found = false;
		for (Object o : parser) {
			if (o instanceof Rule) {
				found = true;
			}
		}
		parser.close();
		Assert.assertTrue(found);
	}

	@Test
	public void parseRule1() {
		Rule r = Dlgp1Parser
				.parseRule("[R0_p0]\"Employee\"(X1_0) :- \"AdministrativeStaff\"(X1_0).");
		Assert.assertNotNull(r);
	}
}
