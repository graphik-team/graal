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
import java.util.Iterator;
import java.util.Scanner;

import fr.lirmm.graphik.graal.api.backward_chaining.QueryRewriter;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.core.Substitution;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.backward_chaining.pure.PureRewriter;
import fr.lirmm.graphik.graal.core.DefaultUnionOfConjunctiveQueries;
import fr.lirmm.graphik.graal.core.ruleset.LinkedListRuleSet;
import fr.lirmm.graphik.graal.forward_chaining.NaiveChase;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.store.rdbms.DefaultRdbmsStore;
import fr.lirmm.graphik.graal.store.rdbms.driver.HSQLDBDriver;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.GIterator;

public class Example0 {

	private static Scanner scan = new Scanner(System.in);
	private static DlgpWriter writer;

	public static void main(String args[]) throws ChaseException, IOException,
 HomomorphismFactoryException,
	                                      HomomorphismException, AtomSetException {

		// 0 - Create a Dlgp writer and a structure to store rules.
		writer = new DlgpWriter();
		RuleSet ontology = new LinkedListRuleSet();

		// 1 - Create a relational database store with HSQLDB (An InMemory Java
		// database system),
		AtomSet store = new DefaultRdbmsStore(new HSQLDBDriver("test", null));

		// 2 - Parse Animals.dlp (A Dlgp file with rules and facts)
		DlgpParser dlgpParser = new DlgpParser(new File("./src/main/resources/animals.dlp"));
		while (dlgpParser.hasNext()) {
			Object o = dlgpParser.next();
			if (o instanceof Atom) {
				store.add((Atom) o);
			}
			if (o instanceof Rule) {
				ontology.add((Rule) o);
			}
		}

		// 3 - Print the set of rules in Dlgp
		writer.write("\n= Ontology =\n");
		writer.write(ontology);

		// 4 - Print the set of facts in Dlgp
		writer.write("\n= Facts =\n");
		writer.write(store);
		waitEntry();

		writer.write("\n=========================================\n");
		writer.write("=        Basic Query Answering          =\n");
		writer.write("=========================================\n");

		// 5 - Parse a query from the code and print it
		writer.write("\n= Query =\n");
		ConjunctiveQuery query = DlgpParser.parseQuery("?(X) :- mammal(X).");
		writer.write(query);

		// 6 - Query the store without reasoning
		writer.write("\n= Answers =\n");
		CloseableIterator<Substitution> results = StaticHomomorphism.instance().execute(query, store);
		printAnswers(results);
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Backward Chaining
		// /////////////////////////////////////////////////////////////////////////

		writer.write("\n=========================================\n");
		writer.write("=           Backward Chaining           =\n");
		writer.write("=========================================\n");

		// 7 - Rewrite the original query (backward chaining) in an union of
		// queries
		QueryRewriter rewriter = new PureRewriter();
		GIterator<ConjunctiveQuery> it = rewriter.execute(query, ontology);
		DefaultUnionOfConjunctiveQueries ucq = new DefaultUnionOfConjunctiveQueries(it);

		// Print the set of facts in Dlgp
		writer.write("\n= Facts =\n");
		writer.write(store);

		// Print the set of rewritings
		writer.write("\n= Union of queries =\n");
		writer.write(ucq);

		// Query data with the union of queries
		writer.write("\n= Answers =\n");
		results = StaticHomomorphism.instance().execute(ucq, store);
		printAnswers(results);
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Forward Chaining
		// /////////////////////////////////////////////////////////////////////////
		writer.write("\n=========================================\n");
		writer.write("=           Forward Chaining            =\n");
		writer.write("=========================================\n");

		// 8 - Apply a naive chase (forward chaining) on data
		Chase chase = new NaiveChase(ontology, store);
		chase.execute();

		writer.write("\n= Query =\n");
		writer.write(query);

		// Print the saturated database
		writer.write("\n= Facts =\n");
		writer.write(store);
		writer.flush();

		// Query saturated data with the original query
		writer.write("\n= Answers =\n");
		results = StaticHomomorphism.instance().execute(query, store);
		while (results.hasNext()) {
			writer.write(results.next().toString());
			writer.write("\n");
		}

		// Close the Dlgp writer
		writer.close();
	}

	private static void printAnswers(Iterator<Substitution> results) throws IOException {
		if (results.hasNext()) {
			while (results.hasNext()) {
				writer.write(results.next().toString());
				writer.write("\n");
			}
		} else {
			writer.write("No answer\n");
		}
	}

	private static void waitEntry() throws IOException {
		writer.write("\n<PRESS ENTER TO CONTINUE>");
		writer.flush();
		scan.nextLine();
	}

}
