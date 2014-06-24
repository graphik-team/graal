
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.ReadOnlyAtomSet;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.obda.io.basic.BasicParser;
import fr.lirmm.graphik.obda.parser.ParseException;
import fr.lirmm.graphik.obda.parser.misc.BasicStringFormat;
import fr.lirmm.graphik.obda.parser.misc.StringFormat;

/**
 * Unit test.
 */
public class TestStringRepresentationTest 
{


	@Test
    public void testStringRepresentation()
    {
		try {
        	String s = "p(a,b)";
        	Atom atom = BasicParser.parseAtom(s);
            List<Term> terms = atom.getTerms();
    
            Assert.assertTrue( new Predicate("p",2).equals(atom.getPredicate()) );
            Assert.assertTrue( terms.size() == 2 );
            Assert.assertTrue( new Term("a",Term.Type.CONSTANT).equals(terms.get(0)) );
            Assert.assertTrue( new Term("b",Term.Type.CONSTANT).equals(terms.get(1)) );
            
            
            s = "predicat(tom,alice,bob)";
            atom = BasicParser.parseAtom(s);
            terms = atom.getTerms();
            
            Assert.assertTrue( new Predicate("predicat", 3).equals(atom.getPredicate()) );
            Assert.assertTrue( terms.size() == 3 );
            Assert.assertTrue( new Term("tom",Term.Type.CONSTANT).equals(terms.get(0)) );
            Assert.assertTrue( new Term("alice",Term.Type.CONSTANT).equals(terms.get(1)) );
            Assert.assertTrue( new Term("bob",Term.Type.CONSTANT).equals(terms.get(2)) );
		
		} catch (ParseException e) {
			Assert.assertFalse(true);
		}
	}
	
	@Test
    public void parseTest()
    {
        String s = "p(a,b).q(X,Y).r(a,b,c,d,Z)";
        ReadOnlyAtomSet atomSet = BasicParser.parse(s);
        
        List<Term> termList = new LinkedList<Term>();
        termList.add(new Term("a", Term.Type.CONSTANT));
        termList.add(new Term("b", Term.Type.CONSTANT));
        Atom atom = new DefaultAtom(new Predicate("p", 2), termList);       
        try {
            Assert.assertTrue( atomSet.contains(atom) );
        } catch (AtomSetException e) {
            Assert.assertTrue(e.getMessage(), false);
        }
        
        termList.clear();
        termList.add(new Term("X", Term.Type.VARIABLE));
        termList.add(new Term("Y", Term.Type.VARIABLE));
        atom = new DefaultAtom(new Predicate("q", 2), termList);
        try {
            Assert.assertTrue( atomSet.contains(atom) );
        } catch (AtomSetException e) {
            Assert.assertTrue(e.getMessage(), false);
        }
        
        termList.clear();
        termList.add(new Term("a", Term.Type.CONSTANT));
        termList.add(new Term("b", Term.Type.CONSTANT));
        termList.add(new Term("c", Term.Type.CONSTANT));
        termList.add(new Term("d", Term.Type.CONSTANT));
        termList.add(new Term("Z", Term.Type.VARIABLE));
        atom = new DefaultAtom(new Predicate("r", 5), termList);
        try {
            Assert.assertTrue( atomSet.contains(atom) );
        } catch (AtomSetException e) {
            Assert.assertTrue(e.getMessage(), false);
        }
        
        termList.clear();
        termList.add(new Term("a", Term.Type.CONSTANT));
        termList.add(new Term("c", Term.Type.CONSTANT));
        atom = new DefaultAtom(new Predicate("p", 2), termList);
        try {
            Assert.assertFalse( atomSet.contains(atom) );
        } catch (AtomSetException e) {
            Assert.assertTrue(e.getMessage(), false);
        }
        
        termList.clear();
        termList.add(new Term("a", Term.Type.CONSTANT));
        termList.add(new Term("b", Term.Type.CONSTANT));
        atom = new DefaultAtom(new Predicate("r", 2), termList);
        try {
            Assert.assertFalse( atomSet.contains(atom) );
        } catch (AtomSetException e) {
            Assert.assertTrue(e.getMessage(), false);
        }
        
    }
    
	
}
