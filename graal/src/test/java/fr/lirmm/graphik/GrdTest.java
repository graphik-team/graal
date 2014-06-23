/**
 * 
 */
package fr.lirmm.graphik;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.junit.Test;

import fr.lirmm.graphik.alaska.grd.GraphOfRuleDependencies;
import fr.lirmm.graphik.kb.core.Rule;
import fr.lirmm.graphik.obda.io.dlgp.DlgpParser;

/**
 * @author clement
 *
 */
public class GrdTest {
	
	 @Test
	 public void grdTest() throws Exception {
		GraphOfRuleDependencies g = new GraphOfRuleDependencies();
		DlgpParser parser = new DlgpParser(new FileReader("./src/test/resources/univ-bench.dlp"));
		for(Object o : parser) {
			if(o instanceof Rule) {
				g.addRule((Rule)o);
			}
		}
		g.parseGrd(new BufferedReader(new FileReader("./src/test/resources/univ-bench.grd")));
		return;
	 }
}
