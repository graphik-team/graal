package fr.lirmm.graphik.graal.homomorphism;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.InMemoryAtomSet;
import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBase;
import fr.lirmm.graphik.graal.api.kb.KnowledgeBaseException;
import fr.lirmm.graphik.graal.core.Rules;
import fr.lirmm.graphik.graal.core.atomset.LinkedListAtomSet;
import fr.lirmm.graphik.graal.core.compilation.IDCompilation;
import fr.lirmm.graphik.graal.forward_chaining.BasicChase;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.kb.KBBuilderException;
import fr.lirmm.graphik.util.stream.IteratorException;

public class SaturationWithCompilationTest {
	final String DLGP_PREFIX_RDF = "@prefix owl: <http://www.w3.org/2002/07/owl#>";

	KnowledgeBase kbPersons;

	@BeforeClass
	public static void setUp() {
	}

	private boolean haveIsomorphism(InMemoryAtomSet a, InMemoryAtomSet b) throws HomomorphismException {
		PureHomomorphism pure = PureHomomorphism.instance();
		return pure.exist(a, b) && pure.exist(b, a);
	}

	/**
	 * This test check all different ontology files in the directory
	 * resources/ontologies/*. It load the ontology with the fact base from file
	 * $file.dlp, do the saturation with built-in predicates and check at final if
	 * the result is equal with the file $file_result.dlp.
	 * 
	 * @throws KBBuilderException
	 * @throws FileNotFoundException
	 * @throws IteratorException
	 * @throws ChaseException
	 * @throws URISyntaxException
	 * @throws HomomorphismException
	 * @throws KnowledgeBaseException
	 */
	@Test
	public void goodAnswers() throws FileNotFoundException, KBBuilderException, IteratorException, ChaseException,
			URISyntaxException, HomomorphismException, KnowledgeBaseException {

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
				builder.addAll(new DlgpParser(file_ontology));
				kb = builder.build();
			}
			// Build the expected fact base
			{
				KBBuilder builder = new KBBuilder();
				builder.addAll(new DlgpParser(file_result));
				expectedFactBase = new LinkedListAtomSet(builder.build().getFacts());
			}
			kb.saturate();
			saturatedFactBase = new LinkedListAtomSet(kb.getFacts());

			assertTrue("Failed on set " + file_ontology + " expected result:\n" + expectedFactBase + "\nHave:\n"
					+ saturatedFactBase, haveIsomorphism(expectedFactBase, saturatedFactBase));
		}
	}

	@Test
	public void goodCompileAnswers() throws FileNotFoundException, KBBuilderException, IteratorException, ChaseException,
			URISyntaxException, HomomorphismException {

		/**
		 * The prefix for the result set associated to an ontology file
		 */
		final String FILE_PREFIX_RESULT = "_compile-result.dlp";

		// We get only the files ontologies/*_result.dlp
		Collection<File> files_result = FileUtils.listFiles(
				new File(getClass().getClassLoader().getResource("ontologies/").toURI()),
				new SuffixFileFilter(FILE_PREFIX_RESULT), null);

		for (File file_result : files_result) {
			File file_ontology = new File(StringUtils.removeEnd(file_result.getPath(), FILE_PREFIX_RESULT) + ".dlp");
			KnowledgeBase kb;
			LinkedListAtomSet expectedFactBase;
			LinkedListAtomSet saturatedFactBase;
			IDCompilation idCompilation = new IDCompilation();

			// Build the fact base with its associated ontology
			{
				KBBuilder builder = new KBBuilder();
				List<Rule> rules = new ArrayList<>();

				try (DlgpParser it = new DlgpParser(file_ontology);) {

					while (it.hasNext()) {
						Object o = it.next();

						if (o instanceof Rule)
							rules.addAll(Rules.computeAtomicHead((Rule) o));
						else if (o instanceof Atom)
							builder.add((Atom) o);
						else
							throw new InvalidParameterException();
					}
				}
				rules.size();
				idCompilation.compile(rules.iterator());

				for (Rule r : rules)
					builder.add(r);

				kb = builder.build();
			}
			// Build the expected fact base
			{
				KBBuilder builder = new KBBuilder();
				builder.addAll(new DlgpParser(file_result));
				expectedFactBase = new LinkedListAtomSet(builder.build().getFacts());
			}
			BasicChase<? extends AtomSet> bf = new BasicChase<AtomSet>(kb.getOntology(), kb.getFacts(),
					idCompilation);

			bf.execute();
			saturatedFactBase = new LinkedListAtomSet(kb.getFacts());

			assertTrue("Failed on set " + file_ontology + " expected result:\n" + expectedFactBase + "\nHave:\n"
					+ saturatedFactBase, haveIsomorphism(expectedFactBase, saturatedFactBase));
		}
	}
}