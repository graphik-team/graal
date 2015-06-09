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
package fr.lirmm.graphik.graal.io;

import java.io.IOException;
import java.io.Writer;

/**
 * @author Clément Sipieter (INRIA) {@literal <clement@6pi.fr>}
 *
 */
public class AbstractWriter {

	private Writer writer;

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	public AbstractWriter(Writer out) {
		this.writer = out;
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	public void close() throws IOException {
		this.writer.close();
	}

	public void flush() throws IOException {
		this.writer.flush();
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// //////////////////////////////////////////////////////////////////////////

	protected void write(char c) throws IOException {
		this.writer.write(c);
	}

	protected void write(Character c) throws IOException {
		this.writer.write(c);
	}

	protected void write(String str) throws IOException {
		this.writer.write(str);
	}

	protected void writeln(char c) throws IOException {
		this.writer.write(c);
		this.writer.write('\n');
	}

	protected void writeln(Character c) throws IOException {
		this.writer.write(c);
		this.writer.write('\n');
	}

	protected void writeln(String str) throws IOException {
		this.writer.write(str);
		this.writer.write('\n');
	}
}
