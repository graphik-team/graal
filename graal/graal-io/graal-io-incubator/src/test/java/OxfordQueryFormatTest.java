/* Graal v0.7.4
 * Copyright (c) 2014-2015 Inria Sophia Antipolis - Méditerranée / LIRMM (Université de Montpellier & CNRS)
 * All rights reserved.
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * Author(s): Clément SIPIETER
 *            Mélanie KÖNIG
 *            Swan ROCHER
 *            Jean-François BAGET
 *            Michel LECLÈRE
 *            Marie-Laure MUGNIER
 */
 /**
 * 
 */

import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.io.ParseException;
import fr.lirmm.graphik.graal.io.oxford.OxfordQueryParser;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public class OxfordQueryFormatTest {

	@Test
	public void testNoParseException() {
		ConjunctiveQuery cquery;
		try {
			cquery = OxfordQueryParser
					.parseQuery("Q(?0,?1,?2) <- FinantialInstrument(?0), belongsToCompany(constant,?19), Company(?1)");
			Assert.assertNotNull("Returned query is null.", cquery);
			Assert.assertEquals("Wrong number of answered variables", 3, cquery
					.getAnswerVariables().size());
		} catch (ParseException e) {
			Assert.assertFalse("Parse error:" + e, true);
		}
	}

	@Test
	public void testNoParseExceptionOnComplicatedQuery() {
		ConjunctiveQuery cquery;
		try {
			cquery = OxfordQueryParser
					.parseQuery(" Q(	?0,	?1,? 2		) <- Fin  a-ntial_Inst  rument (?	0   )   , bel1ongs0ToCompany    ( constant   , ?1	9	), Comp3			any(?1)");
			Assert.assertNotNull("Returned query is null.", cquery);
			Assert.assertEquals("Wrong number of answered variables", 3, cquery
					.getAnswerVariables().size());
		} catch (ParseException e) {
			Assert.assertFalse("Parse error:" + e, true);
		}
	}

	@Test
	public void testQuery() {
		ConjunctiveQuery cquery;
		try {
			cquery = OxfordQueryParser
					.parseQuery("Q(?0) <- FinantialInstrument(?0), belongsToCompany(?0,?1), Company(?1)");
			Assert.assertNotNull("Returned query is null.", cquery);
			Assert.assertEquals("Wrong number of answered variables", 1, cquery
					.getAnswerVariables().size());
		} catch (ParseException e) {
			Assert.assertFalse("Parse error:" + e, true);
		}
	}

}
