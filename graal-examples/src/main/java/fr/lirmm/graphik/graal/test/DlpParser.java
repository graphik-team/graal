/**
 * 
 */
package fr.lirmm.graphik.graal.test;

import fr.lirmm.graphik.graal.io.dlgp.DlgpParser;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class DlpParser {

	public static void main(String[] args) {
		DlgpParser p = new DlgpParser("p(a,b).");
		
		for(Object o : p) {
			System.out.println(o);
		}
	}
	
}
