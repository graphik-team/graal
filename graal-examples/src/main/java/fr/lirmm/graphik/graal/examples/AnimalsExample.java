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
package fr.lirmm.graphik.graal.examples;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import fr.lirmm.graphik.graal.api.backward_chaining.QueryRewriter;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.KnowledgeBase;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.DefaultKnowledgeBase;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.core.atomset.graph.DefaultInMemoryGraphAtomSet;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.DefaultChase;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.GIterator;

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
		CloseableIterator<Substitution> results = StaticHomomorphism.instance().execute(query,
				kb.getFacts());
		if (results.hasNext()) {
			while (results.hasNext()) {
				writer.write(results.next().toString());
				writer.write("\n");
			}
		} else {
			writer.write("No answer");
			writer.write("\n");
		}
		results.close();
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

		QueryRewriter rewriter = new PureRewriter();
		GIterator<ConjunctiveQuery> it = rewriter.execute(query, kb.getOntology());
		DefaultUnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(it);

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
		results = StaticHomomorphism.instance().execute(ucq, kb.getFacts());
		while (results.hasNext()) {
			writer.write(results.next().toString());
			writer.write("\n");
		}
		results.close();
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

		Chase chase = new DefaultChase(kb.getOntology(), kb.getFacts());
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
		results = StaticHomomorphism.instance().execute(query, kb.getFacts());
		while (results.hasNext()) {
			writer.write(results.next().toString());
			writer.write("\n");
		}
		results.close();

		writer.close();
	}

	private static void waitEntry() throws IOException {
		writer.flush();
		scan.nextLine();
	}

}
