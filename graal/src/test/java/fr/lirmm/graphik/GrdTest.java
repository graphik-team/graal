/**
 * 
 */
package fr.lirmm.graphik;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Test;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;
import fr.lirmm.graphik.graal.io.grd.GRDParser;

/**
 * @author clement
 *
 */
public class GrdTest {
	
	 @Test
	 public void grdTest() throws Exception {
		GraphOfRuleDependencies g = GRDParser.getInstance().parse(new File("./src/test/resources/univ-bench.grd"));
		return;
	 }
}
