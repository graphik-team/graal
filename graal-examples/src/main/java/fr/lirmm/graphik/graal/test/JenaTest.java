/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 package fr.lirmm.graphik.graal.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.DefaultAtom;
import fr.lirmm.graphik.graal.core.Predicate;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.term.DefaultTermFactory;
import fr.lirmm.graphik.graal.core.term.Term;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.store.triplestore.JenaStore;

public class JenaTest {
	static DlgpWriter writer = new DlgpWriter();
	
	public static void main(String[] args) throws AtomSetException, IOException {
		File f = new File("/tmp/jena");
		AtomSet atomset = new JenaStore(f.getAbsolutePath());
		
		Term t1 = DefaultTermFactory.instance()
				.createConstant("http://to.to/b");
		Term t2 = DefaultTermFactory.instance()
				.createConstant("http://to.to/a");
		Predicate p = new Predicate("http://to.to/p", 2);
		Atom atom1 = new DefaultAtom(p, t1, t2);
		
		atomset.add(atom1);
		
		writer.write(atomset);
		
		FileUtils.deleteDirectory(f);
	}

}
