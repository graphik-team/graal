package fr.lirmm.graphik.obda.parser.misc;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import fr.lirmm.graphik.kb.core.Atom;
import fr.lirmm.graphik.util.stream.ObjectWriter;

/**
 * @deprecated use {@link fr.lirmm.graphik.obda.io.basic.BasicWriter} instead
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class StringAtomWriter implements ObjectWriter<Atom> {

	private Writer writer;
	private StringFormat representation;
	
	public StringAtomWriter(Writer writer, StringFormat representation) {
		this.writer = writer;
		this.representation = representation;
	}
	
	public void write(Iterable<Atom> atoms) throws IOException {
		Iterator<Atom> it = atoms.iterator();
		if(it.hasNext()) {
			this.writer.write(representation.toString(it.next()));
			while(it.hasNext()) {
				this.writer.write(representation.getAtomSeparator());
				this.writer.write(representation.toString(it.next()));
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
        this.writer.write(representation.toString(atom));
    }
	

}
