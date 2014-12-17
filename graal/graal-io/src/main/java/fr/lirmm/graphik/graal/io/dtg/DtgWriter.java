/**
 * 
 */
package fr.lirmm.graphik.graal.io.dtg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.NegativeConstraint;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.Term.Type;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class DtgWriter extends DlgpWriter {

	// /////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////

	public DtgWriter() {
		super();
	}
	
	public DtgWriter(OutputStream out) {
		super(out);
	}
	
	public DtgWriter(Writer out) {
		super(out);
	}
	
	public DtgWriter(File file) throws IOException {
		super(file);
	}
	
	public DtgWriter(String path) throws IOException {
		 super(path);
	}
	
	// /////////////////////////////////////////////////////////////////////////
	// PROTECTED METHODS
	// /////////////////////////////////////////////////////////////////////////
	
	protected void writeTerm(Term t) throws IOException {
		//String term = t.toString();
//		if(Type.VARIABLE.equals(t.getType())) {
//			this.writer.write("?");
//			this.writer.write(term);
//		} else if(Type.CONSTANT.equals(t.getType())) {
//			this.writer.write(term);
//		} else {
//			this.writer.write('"');
//			this.writer.write(t.toString());
//			this.writer.write('"');
//		}
	}
	
	
	
};
