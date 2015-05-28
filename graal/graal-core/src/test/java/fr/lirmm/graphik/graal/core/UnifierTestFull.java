/**
 * 
 */
package fr.lirmm.graphik.graal.core;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.atomset.InMemoryAtomSet;
import fr.lirmm.graphik.graal.core.factory.AtomSetFactory;
import fr.lirmm.graphik.graal.core.factory.RuleFactory;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.grd.AtomErasing;
import fr.lirmm.graphik.graal.io.dlp.Dlgp1Parser;


public class UnifierTestFull {
	
	private static Rule r1;
	private static Rule r2;
	private static Rule r3;
	private static AtomErasing filter;

	@BeforeClass
	public static void beforeClass() {
		filter = new AtomErasing();
		r1 = Dlgp1.parseRule("p(X,Y),p(Y,Z) :- p(X,Z).");
		r2 = Dlgp1.parseRule("a(X) :- b(X).");
		r3 = Dlgp1.parseRule("b(Z),a(Z),p(X,Z) :- c(X).");
	}
	
	@Test
	public void atomErasingFilterTest() {
		filter.setRule1(r1); filter.setRule2(r1);
		Assert.assertTrue(Unifier.getInstance().existsPieceUnifier(r1,r1.getBody(),filter));

		filter.setRule1(r3); filter.setRule2(r2);
		Assert.assertFalse(Unifier.getInstance().existsPieceUnifier(r3,r2.getBody(),filter));
	}

};

