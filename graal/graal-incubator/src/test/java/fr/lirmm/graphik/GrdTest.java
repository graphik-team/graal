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
package fr.lirmm.graphik;

import java.io.File;

import org.junit.Test;

import fr.lirmm.graphik.graal.io.grd.GRDParser;

/**
 * @author clement
 *
 */
public class GrdTest {
	
	 @Test
	 public void grdTest() throws Exception {
		GRDParser.getInstance().parse(new File("./src/test/resources/univ-bench.grd"));
		return;
	 }
}
