/**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.util.Iterator;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.repository.RepositoryException;

import parser.TERM_TYPE;
import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.io.dlp.DlpParser;
import fr.lirmm.graphik.graal.store.Store;
import fr.lirmm.graphik.graal.store.triplestore.SailStore;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class SailStoreTest {
	
	public static Term createTerm(TERM_TYPE termType, Object term) {
		Term.Type type = null;
		switch(termType) {
		case ANSWER_VARIABLE:
		case VARIABLE:
			type = Term.Type.VARIABLE;
			break;
		case CONSTANT: 
			type = Term.Type.CONSTANT;
			break;
		case FLOAT:
		case INTEGER:
		case STRING:
			type = Term.Type.LITERAL;
			break;
		}
		return new Term(term, type);
	}
	
	public static void main(String args[]) throws RepositoryException, MalformedQueryException, AtomSetException {
				
		Store store = new SailStore();
		
		Term t1 = new Term("http://to.to/b", Term.Type.CONSTANT);
		Term t2 = new Term("http://to.to/a", Term.Type.CONSTANT);
		Predicate p = new Predicate("http://to.to/p", 2);
		Atom atom1 = new DefaultAtom(p, t1, t2);
		
		 t1 = new Term("http://to.to/a", Term.Type.CONSTANT);
		 t2 = new Term("http://to.to/a", Term.Type.CONSTANT);
		Atom atom2 = new DefaultAtom(p, t1, t2);
		
		 t1 = new Term("http://to.to/c", Term.Type.CONSTANT);
		 t2 = new Term("http://to.to/a", Term.Type.CONSTANT);
		Atom atom3 = new DefaultAtom(p, t1, t2);
		System.out.println("atom3: " + atom3);
		
		store.add(atom1);
		store.add(atom2);
		store.add(atom3);
		
		Atom atom4 = DlpParser.parseAtom("p(a,b).");
		System.out.println("atom4: " + atom4);
		System.out.println("==================");
		store.add(atom4);
		
		System.out.println("////////");
		for(Atom a : store) {
			System.out.println(a);
		}
		System.out.println("»»»»»»»»");
		

		
		/*if(store.remove(atom2)) {
			System.out.println("remove :/ ?");
		} else {
			System.out.println("return of remove is ok");
		}*/
				
		
		System.out.println(store.contains(atom1) + "/ true");
		System.out.println(store.contains(atom2) + "/ false");

		{
			System.out.println("*******************"); 
			Iterator<Predicate> it = store.predicatesIterator();
			while(it.hasNext()) {
				System.out.println(it.next());
			}
		}
		
		{
			System.out.println("*******************"); 
			Iterator<Term> it = store.termsIterator();
			while(it.hasNext()) {
				System.out.println(it.next());
			}
		}
			
		

	}
}
