/**
 * 
 */
package fr.lirmm.graphik.obda.writer;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.util.ForEachSeparator;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SparqlConjunctiveQueryWriter implements ConjunctiveQueryWriter {

	public void write(DefaultConjunctiveQuery query, String rdfPrefix) throws WriterException {
		System.out.println("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
		System.out.println("PREFIX : <" + rdfPrefix + '>');

		System.out.print("SELECT DISTINCT ");
		for(Term t : query.getResponseVariables())
			this.write(t);

		System.out.print("\nWHERE\n{\n");
		ForEachSeparator separator = new ForEachSeparator(" .\n");
		for(Atom a : query.getAtomSet()) {
			System.out.print(separator.get());
			this.write(a, rdfPrefix);
		}
		
		System.out.print("\n}\n");
	}
	
	/* (non-Javadoc)
	 * @see fr.lirmm.graphik.obda.writer.ConjunctiveQueryWriter#write(fr.lirmm.graphik.kb.core.impl.ConjunctiveQuery)
	 */
	@Override
	public void write(DefaultConjunctiveQuery query) throws WriterException {
		this.write(query, "");
	}

	/**
	 * @param a
	 * @throws WriterException 
	 */
	private void write(Atom a, String rdfPrefix) throws WriterException {
		System.out.print("\t");
		this.write(a.getTerm(0));
		System.out.print(' ');
		
		if(a.getPredicate().getArity() == 1) {
			System.out.print("rdf:type ");
			this.write(a.getPredicate());
		} else if (a.getPredicate().getArity() == 2) {
			this.write(a.getPredicate());
			System.out.print(' ');
			this.write(a.getTerm(1));
		} else {
			throw new WriterException("Unsupported predicate arity");
		}
	}

	/**
	 * @param predicate
	 */
	private void write(Predicate predicate) {
		System.out.print(':');
		System.out.print(predicate.getLabel());
	}

	/**
	 * @param t
	 */
	private void write(Term t) {
		if(Term.Type.VARIABLE.equals(t.getType()))
			System.out.print('?');
		else
			System.out.print(':');
		
		System.out.print(t);
		System.out.print(' ');
	}
	

}
