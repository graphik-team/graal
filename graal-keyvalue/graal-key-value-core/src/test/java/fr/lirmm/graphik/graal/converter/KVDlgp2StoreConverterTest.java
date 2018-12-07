package fr.lirmm.graphik.graal.converter;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBaseException;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.homomorphism.PureHomomorphism;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.kb.KBBuilderException;
import fr.lirmm.graphik.util.stream.CloseableIteratorRecursive;
import fr.lirmm.graphik.util.stream.IteratorException;
import fr.lirmm.graphik.util.stream.converter.ConverterIterator;

/**
 * @author Olivier Rodriguez
 */
public class KVDlgp2StoreConverterTest {

	/**
	 * This test check all different ontology files in the directory
	 * resources/ontologies/*. It load the ontology with the fact base from file
	 * $file.dlp, do the saturation with built-in predicates and check at final if
	 * the result is equal with the file $file_result.dlp.
	 */
	@Test
	public void goodAnswers() throws URISyntaxException, FileNotFoundException, KBBuilderException, IteratorException,
			ChaseException, KnowledgeBaseException, HomomorphismException {

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

			// Build the fact base with its associated ontology
			{
				KBBuilder builder = new KBBuilder();
				DlgpParser parser = new DlgpParser(file_ontology);
				CloseableIteratorRecursive<Object> values = new CloseableIteratorRecursive<>(
						new ConverterIterator<>(parser, new KVDlgp2StoreConverter()));
				builder.addAll(values);
				kb = builder.build();
			}

			// Build the expected fact base
			{
				KBBuilder builder = new KBBuilder();
				DlgpParser parser = new DlgpParser(file_result);
				CloseableIteratorRecursive<Object> values = new CloseableIteratorRecursive<>(
						new ConverterIterator<>(parser, new KVDlgp2StoreConverter()));
				builder.addAll(values);
				expectedFactBase = new LinkedListAtomSet(builder.build().getFacts());
			}
			kb.saturate();

			saturatedFactBase = new LinkedListAtomSet(kb.getFacts());
			PureHomomorphism pure = PureHomomorphism.instance();

			assertTrue("Failed on set " + file_ontology + " expected result:\n" + expectedFactBase + "\nHave:\n"
					+ saturatedFactBase, pure.exist(saturatedFactBase, expectedFactBase));
		}
	}
}
