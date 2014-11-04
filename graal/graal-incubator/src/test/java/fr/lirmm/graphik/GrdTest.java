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
