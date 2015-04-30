/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.io.IOException;

import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface GraalWriter extends ConjunctiveQueryWriter, RuleWriter,
		AtomSetWriter {
	
	public GraalWriter DEFAULT = new GraalWriter() {

		@Override
		public void write(AtomSet atomSet) throws IOException {
			System.out.println(atomSet.toString());
		}

		@Override
		public void write(Rule rule) throws IOException {
			System.out.println(rule.toString());
		}

		@Override
		public void writeComment(String string) throws IOException {
			System.out.println("# " + string.toString());
		}

		@Override
		public void write(Prefix prefix) throws IOException {
			System.out.println(prefix.toString());
		}

		@Override
		public void write(ConjunctiveQuery query) throws IOException {
			System.out.println(query.toString());
		}

		@Override
		public void flush() throws IOException {
			System.out.flush();
		}

		@Override
		public void close() throws IOException {
		}
	};

}
