/**
 * 
 */
package fr.lirmm.graphik.graal.io;

import java.io.IOException;

import fr.lirmm.graphik.graal.core.Atom;
import fr.lirmm.graphik.graal.core.ConjunctiveQuery;
import fr.lirmm.graphik.graal.core.Rule;
import fr.lirmm.graphik.graal.core.atomset.AtomSet;
import fr.lirmm.graphik.util.Prefix;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public interface GraalWriter extends ConjunctiveQueryWriter, RuleWriter,
		AtomSetWriter, AtomWriter {

	public GraalWriter DEFAULT = new GraalWriter() {

		@Override
		public void write(Atom atom) throws IOException {
			System.out.println(atom);
		}

		@Override
		public void write(AtomSet atomSet) throws IOException {
			System.out.println(atomSet);
		}

		@Override
		public void write(Rule rule) throws IOException {
			System.out.println(rule);
		}

		@Override
		public void writeComment(String string) throws IOException {
			System.out.println("# " + string);
		}

		@Override
		public void write(Prefix prefix) throws IOException {
			System.out.println(prefix);
		}

		@Override
		public void write(ConjunctiveQuery query) throws IOException {
			System.out.println(query);
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
