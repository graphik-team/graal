package fr.lirmm.graphik.graal.stratneg;

import static org.junit.Assert.assertFalse;
import org.junit.Test;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.kb.KBBuilderException;

public class LabeledGraphOfRuleDependenciesTest {

  @Test
  public void testHasCircuitWithNegativeEdge() throws ParseException, KBBuilderException {
    LabeledGraphOfRuleDependencies grd =
        new LabeledGraphOfRuleDependencies(UtilsTest.buildRuleSet());
    assertFalse(grd.hasCircuitWithNegativeEdge());
  }
}
