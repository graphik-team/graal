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
 /**
 * 
 */
package fr.lirmm.graphik.graal.test;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.forward_chaining.Chase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
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
