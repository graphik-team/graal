/**
 * 
 */
package fr.lirmm.graphik.alaska.examples;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import fr.lirmm.graphik.graal.backward_chaining.BackwardChainer;
import fr.lirmm.graphik.graal.backward_chaining.PureRewriter;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.DefaultKnowledgeBase;
import fr.lirmm.graphik.graal.core.KnowledgeBase;
import fr.lirmm.graphik.graal.core.Substitution;
import fr.lirmm.graphik.graal.core.UnionConjunctiveQueries;
import fr.lirmm.graphik.graal.forward_chaining.Chase;
import fr.lirmm.graphik.graal.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.forward_chaining.DefaultChase;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.homomorphism.HomomorphismFactoryException;
import fr.lirmm.graphik.graal.homomorphism.StaticHomomorphism;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlgp.DlgpWriter;
import fr.lirmm.graphik.graal.rulesetanalyser.RuleAnalyser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 * 
 */
public class AnimalsExample {

	private static String filePath = "./src/main/resources/animals.dlp";
	private static DlgpWriter writer = new DlgpWriter();

	public static void main(String args[]) throws ChaseException, IOException,
			HomomorphismFactoryException, HomomorphismException {
		KnowledgeBase kb = new DefaultKnowledgeBase();

		Reader reader = new FileReader(filePath);
		DlgpParser.parseKnowledgeBase(reader, kb);

		writer.write("\n= Ontology =\n");
		writer.write(kb.getRuleSet());
		waitEntry();

		writer.write("\n= Facts =\n");
		writer.write(kb.getAtomSet());
		writer.flush();
		waitEntry();

		writer.write("\n= Query =\n");
		ConjunctiveQuery query = DlgpParser
				.parseQuery("?(X) :- \"mammifère\"(X).");
		writer.write(query);
		waitEntry();

		writer.write("\n= Answers =\n");
		Iterable<Substitution> results = StaticHomomorphism.executeQuery(query,
				kb.getAtomSet());
		for (Substitution s : results) {
			writer.write(s.toString());
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
				kb.getRuleSet());
		UnionConjunctiveQueries ucq = new UnionConjunctiveQueries();
		{
			ConjunctiveQuery q;
			while (backwardChainer.hasNext()) {
				q = backwardChainer.next();
				ucq.add(q);
			}
		}

		// /////////////////////////////////////////////////////////////////////////
		// Rewritings
		writer.write("\n= Queries Union =\n");
		for (ConjunctiveQuery q : ucq) {
			writer.write(q);
		}
		waitEntry();

		writer.write("\n= Facts =\n");
		writer.write(kb.getAtomSet());
		writer.flush();
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Backward Chaining Query
		writer.write("\n= Answers =\n");
		results = StaticHomomorphism.executeQuery(ucq, kb.getAtomSet());
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

		Chase chase = new DefaultChase(kb.getRuleSet(), kb.getAtomSet());
		chase.execute();

		writer.write("\n= Query =\n");
		writer.write(query);
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Saturated database
		writer.write("\n= Facts =\n");
		writer.write(kb.getAtomSet());
		writer.flush();
		waitEntry();

		// /////////////////////////////////////////////////////////////////////////
		// Forward Chaining Query
		writer.write("\n= Answers =\n");
		results = StaticHomomorphism.executeQuery(query, kb.getAtomSet());
		for (Substitution s : results) {
			writer.write(s.toString());
			writer.write("\n");
		}
		writer.flush();
		waitEntry();

		writer.write("\n=========================================\n");
		writer.write("= Ontology Analysis                     =\n");
		writer.write("=========================================\n");

		RuleAnalyser ra = new RuleAnalyser(kb.getRuleSet());
		writer.write(ra.toString());
		writer.flush();

	}

	private static Scanner scan = new Scanner(System.in);

	public static void waitEntry() {
		scan.nextLine();
	}

}
