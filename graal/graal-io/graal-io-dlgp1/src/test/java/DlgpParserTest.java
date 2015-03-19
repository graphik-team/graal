
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
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
		DefaultConjunctiveQuery q = Dlgp1Parser.parseQuery("?(X) :- p(a,X).");
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
		NegativeConstraint r = Dlgp1Parser.parseNegativeConstraint("[N1]!:-p(X,Y), q(X,Y).");
		
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
}
