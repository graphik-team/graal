
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.kb.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.kb.core.NegativeConstraint;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.kb.core.Term;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;

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
		Atom a = DlgpParser.parseAtom("p(a,X).");
		Assert.assertEquals(Term.Type.VARIABLE, a.getTerm(1).getType());
	}
	
	@Test
	public void parseQuery() {
		DefaultConjunctiveQuery q = DlgpParser.parseQuery("?(X) :- p(a,X).");
		Assert.assertEquals(Term.Type.VARIABLE, q.getResponseVariables().iterator().next().getType());
		Assert.assertEquals(Term.Type.VARIABLE, q.getAtomSet().iterator().next().getTerm(1).getType());
	}
	
	@Test
	public void parseRule() {
		Rule r = DlgpParser.parseRule("p(X,Y) :- q(X,Z).");
		
		Atom body = r.getBody().iterator().next();
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(1).getType());

		Atom head = r.getHead().iterator().next();
		Assert.assertEquals(Term.Type.VARIABLE, head.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, head.getTerm(1).getType());

	}
	
	@Test
	public void parseNegativeConstraint() {
		NegativeConstraint r = DlgpParser.parseNegativeConstraint("[N1]!:-p(X,Y), q(X,Y).");
		
		Iterator<Atom> it = r.getBody().iterator();
		Atom body = it.next();
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(1).getType());

		body = it.next();
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(0).getType());
		Assert.assertEquals(Term.Type.VARIABLE, body.getTerm(1).getType());
		
		Assert.assertEquals("N1", r.getLabel());

	}
}
