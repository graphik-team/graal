/**
 * 
 */


import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.kb.core.DefaultConjunctiveQuery;
import fr.lirmm.graphik.obda.parser.ParseException;
import fr.lirmm.graphik.obda.parser.oxford.BasicOxfordQueryParserListener;
import fr.lirmm.graphik.obda.parser.oxford.OxfordQueryParser;

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class OxfordQueryFormatTest {
	
	@Test
	public void testNoParseException() {
		OxfordQueryParser parser = new OxfordQueryParser("Q(?0,?1,?2) <- FinantialInstrument(?0), belongsToCompany(constant,?19), Company(?1)");
		try {
			parser.parse();
		} catch (ParseException e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}
	
	@Test
	public void testNoParseExceptionOnComplicatedQuery() {
		OxfordQueryParser parser = new OxfordQueryParser(" Q(	?0,	?1,? 2		) <- Fin  a-ntial_Inst  rument (?	0   )   , bel1ongs0ToCompany    ( constant   , ?1	9	), Comp3			any(?1)");
		try {
			parser.parse();
		} catch (ParseException e) {
			Assert.assertFalse(e.getMessage(), true);
		}
	}
	
	@Test
	public void testQuery() {
		OxfordQueryParser parser = new OxfordQueryParser("Q(?0) <- FinantialInstrument(?0), belongsToCompany(?0,?1), Company(?1)");
		BasicOxfordQueryParserListener listener = new BasicOxfordQueryParserListener();
		parser.addListener(listener);
		try {
			parser.parse();
		} catch (ParseException e) {
			Assert.assertFalse(e.getMessage(), true);
		}
		
		DefaultConjunctiveQuery cquery = listener.getQuery();
		Assert.assertNotNull("Returned query is null.", cquery);
		Assert.assertEquals("Wrong number of answered variables", 1, cquery.getResponseVariables().size());
	}

}
