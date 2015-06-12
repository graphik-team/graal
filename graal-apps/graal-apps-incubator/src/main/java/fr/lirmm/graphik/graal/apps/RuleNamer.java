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
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.io.dlp.DlgpParser;
import fr.lirmm.graphik.graal.io.dlp.DlgpWriter;

/**
 * @author clement
 *
 */
public class RuleNamer {
	
	public static void main(String[] args) throws IOException {
		DlgpParser parser;
		DlgpWriter writer;
		if(args.length >= 1) {
			parser = new DlgpParser(new File(args[0]));
			if(args.length >= 2) {
				writer = new DlgpWriter(new File(args[1]));
			} else {
				writer = new DlgpWriter(System.out);
			}
		} else {
			parser = new DlgpParser(System.in);
			writer = new DlgpWriter(System.out);
		}
		
		int objectNumber = 0;
		
		try{
			for(Object o : parser) {
				if(o instanceof Rule) {
					Rule rule = (Rule)o;
					if(rule.getLabel().isEmpty()) {
						rule.setLabel("R"+ objectNumber);
					}
					writer.write(rule);
					++objectNumber;
				} 
			}
		} catch(IOException e) {
			writer.close();
			throw e;
		}
		
		writer.close();
	}

}
