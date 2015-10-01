/*
 * Copyright (C) Inria Sophia Antipolis - Méditerranée / LIRMM
 * (Université de Montpellier & CNRS) (2014 - 2015)
 *
 * Contributors :
 *
 * Clément SIPIETER <clement.sipieter@inria.fr>
 * Mélanie KÖNIG
 * Swan ROCHER
 * Jean-François BAGET
 * Michel LECLÈRE
 * Marie-Laure MUGNIER <mugnier@lirmm.fr>
 *
 *
 * This file is part of Graal <https://graphik-team.github.io/graal/>.
 *
 * This software is governed by the CeCILL  license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 */
 /**
 * 
 */
package fr.lirmm.graphik.graal.io.oxford;
import org.junit.Assert;
import org.junit.Test;

import fr.lirmm.graphik.graal.api.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.api.io.ParseException;
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
