package fr.lirmm.graphik.graal.rulesetanalyser;

import java.util.Set;

import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.Rule;
import fr.lirmm.graphik.graal.api.core.RuleSetException;
import fr.lirmm.graphik.graal.api.io.ParseException;
import fr.lirmm.graphik.graal.core.grd.DefaultGraphOfRuleDependencies;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.rulesetanalyser.util.AnalyserRuleSet;
import fr.lirmm.graphik.util.stream.ArrayCloseableIterator;
import org.junit.Assert;


public class AnalyserRuleSetTest {

	@Test
	public void getGRD() throws RuleSetException, ParseException {
		// assume
		Rule r1 = DlgpParser.parseRule("q(X) :- p(X).");
		Rule r2 = DlgpParser.parseRule("r(X) :- q(X).");
		AnalyserRuleSet analyserRuleSet = new AnalyserRuleSet(new ArrayCloseableIterator<Rule>(r1, r2));
		System.out.println(analyserRuleSet);
		
		// when
		DefaultGraphOfRuleDependencies grd = analyserRuleSet.getGraphOfRuleDependencies();
		System.out.println(grd);
		// then
		Set<Rule> triggeredRules = grd.getTriggeredRules(r1);
		Assert.assertEquals(1, triggeredRules.size());
		Assert.assertTrue(triggeredRules.contains(r2));

	}

}
