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
 package fr.lirmm.graphik.graal.trash;
import java.io.FileReader;

import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 *
 */
public class RdfConvertorTest {
	public static void main(String[] args)
			throws Exception
		{
			final RDFParser rdfParser = Rio.createParser(RDFFormat.RDFXML);
			final RDFWriter rdfWriter = Rio.createWriter(RDFFormat.RDFXML, System.out);
			
			rdfWriter.startRDF();
			rdfParser.setRDFHandler(new RDFHandlerBase() {
				@Override
				public void handleStatement(Statement st) throws RDFHandlerException {
					rdfWriter.handleStatement(st);
				}
			}
			);
			rdfParser.parse(new FileReader("/home/clement/projets/ontologies/test.owl"), "");
			rdfWriter.endRDF();
		}
}
