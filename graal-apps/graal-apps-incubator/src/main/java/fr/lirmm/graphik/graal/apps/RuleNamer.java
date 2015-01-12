/**
 * 
 */
package fr.lirmm.graphik.graal.apps;

import java.io.File;
import java.io.IOException;

import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.io.dlp.DlpParser;
import fr.lirmm.graphik.graal.io.dlp.DlpWriter;

/**
 * @author clement
 *
 */
public class RuleNamer {
	
	public static void main(String[] args) throws IOException {
		DlpParser parser;
		DlpWriter writer;
		if(args.length >= 1) {
			parser = new DlpParser(new File(args[0]));
			if(args.length >= 2) {
				writer = new DlpWriter(new File(args[1]));
			} else {
				writer = new DlpWriter(System.out);
			}
		} else {
			parser = new DlpParser(System.in);
			writer = new DlpWriter(System.out);
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
