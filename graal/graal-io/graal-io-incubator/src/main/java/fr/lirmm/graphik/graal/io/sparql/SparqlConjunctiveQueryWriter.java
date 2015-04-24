/**
 * 
 */
package fr.lirmm.graphik.graal.io.sparql;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.io.AbstractWriter;
import fr.lirmm.graphik.graal.io.ConjunctiveQueryWriter;
import fr.lirmm.graphik.graal.io.WriterException;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class SparqlConjunctiveQueryWriter extends AbstractWriter implements
		ConjunctiveQueryWriter {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public SparqlConjunctiveQueryWriter(Writer out) {
		super(out);
	}

	public SparqlConjunctiveQueryWriter() {
		this(new OutputStreamWriter(System.out));
	}

	public SparqlConjunctiveQueryWriter(OutputStream out) {
		this(new OutputStreamWriter(out));
	}

	public SparqlConjunctiveQueryWriter(File file) throws IOException {
		this(new FileWriter(file));
	}

	public SparqlConjunctiveQueryWriter(String path) throws IOException {
		this(new FileWriter(path));
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////
	@Override
	public void write(Prefix prefix) throws IOException {
		this.write("PREFIX ");
		this.write(prefix.getPrefixName());
		this.write(": <");
		this.write(prefix.getPrefix());
		this.writeln('>');
	}
	
	@Override
	public void write(ConjunctiveQuery query)
			throws IOException {

		this.write("SELECT DISTINCT ");
		for(Term t : query.getAnswerVariables())
			this.write(t);

		this.write("\nWHERE\n{\n");
		boolean isFirst = true;
		for(Atom a : query.getAtomSet()) {
			if(!isFirst) {
				this.write(" .\n");
			} else {
				isFirst = false;
			}
			this.writeAtom(a);
		}
		
		this.write("\n}\n");
	}

	/**
	 * @param a
	 * @throws IOException 
	 */
	private void writeAtom(Atom a) throws IOException {
		this.write("\t");
		this.write(a.getTerm(0));
		this.write(' ');
		
		if(a.getPredicate().getArity() == 1) {
			this.write("rdf:type ");
			this.write(a.getPredicate());
		} else if (a.getPredicate().getArity() == 2) {
			this.write(a.getPredicate());
			this.write(' ');
			this.write(a.getTerm(1));
		} else {
			throw new WriterException("Unsupported predicate arity");
		}
	}

	/**
	 * @param predicate
	 * @throws IOException 
	 */
	private void write(Predicate predicate) throws IOException {
		this.write(predicate.getIdentifier());
	}

	/**
	 * @param t
	 * @throws IOException 
	 */
	private void write(Term t) throws IOException {
		if (Term.Type.VARIABLE.equals(t.getType())) {
			this.write('?');
		}
		
		this.write(t.getIdentifier());
		this.write(' ');
	}

}
