/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2016)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.MysqlDriver;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class MultiThreadTest {
	public static void main(String[] args) throws FileNotFoundException, AtomSetException, ChaseException {
		ArrayList<AtomSet> atomsets = new ArrayList<AtomSet>();
		atomsets.add(new DefaultRdbmsStore(new MysqlDriver("localhost", "thread", "root", "root")));
		atomsets.get(0).add(DlgpParser.parseAtom("child(a)."));
		
		LinkedList<Rule> rules = new LinkedList<Rule>();
		rules.add(DlgpParser.parseRule("father(X,Y) :- child(X)."));
		rules.add(DlgpParser.parseRule("mother(X,Y) :- child(X)."));
		rules.add(DlgpParser.parseRule("parent(Y) :- father(X,Y)."));
		rules.add(DlgpParser.parseRule("parent(Y) :- mother(X,Y)."));
		
		/*DlgpParser parser = new DlgpParser(new File("./src/test/resources/lubm-ex-10.dlp"));
		for(Object o : parser) {
			if(o instanceof Rule) {
				rules.add((Rule)o);
			}
		}*/
		
		/*String file = "./src/test/resources/University0_0.owl";
		RDFParser rdfParser = new RDFParser(new FileReader(file));
		ObjectReader<Atom> stream = new RDFPrefixFilter(new RDF2Atom(rdfParser),"http://swat.cse.lehigh.edu/onto/univ-bench.owl#");
		
		atomsets.get(0).addAll(stream);*/
		
		atomsets.add(new DefaultRdbmsStore(new MysqlDriver("localhost", "thread", "root", "root")));
		atomsets.add(new DefaultRdbmsStore(new MysqlDriver("localhost", "thread", "root", "root")));
		atomsets.add(new DefaultRdbmsStore(new MysqlDriver("localhost", "thread", "root", "root")));

		
		//Chase chase = new MultiThreadsChase(rules, atomsets, 4);
		Chase chase = new NaiveChase(rules, atomsets.get(0));
		
		long begin = System.nanoTime();
		System.out.println(begin);
		chase.execute();
		System.out.println(begin);
		System.out.println(System.nanoTime());
		
		int i = 0;
		for(Atom a : atomsets.get(0)) {
			System.out.println(a);
			++i;
		}
		
		System.out.println("nb facts : " + i);
		
	}
}
