package fr.lirmm.graphik.graal.homomorphism;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.util.ClassicBuiltInPredicates;
import fr.lirmm.graphik.graal.converter.Object2RuleWithBuiltInPredicateConverter;
import fr.lirmm.graphik.graal.core.DefaultBuiltInPredicateSet;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.forward_chaining.BreadthFirstChase;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.RuleWithBuiltInPredicateApplier;
import fr.lirmm.graphik.graal.homomorphism.checker.ConjunctiveQueryWithBuiltInPredicatesChecker;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.kb.KBBuilderException;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.converter.ConverterIterator;

public class ConjunctiveQueryWBPHomomorphismTest {
	final String DLGP_PREFIX_RDF = "@prefix owl: <http://www.w3.org/2002/07/owl#>";

	KnowledgeBase kbPersons;

	@BeforeClass
	public static void setUp() {
		SmartHomomorphism.instance().addChecker(ConjunctiveQueryWithBuiltInPredicatesChecker.instance());
	}

	/**
	 * This test check all different ontology files in the directory
	 * resources/ontologies/*. It load the ontology with the fact base from file
	 * $file.dlp, do the saturation with built-in predicates and check at final if
	 * the result is equal with the file $file_result.dlp.
	 */
	@Test
	public void goodAnswers()
			throws URISyntaxException, FileNotFoundException, KBBuilderException, IteratorException, ChaseException {

		/**
		 * The prefix for the result set associated to an ontology file
		 */
		final String FILE_PREFIX_RESULT = "_result.dlp";

		// We get only the files ontologies/*_result.dlp
		Collection<File> files_result = FileUtils.listFiles(
				new File(getClass().getClassLoader().getResource("ontologies/").toURI()),
				new SuffixFileFilter(FILE_PREFIX_RESULT), null);

		for (File file_result : files_result) {
			File file_ontology = new File(StringUtils.removeEnd(file_result.getPath(), FILE_PREFIX_RESULT) + ".dlp");
			KnowledgeBase kb;
			LinkedListAtomSet expectedFactBase;
			LinkedListAtomSet saturatedFactBase;

			DefaultBuiltInPredicateSet btpredicates = new DefaultBuiltInPredicateSet(
					ClassicBuiltInPredicates.owlPredicates());

			// Build the fact base with its associated ontology
			{
				KBBuilder builder = new KBBuilder();
				DlgpParser parser = new DlgpParser(file_ontology);
				ConverterIterator<Object, Object> converter = new ConverterIterator<>(parser,
						new Object2RuleWithBuiltInPredicateConverter(btpredicates));
				builder.addAll(converter);
				kb = builder.build();
			}

			// Build the expected fact base
			{
				KBBuilder builder = new KBBuilder();
				builder.addAll(new DlgpParser(file_result));
				expectedFactBase = new LinkedListAtomSet(builder.build().getFacts());
			}

			RuleApplier<Rule, AtomSet> applier = new RuleWithBuiltInPredicateApplier<AtomSet>();
			BreadthFirstChase bf = new BreadthFirstChase(kb.getOntology(), kb.getFacts(), applier);
			bf.execute();

			saturatedFactBase = new LinkedListAtomSet(kb.getFacts());

			assertTrue("Failed on set " + file_ontology + " expected result:\n" + expectedFactBase + "\nHave:\n"
					+ saturatedFactBase, expectedFactBase.equals(saturatedFactBase));
		}
	}
}