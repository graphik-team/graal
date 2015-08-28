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
package fr.lirmm.graphik.graal.examples;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import fr.lirmm.graphik.graal.backward_chaining.BackwardChainer;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.KnowledgeBase;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.atomset.AtomSetException;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.impl.DefaultKnowledgeBase;
import fr.lirmm.graphik.graal.core.impl.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.Chase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class AnimalsExample {

	private static Scanner scan = new Scanner(System.in);
	private static DlgpWriter writer;

	public static void main(String args[]) throws ChaseException, IOException,
			HomomorphismFactoryException, HomomorphismException, AtomSetException {

		DlgpParser parser = new DlgpParser(new File(
				"./src/main/resources/animals.dlp"));

		KnowledgeBase kb = new DefaultKnowledgeBase(new LinkedListRuleSet(),
				new DefaultInMemoryGraphAtomSet());
		kb.load(parser);

		writer = new DlgpWriter();

		writer.write("\n= Ontology =\n");
		writer.write(kb.getOntology());
		waitEntry();

		writer.write("\n= Facts =\n");
		writer.write(kb.getFacts());
		writer.flush();
		waitEntry();

		writer.write("\n= Query =\n");
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- mammal(X).");
		writer.write(query);
		waitEntry();

		writer.write("\n= Answers =\n");
		Iterable<Substitution> results = StaticHomomorphism.executeQuery(query,
				kb.getFacts());
		if (results.iterator().hasNext()) {
			for (Substitution s : results) {
				writer.write(s.toString());
				writer.write("\n");
			}
		} else {
			writer.write("No answer");
			writer.write("\n");
		}
		writer.flush();
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Backward Chaining
		// /////////////////////////////////////////////////////////////////////////
		writer.write("\n=========================================\n");
		writer.write("= Backward Chaining                     =\n");
		writer.write("=========================================\n");
		writer.flush();
		waitEntry();

		BackwardChainer backwardChainer = new PureRewriter(query,
				kb.getOntology());
		UnionConjunctiveQueries ucq = new UnionConjunctiveQueries();
		while(backwardChainer.hasNext()) {
			ucq.add(backwardChainer.next());
		}

		writer.write("\n= Facts =\n");
		writer.write(kb.getFacts());
		writer.flush();
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Rewritings
		writer.write("\n= Queries Union =\n");
		for (ConjunctiveQuery q : ucq) {
			writer.write(q);
		}
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Backward Chaining Query
		writer.write("\n= Answers =\n");
		results = StaticHomomorphism.executeQuery(ucq, kb.getFacts());
		for (Substitution s : results) {
			writer.write(s.toString());
			writer.write("\n");
		}
		writer.flush();
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Forward Chaining
		// /////////////////////////////////////////////////////////////////////////
		writer.write("\n=========================================\n");
		writer.write("= Forward Chaining                      =\n");
		writer.write("=========================================\n");
		writer.flush();
		waitEntry();

		Chase chase = new NaiveChase(kb.getOntology(), kb.getFacts());
		chase.execute();

		writer.write("\n= Query =\n");
		writer.write(query);
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Saturated database
		writer.write("\n= Facts =\n");
		writer.write(kb.getFacts());
		writer.flush();
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Forward Chaining Query
		writer.write("\n= Answers =\n");
		results = StaticHomomorphism.executeQuery(query, kb.getFacts());
		for (Substitution s : results) {
			writer.write(s.toString());
			writer.write("\n");
		}

		writer.close();
	}

	private static void waitEntry() throws IOException {
		writer.flush();
		scan.nextLine();
	}

}
