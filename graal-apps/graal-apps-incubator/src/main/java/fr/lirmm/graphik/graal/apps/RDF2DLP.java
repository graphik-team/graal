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
 package fr.lirmm.graphik.graal.apps;

import java.io.FileReader;
import java.io.IOException;

import org.openrdf.rio.RDFFormat;

import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;
import fr.lirmm.graphik.graal.io.rdf.RDF2Atom;
import fr.lirmm.graphik.graal.io.rdf.RDFParser;

/**
 * 
 */

/**
 * @author Clément Sipieter (INRIA) <clement@6pi.fr>
 * 
 */
public final class RDF2DLP {

	private RDF2DLP(){}

	public static void main(final String[] args) throws IOException {
		if(args.length == 0)
			System.out.println("give me a RDF file path.");
		
		RDFParser parser = new RDFParser(new FileReader(args[0]), RDFFormat.RDFXML);
		DlgpWriter writer = new DlgpWriter();
		
		writer.write(new RDF2Atom(parser));
		writer.close();
	}
}
