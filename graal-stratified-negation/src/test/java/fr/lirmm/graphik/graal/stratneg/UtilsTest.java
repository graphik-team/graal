package fr.lirmm.graphik.graal.stratneg;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import fr.lirmm.graphik.graal.api.core.Atom;
import fr.lirmm.graphik.graal.api.core.AtomSet;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.core.RuleSet;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.forward_chaining.SccChase;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.kb.KBBuilder;
import fr.lirmm.graphik.graal.kb.KBBuilderException;
import fr.lirmm.graphik.util.stream.CloseableIterator;
import fr.lirmm.graphik.util.stream.IteratorException;

public class UtilsTest {

  static RuleSet buildRuleSet() throws ParseException, KBBuilderException {

    KBBuilder kbb = new KBBuilder();

    kbb.add(Utils.parseRule(
        "garantie_non_souscrite(X) :- u(X, A, \"garantie\"), u(X, B, \"non\"), u(X, C, \"souscrite\")."));
    kbb.add(Utils.parseRule(
        "garantie_souscrite(X) :- u(X, A, \"garantie\"), u(X, B, \"souscrite\"), not_garantie_non_souscrite(X)."));

    return kbb.build().getOntology();
  }

  static AtomSet buildAtomSet() throws ParseException, KBBuilderException {

    KBBuilder kbb = new KBBuilder();

    kbb.add(DlgpParser.parseAtom("u(1, 1, \"garantie\")."));
    kbb.add(DlgpParser.parseAtom("u(1, 2, \"non\")."));
    kbb.add(DlgpParser.parseAtom("u(1, 3, \"souscrite\")."));
    kbb.add(DlgpParser.parseAtom("u(2, 1, \"garantie\")."));
    kbb.add(DlgpParser.parseAtom("u(2, 2, \"souscrite\")."));

    return kbb.build().getFacts();
  }

  @Test
  public void testParseRule() throws ParseException, KBBuilderException {
    assertEquals(2, buildRuleSet().size());
  }

  @Test
  public void testParseAtom() throws ParseException, KBBuilderException, AtomSetException {
    assertEquals(5, buildAtomSet().size());
  }

  @Test
  public void testSaturation() throws KBBuilderException, ChaseException, IteratorException {

    AtomSet atomSet = buildAtomSet();
    LabeledGraphOfRuleDependencies grd = new LabeledGraphOfRuleDependencies(buildRuleSet());
    SccChase<AtomSet> chase = new SccChase<>(grd, atomSet, new RuleApplierWithNegation<>());
    chase.execute();

    Set<String> result = new HashSet<>();

    try (CloseableIterator<Atom> iterator = atomSet.iterator()) {
      while (iterator.hasNext()) {
        result.add(iterator.next().toString());
      }
    }

    assertEquals(5 + 2, result.size());

    assertTrue(result.contains("u\\3(\"2\",\"1\",\"garantie\")"));
    assertTrue(result.contains("u\\3(\"1\",\"3\",\"souscrite\")"));
    assertTrue(result.contains("u\\3(\"1\",\"1\",\"garantie\")"));
    assertTrue(result.contains("u\\3(\"1\",\"2\",\"non\")"));
    assertTrue(result.contains("u\\3(\"2\",\"2\",\"souscrite\")"));

    assertTrue(result.contains("garantie_non_souscrite\\1(\"1\")"));
    assertTrue(result.contains("garantie_souscrite\\1(\"2\")"));
  }
}
