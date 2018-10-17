package fr.lirmm.graphik.graal.homomorphism;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.Query;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.Chase;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.forward_chaining.RuleApplier;
import fr.lirmm.graphik.graal.api.homomorphism.Homomorphism;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBaseException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.forward_chaining.BasicChase;
import fr.lirmm.graphik.graal.forward_chaining.rule_applier.DefaultRuleApplier;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.kb.KBBuilderException;
import fr.lirmm.graphik.graal.store.triplestore.rdf4j.RDF4jStore;
import fr.lirmm.graphik.util.stream.IteratorException;
import src.fr.lirmm.graphik.graal.homomorphism.SPARQLHomomorphism;

/**
 * @author Olivier Rodriguez
 */
public class SparqlHomomorphismTest {
	final String DLGP_PREFIX_RDF = "@prefix owl: <http://www.w3.org/2002/07/owl#>";

	KnowledgeBase kbPersons;

	@Test
	public void goodAnswers() throws URISyntaxException, AtomSetException, FileNotFoundException, KBBuilderException,
			KnowledgeBaseException, ChaseException, IteratorException {

		/**
		 * The prefix for the result set associated to an ontology file
		 */
		final String FILE_PREFIX_RESULT = "_result.dlp";

		// We get only the files ontologies/*_result.dlp
		Collection<File> files_result = FileUtils.listFiles(
				new File(SparqlHomomorphismTest.class.getClassLoader().getResource("ontologies/").toURI()),
				new SuffixFileFilter(FILE_PREFIX_RESULT), null);

		Homomorphism<Query, RDF4jStore> homomorphism = SPARQLHomomorphism.instance();
		RuleApplier<Rule, RDF4jStore> ruleApplier = new DefaultRuleApplier<RDF4jStore>(homomorphism);

		for (File file_result : files_result) {
			RDF4jStore store = new RDF4jStore(new SailRepository(new MemoryStore()));
			File file_ontology = new File(StringUtils.removeEnd(file_result.getPath(), FILE_PREFIX_RESULT) + ".dlp");
			KnowledgeBase kb;
			LinkedListAtomSet expectedFactBase;
			LinkedListAtomSet saturatedFactBase;
			{
				KBBuilder builder = new KBBuilder();
				builder.setStore(store);
				builder.addAll(new DlgpParser(file_ontology));
				kb = builder.build();
			}
			{
				KBBuilder builder = new KBBuilder();
				RDF4jStore nstore = new RDF4jStore(new SailRepository(new MemoryStore()));
				builder.setStore(nstore);
				builder.addAll(new DlgpParser(file_result));
				expectedFactBase = new LinkedListAtomSet(builder.build().getFacts());
			}
			Chase chase = new BasicChase<RDF4jStore>(kb.getOntology(), store, ruleApplier);
			chase.execute();

			saturatedFactBase = new LinkedListAtomSet(kb.getFacts());

			assertTrue("Failed on set " + file_ontology + " expected result:\n" + expectedFactBase + "\nHave:\n"
					+ saturatedFactBase, expectedFactBase.equals(saturatedFactBase));
		}
	}
}