package fr.lirmm.graphik.graal.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.Term;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.store.triplestore.JenaStore;

public class JenaTest {
	static DlgpWriter writer = new DlgpWriter();
	
	public static void main(String[] args) throws AtomSetException, IOException {
		File f = new File("/tmp/jena");
		AtomSet atomset = new JenaStore(f.getAbsolutePath());
		
		Term t1 = new Term("http://to.to/b", Term.Type.CONSTANT);
		Term t2 = new Term("http://to.to/a", Term.Type.CONSTANT);
		Predicate p = new Predicate("http://to.to/p", 2);
		Atom atom1 = new DefaultAtom(p, t1, t2);
		
		atomset.add(atom1);
		
		writer.write(atomset);
		
		FileUtils.deleteDirectory(f);
	}

}
