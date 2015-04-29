/**
 * 
 */
package fr.lirmm.graphik.graal.backward_chaining.test;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.FreeVarSubstitution;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class FreeVarSubstitutionTest {
	
	@Test
	public void test() {
		final Term X = DefaultTermFactory.instance().createVariable("X");
		final Term Y = DefaultTermFactory.instance().createVariable("Y");
		final Term Z = DefaultTermFactory.instance().createVariable("Z");
		
		Rule rule = DlgpParser.parseRule("p(X,Y,Z) :- q(X,Y), q(Y,Z).");
		
		FreeVarSubstitution subtitution = new FreeVarSubstitution();
		
		Rule substitut = subtitution.getSubstitut(rule);
		for(Term t : substitut.getTerms(Term.Type.VARIABLE)) {
			if(t.equals(X) || t.equals(Y) || t.equals(Z)) {
				Assert.assertFalse(true);
			}
		}
	}

}
