package fr.lirmm.graphik.graal.io.basic;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.util.stream.ObjectWriter;

public class BasicWriter implements ObjectWriter<Atom> {

	private Writer writer;
	
	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public BasicWriter(Writer writer) {
		this.writer = writer;
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PUBLIC METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	public void write(Iterable<Atom> atoms) throws IOException {
		Iterator<Atom> it = atoms.iterator();
		if(it.hasNext()) {
			this.writer.write(this.toString(it.next()));
			while(it.hasNext()) {
				this.writer.write(BasicFormat.ATOM_SEPARATOR);
				this.writer.write(this.toString(it.next()));
			}
		}
		this.writer.flush();
	}
	
	public String toString() {
		return this.writer.toString();
	}

    /* (non-Javadoc)
     * @see fr.lirmm.graphik.kb.stream.AtomWriter#write(fr.lirmm.graphik.kb.core.Atom)
     */
    @Override
    public void write(Atom atom) throws IOException {
        this.writer.write(this.toString(atom));
    }
    
	public String toString(Atom atom) {
		StringBuilder string = new StringBuilder(atom.getPredicate().getLabel());
		string.append('(');
		append(string, atom.getTerms(), BasicFormat.TERM_SEPARATOR);
		string.append(')');
					
		return string.toString();
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PRIVATE METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	private static void append(StringBuilder stringBuilder, Iterable<Term> objects, String separator) {
		Iterator<Term> it = objects.iterator();
		if (it.hasNext()) {
			stringBuilder.append(it.next());
			while (it.hasNext()) {
				stringBuilder.append(BasicFormat.TERM_SEPARATOR);
				stringBuilder.append(it.next());
			}
		}
	}

}
